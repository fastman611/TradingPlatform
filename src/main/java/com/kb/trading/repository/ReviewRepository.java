package com.kb.trading.repository;
import com.kb.trading.entity.Review;
import com.kb.trading.entity.ReviewType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 根据订单ID查找评价
    List<Review> findByOrderId(Long orderId);

    // 根据商品ID查找评价
    List<Review> findByProductId(Long productId);

    // 根据买家ID查找评价
    List<Review> findByBuyerId(Long buyerId);

    // 根据卖家ID查找评价
    List<Review> findBySellerId(Long sellerId);

    // 根据评价类型查找
    List<Review> findByType(ReviewType type);

    // 根据订单ID和评价类型查找
    Optional<Review> findByOrderIdAndType(Long orderId, ReviewType type);

    // 根据商品ID和评价类型查找
    List<Review> findByProductIdAndType(Long productId, ReviewType type);

    // 分页查询商品评价
    Page<Review> findByBuyerIdAndType(Long buyerId, ReviewType type, Pageable pageable);
    Page<Review> findBySellerIdAndType(Long sellerId, ReviewType type, Pageable pageable);
    Page<Review> findByProductIdAndType(Long productId, ReviewType type, Pageable pageable);

    // 获取商品的评价数量
    Long countByProductId(Long productId);

    // 获取商品的评分统计
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.productId = :productId AND r.type = 'PRODUCT_REVIEW' GROUP BY r.rating")
    List<Object[]> countRatingsByProductId(@Param("productId") Long productId);

    // 获取商品的平均评分
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.productId = :productId AND r.type = 'PRODUCT_REVIEW'")
    Double getAverageRatingByProductId(@Param("productId") Long productId);

    // 获取带图评价
    @Query("SELECT r FROM Review r WHERE r.productId = :productId AND r.imageUrls IS NOT NULL AND r.imageUrls <> ''")
    List<Review> findReviewsWithImages(@Param("productId") Long productId);

    // 获取好评（4-5星）
    @Query("SELECT r FROM Review r WHERE r.productId = :productId AND r.rating >= 4")
    List<Review> findPositiveReviews(@Param("productId") Long productId);

    // 获取中评（3星）
    @Query("SELECT r FROM Review r WHERE r.productId = :productId AND r.rating = 3")
    List<Review> findNeutralReviews(@Param("productId") Long productId);

    // 获取差评（1-2星）
    @Query("SELECT r FROM Review r WHERE r.productId = :productId AND r.rating <= 2")
    List<Review> findNegativeReviews(@Param("productId") Long productId);

    // 获取追加评价
    @Query("SELECT r FROM Review r WHERE r.parentId = :parentId AND r.type = 'APPEND_REVIEW'")
    List<Review> findAppendReviews(@Param("parentId") Long parentId);

    // 检查订单是否已评价
    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.orderId = :orderId AND r.buyerId = :buyerId")
    boolean existsByOrderIdAndBuyerId(@Param("orderId") Long orderId, @Param("buyerId") Long buyerId);

    // 获取热门评价（点赞数高的）
    @Query("SELECT r FROM Review r WHERE r.productId = :productId ORDER BY r.likeCount DESC")
    Page<Review> findHotReviews(@Param("productId") Long productId, Pageable pageable);

    // 获取最新评价
    @Query("SELECT r FROM Review r WHERE r.productId = :productId ORDER BY r.createTime DESC")
    Page<Review> findLatestReviews(@Param("productId") Long productId, Pageable pageable);
}
