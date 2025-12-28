package com.kb.trading.service.impl;
import com.kb.trading.entity.*;
import com.kb.trading.repository.OrderItemRepository;
import com.kb.trading.repository.OrderRepository;
import com.kb.trading.repository.ProductRepository;
import com.kb.trading.repository.UserRepository;
import com.kb.trading.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Order createOrder(Long buyerId, List<OrderItem> items,
                             String address, String phone, String note) {
        // 1. 验证买家
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("买家不存在"));

        if (items == null || items.isEmpty()) {
            throw new RuntimeException("订单商品不能为空");
        }

        // 2. 计算订单总金额，并验证商品
        BigDecimal totalAmount = BigDecimal.ZERO;
        Long sellerId = null;
        String sellerName = null;

        for (OrderItem item : items) {
            // 验证商品
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("商品不存在: " + item.getProductId()));

            // 验证商品状态
            if (product.getStatus() != 1) {
                throw new RuntimeException("商品已下架或售出: " + product.getTitle());
            }

            // 验证库存
            if (item.getQuantity() > product.getStock()) {
                throw new RuntimeException("商品库存不足: " + product.getTitle());
            }

            // 设置商品信息
            item.setProductTitle(product.getTitle());
            item.setProductImage(product.getMainImage());
            item.setUnitPrice(product.getPrice());
            item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

            // 累加总金额
            totalAmount = totalAmount.add(item.getSubtotal());

            // 记录卖家信息（假设一个订单只从一个卖家购买）
            sellerId = product.getSellerId();
            sellerName = product.getSellerName();
        }

        // 3. 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setBuyerId(buyerId);
        order.setBuyerName(buyer.getNickname());
        order.setSellerId(sellerId);
        order.setSellerName(sellerName);
        order.setTotalAmount(totalAmount);
        order.setPayableAmount(totalAmount); // 暂时没有优惠
        order.setBuyerAddress(address);
        order.setBuyerPhone(phone);
        order.setBuyerNote(note);
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        // 4. 保存订单
        Order savedOrder = orderRepository.save(order);

        // 5. 保存订单项并扣减库存
        for (OrderItem item : items) {
            item.setOrderId(savedOrder.getId());
            orderItemRepository.save(item);

            // 扣减商品库存
            Product product = productRepository.findById(item.getProductId()).get();
            product.setStock(product.getStock() - item.getQuantity());
            if (product.getStock() <= 0) {
                product.setStatus(2); // 标记为售罄
            }
            productRepository.save(product);
        }

        log.info("订单创建成功: 订单号={}, 买家={}, 总金额={}",
                order.getOrderNo(), buyer.getUsername(), totalAmount);

        return savedOrder;
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
    }

    @Override
    public Order getOrderByNo(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在: " + orderNo);
        }
        return order;
    }

    @Override
    public Map<String, Object> getOrderDetail(Long orderId) {
        Order order = getOrderById(orderId);
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", orderItems);
        result.put("itemCount", orderItems.size());

        return result;
    }

    @Override
    public List<Order> getOrdersByBuyer(Long buyerId) {
        return orderRepository.findByBuyerId(buyerId);
    }

    @Override
    public List<Order> getOrdersBySeller(Long sellerId) {
        return orderRepository.findBySellerId(sellerId);
    }

    @Override
    public Page<Order> getBuyerOrdersPage(Long buyerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return orderRepository.findByBuyerId(buyerId, pageable);
    }

    @Override
    public Page<Order> getSellerOrdersPage(Long sellerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return orderRepository.findBySellerId(sellerId, pageable);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus,
                                   Long operatorId, String note) {
        Order order = getOrderById(orderId);

        // 验证操作权限
        if (!order.getBuyerId().equals(operatorId) &&
                !order.getSellerId().equals(operatorId)) {
            throw new RuntimeException("无权操作此订单");
        }

        // 状态转换验证（这里可以更严格）
        order.setStatus(newStatus);

        // 设置备注
        if (operatorId.equals(order.getBuyerId())) {
            order.setBuyerNote(note);
        } else {
            order.setSellerNote(note);
        }

        // 记录状态变更时间
        Date now = new Date();
        switch (newStatus) {
            case PAID:
                order.setPaymentTime(now);
                break;
            case SHIPPED:
                order.setShippingTime(now);
                break;
            case DELIVERED:
                order.setDeliveryTime(now);
                break;
            case COMPLETED:
                order.setCompletedTime(now);
                break;
        }

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order payOrder(Long orderId, Long buyerId, BigDecimal paidAmount) {
        Order order = getOrderById(orderId);

        // 验证买家
        if (!order.getBuyerId().equals(buyerId)) {
            throw new RuntimeException("只能支付自己的订单");
        }

        // 验证订单状态
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("订单当前不能支付");
        }

        // 验证支付金额
        if (paidAmount.compareTo(order.getPayableAmount()) < 0) {
            throw new RuntimeException("支付金额不足");
        }

        // 更新状态为已支付
        order.setStatus(OrderStatus.PAID);
        order.setPaymentTime(new Date());

        log.info("订单支付成功: 订单号={}, 支付金额={}", order.getOrderNo(), paidAmount);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order shipOrder(Long orderId, Long sellerId,
                           String shippingCompany, String trackingNumber) {
        Order order = getOrderById(orderId);

        // 验证卖家
        if (!order.getSellerId().equals(sellerId)) {
            throw new RuntimeException("只能发货自己的订单");
        }

        // 验证订单状态
        if (order.getStatus() != OrderStatus.PAID) {
            throw new RuntimeException("只有已支付的订单才能发货");
        }

        // 更新发货信息
        order.setStatus(OrderStatus.SHIPPED);
        order.setShippingCompany(shippingCompany);
        order.setTrackingNumber(trackingNumber);
        order.setShippingTime(new Date());

        log.info("订单已发货: 订单号={}, 物流单号={}", order.getOrderNo(), trackingNumber);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order confirmDelivery(Long orderId, Long buyerId) {
        Order order = getOrderById(orderId);

        // 验证买家
        if (!order.getBuyerId().equals(buyerId)) {
            throw new RuntimeException("只能确认自己的订单");
        }

        // 验证订单状态
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new RuntimeException("只有已发货的订单才能确认收货");
        }

        // 更新状态
        order.setStatus(OrderStatus.DELIVERED);
        order.setDeliveryTime(new Date());

        log.info("订单确认收货: 订单号={}", order.getOrderNo());

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId, Long userId, String reason) {
        Order order = getOrderById(orderId);

        // 验证操作权限
        if (!order.getBuyerId().equals(userId) &&
                !order.getSellerId().equals(userId)) {
            throw new RuntimeException("无权取消此订单");
        }

        // 只有待付款和已付款的订单可以取消
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT &&
                order.getStatus() != OrderStatus.PAID) {
            throw new RuntimeException("当前状态的订单不能取消");
        }

        // 更新状态
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelReason(reason);

        // 恢复商品库存
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElse(null);
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                if (product.getStatus() == 2) { // 如果是售罄状态
                    product.setStatus(1); // 恢复为上架
                }
                productRepository.save(product);
            }
        }

        log.info("订单已取消: 订单号={}, 原因={}", order.getOrderNo(), reason);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order applyRefund(Long orderId, Long buyerId,
                             BigDecimal refundAmount, String reason) {
        Order order = getOrderById(orderId);

        // 验证买家
        if (!order.getBuyerId().equals(buyerId)) {
            throw new RuntimeException("只能申请自己订单的退款");
        }

        // 验证订单状态（只有已支付和已发货的订单可以申请退款）
        if (order.getStatus() != OrderStatus.PAID &&
                order.getStatus() != OrderStatus.SHIPPED) {
            throw new RuntimeException("当前订单状态不能申请退款");
        }

        // 验证退款金额
        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0 ||
                refundAmount.compareTo(order.getPayableAmount()) > 0) {
            throw new RuntimeException("退款金额无效");
        }

        // 更新状态
        order.setStatus(OrderStatus.REFUNDING);
        order.setRefundAmount(refundAmount);
        order.setRefundReason(reason);

        log.info("申请退款: 订单号={}, 退款金额={}, 原因={}",
                order.getOrderNo(), refundAmount, reason);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order processRefund(Long orderId, Long sellerId,
                               boolean agree, String remark) {
        Order order = getOrderById(orderId);

        // 验证卖家
        if (!order.getSellerId().equals(sellerId)) {
            throw new RuntimeException("只能处理自己订单的退款");
        }

        // 验证订单状态
        if (order.getStatus() != OrderStatus.REFUNDING) {
            throw new RuntimeException("订单不在退款申请中");
        }

        if (agree) {
            // 同意退款
            order.setStatus(OrderStatus.REFUNDED);
            log.info("退款已同意: 订单号={}, 退款金额={}", order.getOrderNo(), order.getRefundAmount());
        } else {
            // 拒绝退款，恢复原状态
            order.setStatus(OrderStatus.PAID); // 恢复为已支付
            order.setRefundAmount(null);
            order.setRefundReason(null);
            log.info("退款已拒绝: 订单号={}", order.getOrderNo());
        }

        order.setSellerNote(remark);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId, Long userId) {
        Order order = getOrderById(orderId);

        // 验证操作权限
        if (!order.getBuyerId().equals(userId) &&
                !order.getSellerId().equals(userId)) {
            throw new RuntimeException("无权删除此订单");
        }

        // 逻辑删除：这里我们简单地从数据库删除
        // 实际项目中可能需要软删除（标记删除）
        orderRepository.deleteById(orderId);

        log.info("订单已删除: 订单号={}, 操作人={}", order.getOrderNo(), userId);
    }

    @Override
    public Map<String, Object> getOrderStats(Long userId, boolean isSeller) {
        Map<String, Object> stats = new HashMap<>();

        if (isSeller) {
            // 卖家统计
            Long totalOrders = orderRepository.countBySellerId(userId);
            Long pendingPayment = orderRepository.countBySellerIdAndStatus(userId, OrderStatus.PENDING_PAYMENT);
            Long pendingShipment = orderRepository.countBySellerIdAndStatus(userId, OrderStatus.PAID);
            Long shipped = orderRepository.countBySellerIdAndStatus(userId, OrderStatus.SHIPPED);

            stats.put("totalOrders", totalOrders);
            stats.put("pendingPayment", pendingPayment);
            stats.put("pendingShipment", pendingShipment);
            stats.put("shipped", shipped);
        } else {
            // 买家统计
            Long totalOrders = orderRepository.countByBuyerId(userId);
            Long pendingPayment = (long) orderRepository.findByBuyerIdAndStatus(userId, OrderStatus.PENDING_PAYMENT).size();
            Long pendingReceipt = (long) orderRepository.findByBuyerIdAndStatus(userId, OrderStatus.SHIPPED).size();
            Long completed = (long) orderRepository.findByBuyerIdAndStatus(userId, OrderStatus.COMPLETED).size();

            stats.put("totalOrders", totalOrders);
            stats.put("pendingPayment", pendingPayment);
            stats.put("pendingReceipt", pendingReceipt);
            stats.put("completed", completed);
        }

        return stats;
    }

    @Override
    public String generateOrderNo() {
        // 生成订单号：年月日时分秒 + 4位随机数
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timePart = LocalDateTime.now().format(formatter);
        String randomPart = String.format("%04d", new Random().nextInt(10000));
        return "KB" + timePart + randomPart;
    }
}
