package com.kb.trading.repository;
import com.kb.trading.entity.Order;
import com.kb.trading.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
    // 根据订单号查找
    Order findByOrderNo(String orderNo);

    // 根据买家ID查找
    List<Order> findByBuyerId(Long buyerId);

    // 根据卖家ID查找
    List<Order> findBySellerId(Long sellerId);

    // 根据状态查找
    List<Order> findByStatus(OrderStatus status);

    // 买家按状态查找
    List<Order> findByBuyerIdAndStatus(Long buyerId, OrderStatus status);

    // 卖家按状态查找
    List<Order> findBySellerIdAndStatus(Long sellerId, OrderStatus status);

    // 按时间范围查找
    List<Order> findByCreateTimeBetween(Date startDate, Date endDate);

    // 分页查询买家订单
    Page<Order> findByBuyerId(Long buyerId, Pageable pageable);

    // 分页查询卖家订单
    Page<Order> findBySellerId(Long sellerId, Pageable pageable);

    // 统计买家订单数量
    Long countByBuyerId(Long buyerId);

    // 统计卖家订单数量
    Long countBySellerId(Long sellerId);

    // 统计卖家不同状态的订单数量
    @Query("SELECT COUNT(o) FROM Order o WHERE o.sellerId = :sellerId AND o.status = :status")
    Long countBySellerIdAndStatus(@Param("sellerId") Long sellerId,
                                  @Param("status") OrderStatus status);

    // 搜索订单（按订单号或商品名称）
    @Query("SELECT o FROM Order o WHERE o.orderNo LIKE %:keyword% OR " +
            "o.buyerName LIKE %:keyword% OR o.sellerName LIKE %:keyword%")
    List<Order> searchOrders(@Param("keyword") String keyword);
}
