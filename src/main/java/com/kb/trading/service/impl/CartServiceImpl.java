package com.kb.trading.service.impl;
import com.kb.trading.entity.CartItem;
import com.kb.trading.entity.OrderItem;
import com.kb.trading.entity.Product;
import com.kb.trading.entity.User;
import com.kb.trading.repository.CartItemRepository;
import com.kb.trading.repository.ProductRepository;
import com.kb.trading.repository.UserRepository;
import com.kb.trading.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CartItem addToCart(Long userId, Long productId, Integer quantity, String spec) {
        // 验证用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 验证商品
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 验证商品状态
        if (product.getStatus() != 1) {
            throw new RuntimeException("商品已下架或售出");
        }

        // 验证库存
        if (quantity > product.getStock()) {
            throw new RuntimeException("商品库存不足，当前库存: " + product.getStock());
        }

        // 检查是否已在购物车中
        Optional<CartItem> existingCartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            // 更新已有购物车项的数量
            cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;

            // 再次验证库存
            if (newQuantity > product.getStock()) {
                throw new RuntimeException("商品库存不足，最多可购买: " + product.getStock());
            }

            cartItem.setQuantity(newQuantity);
            cartItem.setProductSpec(spec != null ? spec : cartItem.getProductSpec());
        } else {
            // 创建新的购物车项
            cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
            cartItem.setProductSpec(spec);
            cartItem.setUnitPrice(product.getPrice());
            cartItem.setProductTitle(product.getTitle());
            cartItem.setProductImage(product.getMainImage());
        }

        // 计算小计金额
        cartItem.calculateSubtotal();

        // 保存购物车项
        CartItem savedCartItem = cartItemRepository.save(cartItem);

        log.info("用户 {} 添加商品 {} 到购物车，数量: {}", userId, productId, quantity);

        return savedCartItem;
    }

    @Override
    @Transactional
    public CartItem updateQuantity(Long userId, Long cartItemId, Integer quantity) {
        // 验证购物车项是否存在且属于该用户
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("购物车项不存在"));

        if (!cartItem.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此购物车项");
        }

        // 验证商品库存
        Product product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (quantity > product.getStock()) {
            throw new RuntimeException("商品库存不足，最多可购买: " + product.getStock());
        }

        if (quantity <= 0) {
            // 数量为0或负数，删除该购物车项
            cartItemRepository.delete(cartItem);
            log.info("用户 {} 删除购物车项 {}", userId, cartItemId);
            return null;
        }

        // 更新数量
        cartItem.setQuantity(quantity);
        cartItem.calculateSubtotal();

        CartItem updatedCartItem = cartItemRepository.save(cartItem);

        log.info("用户 {} 更新购物车项 {} 数量为 {}", userId, cartItemId, quantity);

        return updatedCartItem;
    }

    @Override
    @Transactional
    public CartItem updateQuantityByProduct(Long userId, Long productId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("购物车中未找到该商品"));

        return updateQuantity(userId, cartItem.getId(), quantity);
    }

    @Override
    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("购物车项不存在"));

        if (!cartItem.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此购物车项");
        }

        cartItemRepository.delete(cartItem);

        log.info("用户 {} 从购物车移除商品 {}", userId, cartItem.getProductId());
    }

    @Override
    @Transactional
    public void batchRemoveFromCart(Long userId, List<Long> cartItemIds) {
        int deletedCount = 0;
        for (Long cartItemId : cartItemIds) {
            try {
                removeFromCart(userId, cartItemId);
                deletedCount++;
            } catch (Exception e) {
                log.warn("删除购物车项失败: {}", e.getMessage());
            }
        }

        log.info("用户 {} 批量删除 {} 个购物车项", userId, deletedCount);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
        log.info("用户 {} 清空购物车", userId);
    }

    @Override
    public List<CartItem> getCartItems(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Override
    public Map<String, Object> getCartSummary(Long userId) {
        List<CartItem> cartItems = getCartItems(userId);
        Integer itemCount = getCartItemCount(userId);
        BigDecimal totalAmount = getCartTotalAmount(userId);

        Map<String, Object> summary = new HashMap<>();
        summary.put("cartItems", cartItems);
        summary.put("itemCount", itemCount);           // 商品种类数
        summary.put("totalQuantity", cartItems.stream()  // 商品总件数
                .mapToInt(CartItem::getQuantity)
                .sum());
        summary.put("totalAmount", totalAmount);

        return summary;
    }

    @Override
    public Integer getCartItemCount(Long userId) {
        Long count = cartItemRepository.countByUserId(userId);
        return count != null ? count.intValue() : 0;
    }

    @Override
    public BigDecimal getCartTotalAmount(Long userId) {
        BigDecimal total = cartItemRepository.sumSubtotalByUserId(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public boolean isInCart(Long userId, Long productId) {
        return cartItemRepository.findByUserIdAndProductId(userId, productId).isPresent();
    }

    @Override
    @Transactional
    public void mergeCart(Long userId, List<CartItem> tempCartItems) {
        if (tempCartItems == null || tempCartItems.isEmpty()) {
            return;
        }

        for (CartItem tempItem : tempCartItems) {
            try {
                addToCart(userId, tempItem.getProductId(),
                        tempItem.getQuantity(), tempItem.getProductSpec());
            } catch (Exception e) {
                log.warn("合并购物车项失败: {}", e.getMessage());
            }
        }

        log.info("用户 {} 合并购物车，合并了 {} 个商品", userId, tempCartItems.size());
    }

    @Override
    public Map<Long, Integer> validateCartStock(Long userId) {
        List<CartItem> cartItems = getCartItems(userId);
        Map<Long, Integer> validationResult = new HashMap<>();

        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId()).orElse(null);

            if (product == null) {
                validationResult.put(cartItem.getProductId(), -1); // 商品不存在
            } else if (product.getStatus() != 1) {
                validationResult.put(cartItem.getProductId(), -2); // 商品已下架
            } else if (cartItem.getQuantity() > product.getStock()) {
                validationResult.put(cartItem.getProductId(), product.getStock()); // 库存不足
            } else {
                validationResult.put(cartItem.getProductId(), 0); // 库存充足
            }
        }

        return validationResult;
    }

    @Override
    public List<OrderItem> convertToOrderItems(Long userId) {
        List<CartItem> cartItems = getCartItems(userId);
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId()).orElse(null);

            if (product != null && product.getStatus() == 1 &&
                    cartItem.getQuantity() <= product.getStock()) {

                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(cartItem.getProductId());
                orderItem.setProductTitle(cartItem.getProductTitle());
                orderItem.setProductImage(cartItem.getProductImage());
                orderItem.setUnitPrice(cartItem.getUnitPrice());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setSubtotal(cartItem.getSubtotal());
                orderItem.setProductSpec(cartItem.getProductSpec());

                orderItems.add(orderItem);
            }
        }

        return orderItems;
    }
}
