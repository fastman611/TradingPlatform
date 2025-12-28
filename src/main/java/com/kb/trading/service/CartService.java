package com.kb.trading.service;
import com.kb.trading.entity.CartItem;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
public interface CartService {
    // 添加商品到购物车
    CartItem addToCart(Long userId, Long productId, Integer quantity, String spec);

    // 更新购物车商品数量
    CartItem updateQuantity(Long userId, Long cartItemId, Integer quantity);

    // 直接更新商品数量（通过用户ID和商品ID）
    CartItem updateQuantityByProduct(Long userId, Long productId, Integer quantity);

    // 从购物车移除商品
    void removeFromCart(Long userId, Long cartItemId);

    // 批量移除商品
    void batchRemoveFromCart(Long userId, List<Long> cartItemIds);

    // 清空购物车
    void clearCart(Long userId);

    // 获取用户购物车列表
    List<CartItem> getCartItems(Long userId);

    // 获取购物车统计信息
    Map<String, Object> getCartSummary(Long userId);

    // 获取购物车商品数量
    Integer getCartItemCount(Long userId);

    // 获取购物车总金额
    BigDecimal getCartTotalAmount(Long userId);

    // 检查商品是否在购物车中
    boolean isInCart(Long userId, Long productId);

    // 合并购物车（用户登录时调用）
    void mergeCart(Long userId, List<CartItem> tempCartItems);

    // 验证购物车商品库存
    Map<Long, Integer> validateCartStock(Long userId);

    // 从购物车生成订单项
    List<com.kb.trading.entity.OrderItem> convertToOrderItems(Long userId);
}
