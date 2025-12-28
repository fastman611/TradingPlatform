package com.kb.trading.repository;
import com.kb.trading.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{
    // 根据订单ID查找所有订单项
    List<OrderItem> findByOrderId(Long orderId);

    // 根据商品ID查找销售记录
    List<OrderItem> findByProductId(Long productId);

    // 统计商品销量
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.productId = :productId")
    Integer sumQuantityByProductId(@Param("productId") Long productId);

    // 统计商品销售额
    @Query("SELECT SUM(oi.subtotal) FROM OrderItem oi WHERE oi.productId = :productId")
    BigDecimal sumSalesByProductId(@Param("productId") Long productId);

    // 根据多个订单ID批量查找
    @Query("SELECT oi FROM OrderItem oi WHERE oi.orderId IN :orderIds")
    List<OrderItem> findByOrderIds(@Param("orderIds") List<Long> orderIds);
}
