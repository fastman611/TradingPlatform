package com.kb.trading.controller;
import com.kb.trading.entity.CartItem;
import com.kb.trading.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    // 添加商品到购物车
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            @RequestParam(required = false) String spec) {

        try {
            CartItem cartItem = cartService.addToCart(userId, productId, quantity, spec);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "已添加到购物车");
            response.put("cartItem", cartItem);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 更新购物车商品数量
    @PutMapping("/update-quantity")
    public ResponseEntity<Map<String, Object>> updateQuantity(
            @RequestParam Long userId,
            @RequestParam Long cartItemId,
            @RequestParam Integer quantity) {

        try {
            CartItem cartItem = cartService.updateQuantity(userId, cartItemId, quantity);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", cartItem != null ? "数量更新成功" : "已从购物车移除");
            if (cartItem != null) {
                response.put("cartItem", cartItem);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 直接通过商品ID更新数量
    @PutMapping("/update-by-product")
    public ResponseEntity<Map<String, Object>> updateQuantityByProduct(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        try {
            CartItem cartItem = cartService.updateQuantityByProduct(userId, productId, quantity);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", cartItem != null ? "数量更新成功" : "已从购物车移除");
            if (cartItem != null) {
                response.put("cartItem", cartItem);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 从购物车移除商品
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeFromCart(
            @RequestParam Long userId,
            @RequestParam Long cartItemId) {

        try {
            cartService.removeFromCart(userId, cartItemId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "已从购物车移除");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 批量移除商品
    @DeleteMapping("/batch-remove")
    public ResponseEntity<Map<String, Object>> batchRemoveFromCart(
            @RequestParam Long userId,
            @RequestBody List<Long> cartItemIds) {

        try {
            cartService.batchRemoveFromCart(userId, cartItemIds);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量移除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 清空购物车
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearCart(@RequestParam Long userId) {
        try {
            cartService.clearCart(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "购物车已清空");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取购物车列表
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getCartItems(@RequestParam Long userId) {
        try {
            List<CartItem> cartItems = cartService.getCartItems(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("cartItems", cartItems);
            response.put("count", cartItems.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取购物车统计信息
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getCartSummary(@RequestParam Long userId) {
        try {
            Map<String, Object> summary = cartService.getCartSummary(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("summary", summary);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取购物车商品数量
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getCartItemCount(@RequestParam Long userId) {
        try {
            Integer count = cartService.getCartItemCount(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取购物车总金额
    @GetMapping("/total-amount")
    public ResponseEntity<Map<String, Object>> getCartTotalAmount(@RequestParam Long userId) {
        try {
            BigDecimal totalAmount = cartService.getCartTotalAmount(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalAmount", totalAmount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 检查商品是否在购物车中
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> isInCart(
            @RequestParam Long userId,
            @RequestParam Long productId) {

        try {
            boolean isInCart = cartService.isInCart(userId, productId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isInCart", isInCart);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 合并购物车（用于用户登录）
    @PostMapping("/merge")
    public ResponseEntity<Map<String, Object>> mergeCart(
            @RequestParam Long userId,
            @RequestBody List<CartItem> tempCartItems) {

        try {
            cartService.mergeCart(userId, tempCartItems);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "购物车合并成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 验证购物车商品库存
    @GetMapping("/validate-stock")
    public ResponseEntity<Map<String, Object>> validateCartStock(@RequestParam Long userId) {
        try {
            Map<Long, Integer> validationResult = cartService.validateCartStock(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("validationResult", validationResult);

            // 检查是否有库存问题
            boolean hasStockIssue = validationResult.values().stream()
                    .anyMatch(result -> result != 0);
            response.put("hasStockIssue", hasStockIssue);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 从购物车生成订单项（用于下单）
    @GetMapping("/to-order-items")
    public ResponseEntity<Map<String, Object>> convertToOrderItems(@RequestParam Long userId) {
        try {
            List<com.kb.trading.entity.OrderItem> orderItems = cartService.convertToOrderItems(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderItems", orderItems);
            response.put("count", orderItems.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 从购物车直接下单（整合功能）
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkoutFromCart(
            @RequestParam Long userId,
            @RequestParam String address,
            @RequestParam String phone,
            @RequestParam(required = false) String note) {

        try {
            // 1. 验证库存
            Map<Long, Integer> validationResult = cartService.validateCartStock(userId);
            boolean hasStockIssue = validationResult.values().stream()
                    .anyMatch(result -> result != 0);

            if (hasStockIssue) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "部分商品库存不足，请调整购物车");
                response.put("validationResult", validationResult);
                return ResponseEntity.badRequest().body(response);
            }

            // 2. 从购物车生成订单项
            List<com.kb.trading.entity.OrderItem> orderItems = cartService.convertToOrderItems(userId);

            if (orderItems.isEmpty()) {
                throw new RuntimeException("购物车为空");
            }

            // 3. 调用订单服务创建订单
            // 这里需要调用OrderService，暂时返回模拟数据
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "订单创建成功（模拟）");
            response.put("orderItems", orderItems);
            response.put("itemCount", orderItems.size());
            response.put("totalAmount", orderItems.stream()
                    .map(item -> item.getSubtotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add));

            // 4. 清空购物车（可选）
            // cartService.clearCart(userId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
