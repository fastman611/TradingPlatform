package com.kb.trading.service;
import com.kb.trading.entity.Review;
import com.kb.trading.entity.ReviewStats;
import com.kb.trading.entity.ReviewType;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Map;
public interface ReviewService {
    Review createProductReview(Long orderId, Long buyerId, Long productId,
                               Integer rating, String content, List<String> imageUrls,
                               Boolean isAnonymous);

    // 卖家评价买家
    Review createBuyerReview(Long orderId, Long sellerId, Long buyerId,
                             Integer rating, String content);

    // 追加评价
    Review createAppendReview(Long parentReviewId, Long userId, String content,
                              List<String> imageUrls);

    // 回复评价
    Review replyToReview(Long reviewId, Long respondentId, String replyContent);

    // 获取评价详情
    Review getReviewById(Long reviewId);

    // 获取订单的所有评价
    List<Review> getReviewsByOrder(Long orderId);

    // 获取商品的评价列表（分页）
    Page<Review> getProductReviews(Long productId, int page, int size, String sortBy);

    // 获取商品的评价统计
    ReviewStats getProductReviewStats(Long productId);

    // 获取卖家的评价统计
    ReviewStats getSellerReviewStats(Long sellerId);

    // 获取买家的评价统计
    ReviewStats getBuyerReviewStats(Long buyerId);

    // 获取商品的评分分布
    Map<Integer, Long> getProductRatingDistribution(Long productId);

    // 点赞评价
    Review likeReview(Long reviewId, Long userId);

    // 取消点赞
    Review unlikeReview(Long reviewId, Long userId);

    // 删除评价
    void deleteReview(Long reviewId, Long userId);

    // 检查是否可以评价
    Map<String, Object> checkReviewEligibility(Long orderId, Long userId);

    // 获取热门评价
    List<Review> getHotReviews(Long productId, int limit);

    // 获取带图评价
    List<Review> getReviewsWithImages(Long productId, int limit);

    // 获取用户的评价列表
    Page<Review> getUserReviews(Long userId, ReviewType type, int page, int size);

    // 批量获取商品评价统计
    Map<Long, ReviewStats> batchGetProductReviewStats(List<Long> productIds);
}
