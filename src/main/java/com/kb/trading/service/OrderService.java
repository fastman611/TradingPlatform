package com.kb.trading.service;
import com.kb.trading.entity.Order;
import com.kb.trading.entity.OrderItem;
import com.kb.trading.entity.OrderStatus;
import org.springframework.data.domain.Page;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
public interface OrderService {
    // 创建订单
    Order createOrder(Long buyerId, List<OrderItem> items,
                      String address, String phone, String note);

    // 根据ID获取订单
    Order getOrderById(Long orderId);

    // 根据订单号获取订单
    Order getOrderByNo(String orderNo);

    // 获取订单详情（包含订单项）
    Map<String, Object> getOrderDetail(Long orderId);

    // 获取买家订单列表
    List<Order> getOrdersByBuyer(Long buyerId);

    // 获取卖家订单列表
    List<Order> getOrdersBySeller(Long sellerId);

    // 分页获取买家订单
    Page<Order> getBuyerOrdersPage(Long buyerId, int page, int size);

    // 分页获取卖家订单
    Page<Order> getSellerOrdersPage(Long sellerId, int page, int size);

    // 更新订单状态
    Order updateOrderStatus(Long orderId, OrderStatus newStatus,
                            Long operatorId, String note);

    // 支付订单
    Order payOrder(Long orderId, Long buyerId, BigDecimal paidAmount);

    // 发货
    Order shipOrder(Long orderId, Long sellerId,
                    String shippingCompany, String trackingNumber);

    // 确认收货
    Order confirmDelivery(Long orderId, Long buyerId);

    // 取消订单
    Order cancelOrder(Long orderId, Long userId, String reason);

    // 申请退款
    Order applyRefund(Long orderId, Long buyerId,
                      BigDecimal refundAmount, String reason);

    // 处理退款
    Order processRefund(Long orderId, Long sellerId,
                        boolean agree, String remark);

    // 删除订单（逻辑删除）
    void deleteOrder(Long orderId, Long userId);

    // 获取订单统计信息
    Map<String, Object> getOrderStats(Long userId, boolean isSeller);

    // 生成订单号（工具方法）
    String generateOrderNo();
}


