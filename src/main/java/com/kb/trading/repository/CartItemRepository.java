package com.kb.trading.repository;
import com.kb.trading.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // 根据用户ID查找购物车项
    List<CartItem> findByUserId(Long userId);

    // 根据用户ID和商品ID查找
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    // 统计用户购物车商品数量
    Long countByUserId(Long userId);

    // 统计用户购物车商品总件数
    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.userId = :userId")
    Integer sumQuantityByUserId(@Param("userId") Long userId);

    // 计算购物车总金额
    @Query("SELECT SUM(ci.subtotal) FROM CartItem ci WHERE ci.userId = :userId")
    BigDecimal sumSubtotalByUserId(@Param("userId") Long userId);

    // 根据用户ID删除购物车项
    @Transactional
    @Modifying
    void deleteByUserId(Long userId);

    // 根据用户ID和商品ID删除
    @Transactional
    @Modifying
    void deleteByUserIdAndProductId(Long userId, Long productId);

    // 批量删除购物车项
    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.userId = :userId AND ci.productId IN :productIds")
    void deleteByUserIdAndProductIds(@Param("userId") Long userId,
                                     @Param("productIds") List<Long> productIds);

    // 获取用户购物车中的商品ID列表
    @Query("SELECT ci.productId FROM CartItem ci WHERE ci.userId = :userId")
    List<Long> findProductIdsByUserId(@Param("userId") Long userId);

    // 更新购物车项数量
    @Transactional
    @Modifying
    @Query("UPDATE CartItem ci SET ci.quantity = :quantity, " +
            "ci.subtotal = ci.unitPrice * CAST(:quantity AS java.math.BigDecimal) " +
            "WHERE ci.id = :id AND ci.userId = :userId")
    int updateQuantity(@Param("id") Long id,
                       @Param("userId") Long userId,
                       @Param("quantity") Integer quantity);
}
