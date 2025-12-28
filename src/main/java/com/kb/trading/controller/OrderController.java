package com.kb.trading.controller;
import com.kb.trading.entity.Order;
import com.kb.trading.entity.OrderItem;
import com.kb.trading.entity.OrderStatus;
import com.kb.trading.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // 创建订单
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestParam Long buyerId,
            @RequestBody List<OrderItem> items,
            @RequestParam String address,
            @RequestParam String phone,
            @RequestParam(required = false) String note) {

        try {
            Order order = orderService.createOrder(buyerId, items, address, phone, note);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "订单创建成功");
            response.put("order", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取订单详情
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderDetail(@PathVariable Long orderId) {
        try {
            Map<String, Object> orderDetail = orderService.getOrderDetail(orderId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", orderDetail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 根据订单号查询
    @GetMapping("/no/{orderNo}")
    public ResponseEntity<Map<String, Object>> getOrderByNo(@PathVariable String orderNo) {
        try {
            Order order = orderService.getOrderByNo(orderNo);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("order", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取买家订单列表
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<Map<String, Object>> getBuyerOrders(@PathVariable Long buyerId) {
        try {
            List<Order> orders = orderService.getOrdersByBuyer(buyerId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orders", orders);
            response.put("count", orders.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取卖家订单列表
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<Map<String, Object>> getSellerOrders(@PathVariable Long sellerId) {
        try {
            List<Order> orders = orderService.getOrdersBySeller(sellerId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orders", orders);
            response.put("count", orders.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 分页获取买家订单
    @GetMapping("/buyer/{buyerId}/page")
    public ResponseEntity<Map<String, Object>> getBuyerOrdersPage(
            @PathVariable Long buyerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Page<Order> orderPage = orderService.getBuyerOrdersPage(buyerId, page, size);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orders", orderPage.getContent());
            response.put("totalPages", orderPage.getTotalPages());
            response.put("totalElements", orderPage.getTotalElements());
            response.put("currentPage", page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 支付订单
    @PutMapping("/{orderId}/pay")
    public ResponseEntity<Map<String, Object>> payOrder(
            @PathVariable Long orderId,
            @RequestParam Long buyerId,
            @RequestParam BigDecimal amount) {

        try {
            Order order = orderService.payOrder(orderId, buyerId, amount);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "支付成功");
            response.put("order", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 发货
    @PutMapping("/{orderId}/ship")
    public ResponseEntity<Map<String, Object>> shipOrder(
            @PathVariable Long orderId,
            @RequestParam Long sellerId,
            @RequestParam String shippingCompany,
            @RequestParam String trackingNumber) {

        try {
            Order order = orderService.shipOrder(orderId, sellerId, shippingCompany, trackingNumber);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "发货成功");
            response.put("order", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 确认收货
    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<Map<String, Object>> confirmDelivery(
            @PathVariable Long orderId,
            @RequestParam Long buyerId) {

        try {
            Order order = orderService.confirmDelivery(orderId, buyerId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "确认收货成功");
            response.put("order", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 取消订单
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam Long userId,
            @RequestParam String reason) {

        try {
            Order order = orderService.cancelOrder(orderId, userId, reason);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "订单取消成功");
            response.put("order", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 申请退款
    @PutMapping("/{orderId}/apply-refund")
    public ResponseEntity<Map<String, Object>> applyRefund(
            @PathVariable Long orderId,
            @RequestParam Long buyerId,
            @RequestParam BigDecimal refundAmount,
            @RequestParam String reason) {

        try {
            Order order = orderService.applyRefund(orderId, buyerId, refundAmount, reason);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "退款申请已提交");
            response.put("order", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 处理退款
    @PutMapping("/{orderId}/process-refund")
    public ResponseEntity<Map<String, Object>> processRefund(
            @PathVariable Long orderId,
            @RequestParam Long sellerId,
            @RequestParam boolean agree,
            @RequestParam String remark) {

        try {
            Order order = orderService.processRefund(orderId, sellerId, agree, remark);
            Map<String, Object> response = new HashMap<>();
            String message = agree ? "退款申请已同意" : "退款申请已拒绝";
            response.put("success", true);
            response.put("message", message);
            response.put("order", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 删除订单
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> deleteOrder(
            @PathVariable Long orderId,
            @RequestParam Long userId) {

        try {
            orderService.deleteOrder(orderId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "订单删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取订单统计
    @GetMapping("/stats/{userId}")
    public ResponseEntity<Map<String, Object>> getOrderStats(
            @PathVariable Long userId,
            @RequestParam boolean isSeller) {

        try {
            Map<String, Object> stats = orderService.getOrderStats(userId, isSeller);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 生成测试订单号
    @GetMapping("/generate-order-no")
    public ResponseEntity<Map<String, Object>> generateOrderNo() {
        String orderNo = orderService.generateOrderNo();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("orderNo", orderNo);
        return ResponseEntity.ok(response);
    }

    // 获取订单状态列表
    @GetMapping("/status-list")
    public ResponseEntity<Map<String, Object>> getOrderStatusList() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);

        List<Map<String, String>> statusList = List.of(
                Map.of("code", "PENDING_PAYMENT", "name", "待付款"),
                Map.of("code", "PAID", "name", "已付款"),
                Map.of("code", "SHIPPED", "name", "已发货"),
                Map.of("code", "DELIVERED", "name", "已送达"),
                Map.of("code", "COMPLETED", "name", "已完成"),
                Map.of("code", "CANCELLED", "name", "已取消"),
                Map.of("code", "REFUNDING", "name", "退款中"),
                Map.of("code", "REFUNDED", "name", "已退款")
        );

        response.put("statusList", statusList);
        return ResponseEntity.ok(response);
    }
}
