package com.kb.trading.service.impl;
import com.kb.trading.entity.*;
import com.kb.trading.repository.*;
import com.kb.trading.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewStatsRepository reviewStatsRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Review createProductReview(Long orderId, Long buyerId, Long productId,
                                      Integer rating, String content, List<String> imageUrls,
                                      Boolean isAnonymous) {
        // 1. 验证订单
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        // 2. 验证买家权限
        if (!order.getBuyerId().equals(buyerId)) {
            throw new RuntimeException("只能评价自己的订单");
        }

        // 3. 验证订单状态（只有已完成订单可以评价）
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new RuntimeException("只有已完成的订单可以评价");
        }

        // 4. 检查是否已评价
        if (reviewRepository.existsByOrderIdAndBuyerId(orderId, buyerId)) {
            throw new RuntimeException("该订单已评价");
        }

        // 5. 验证评分范围
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("评分必须在1-5之间");
        }

        // 6. 创建商品评价
        Review productReview = createReview(order, productId, buyerId,
                order.getSellerId(), ReviewType.PRODUCT_REVIEW, rating,
                content, imageUrls, isAnonymous);

        log.info("用户 {} 对商品 {} 创建评价，评分: {}", buyerId, productId, rating);

        // 7. 自动创建卖家评价（可选）
        createSellerReview(order, buyerId, rating, content);

        return productReview;
    }

    @Override
    @Transactional
    public Review createBuyerReview(Long orderId, Long sellerId, Long buyerId,
                                    Integer rating, String content) {
        // 1. 验证订单
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        // 2. 验证卖家权限
        if (!order.getSellerId().equals(sellerId)) {
            throw new RuntimeException("只能评价自己的订单买家");
        }

        // 3. 验证订单状态
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new RuntimeException("只有已完成的订单可以评价买家");
        }

        // 4. 验证评分范围
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("评分必须在1-5之间");
        }

        // 5. 创建买家评价
        Review buyerReview = createReview(order, null, sellerId,
                buyerId, ReviewType.BUYER_REVIEW, rating, content, null, false);

        log.info("卖家 {} 对买家 {} 创建评价，评分: {}", sellerId, buyerId, rating);

        return buyerReview;
    }

    private Review createReview(Order order, Long productId, Long reviewerId,
                                Long targetId, ReviewType type, Integer rating,
                                String content, List<String> imageUrls, Boolean isAnonymous) {
        // 获取相关信息
        Product product = productId != null ?
                productRepository.findById(productId).orElse(null) : null;
        User reviewer = userRepository.findById(reviewerId).orElse(null);
        User targetUser = userRepository.findById(targetId).orElse(null);

        // 创建评价
        Review review = new Review();
        review.setOrderId(order.getId());
        review.setProductId(productId);
        review.setBuyerId(order.getBuyerId());
        review.setBuyerName(order.getBuyerName());
        review.setSellerId(order.getSellerId());
        review.setSellerName(order.getSellerName());
        review.setType(type);
        review.setRating(rating);
        review.setContent(content);
        review.setIsAnonymous(isAnonymous != null ? isAnonymous : false);

        // 设置图片
        if (imageUrls != null && !imageUrls.isEmpty()) {
            review.setImageList(imageUrls);
        }

        // 保存评价
        Review savedReview = reviewRepository.save(review);

        // 更新统计信息
        updateReviewStats(savedReview, true);

        return savedReview;
    }

    private void createSellerReview(Order order, Long buyerId, Integer rating, String content) {
        try {
            // 自动创建卖家评价（复制商品评价内容）
            Review sellerReview = new Review();
            sellerReview.setOrderId(order.getId());
            sellerReview.setProductId(null); // 卖家评价不关联具体商品
            sellerReview.setBuyerId(buyerId);
            sellerReview.setBuyerName(order.getBuyerName());
            sellerReview.setSellerId(order.getSellerId());
            sellerReview.setSellerName(order.getSellerName());
            sellerReview.setType(ReviewType.SELLER_REVIEW);
            sellerReview.setRating(rating);
            sellerReview.setContent(content);
            sellerReview.setIsAnonymous(false);

            reviewRepository.save(sellerReview);

            // 更新卖家统计
            updateReviewStatsForTarget(order.getSellerId(), "SELLER", sellerReview, true);

            log.debug("自动创建卖家评价: 卖家ID={}", order.getSellerId());
        } catch (Exception e) {
            log.warn("自动创建卖家评价失败: {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public Review createAppendReview(Long parentReviewId, Long userId, String content,
                                     List<String> imageUrls) {
        // 1. 获取父评价
        Review parentReview = reviewRepository.findById(parentReviewId)
                .orElseThrow(() -> new RuntimeException("原评价不存在"));

        // 2. 验证权限（只能追加自己的评价）
        if (!parentReview.getBuyerId().equals(userId)) {
            throw new RuntimeException("只能追加自己的评价");
        }

        // 3. 检查是否已追加
        List<Review> existingAppends = reviewRepository.findAppendReviews(parentReviewId);
        if (!existingAppends.isEmpty()) {
            throw new RuntimeException("已追加过评价");
        }

        // 4. 创建追加评价
        Review appendReview = new Review();
        appendReview.setOrderId(parentReview.getOrderId());
        appendReview.setProductId(parentReview.getProductId());
        appendReview.setBuyerId(parentReview.getBuyerId());
        appendReview.setBuyerName(parentReview.getBuyerName());
        appendReview.setSellerId(parentReview.getSellerId());
        appendReview.setSellerName(parentReview.getSellerName());
        appendReview.setType(ReviewType.APPEND_REVIEW);
        appendReview.setRating(parentReview.getRating()); // 继承原评分
        appendReview.setContent(content);
        appendReview.setParentId(parentReviewId);

        if (imageUrls != null && !imageUrls.isEmpty()) {
            appendReview.setImageList(imageUrls);
        }

        Review savedReview = reviewRepository.save(appendReview);

        // 5. 更新父评价的追加计数
        parentReview.setReplyCount(parentReview.getReplyCount() + 1);
        reviewRepository.save(parentReview);

        // 6. 更新统计
        updateReviewStats(savedReview, true);

        log.info("用户 {} 追加评价 {}", userId, parentReviewId);

        return savedReview;
    }

    @Override
    @Transactional
    public Review replyToReview(Long reviewId, Long respondentId, String replyContent) {
        // 1. 获取评价
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("评价不存在"));

        // 2. 验证权限（卖家可以回复商品评价，买家可以回复买家评价）
        boolean canReply = false;
        if (review.getType() == ReviewType.PRODUCT_REVIEW) {
            // 商品评价，卖家可以回复
            canReply = review.getSellerId().equals(respondentId);
        } else if (review.getType() == ReviewType.BUYER_REVIEW) {
            // 买家评价，买家可以回复
            canReply = review.getBuyerId().equals(respondentId);
        }

        if (!canReply) {
            throw new RuntimeException("无权回复此评价");
        }

        // 3. 检查是否已回复
        if (review.getHasReply()) {
            throw new RuntimeException("该评价已回复");
        }

        // 4. 设置回复
        review.setHasReply(true);
        review.setReplyContent(replyContent);

        // 5. 更新统计
        if (review.getProductId() != null) {
            updateReviewStatsForTarget(review.getProductId(), "PRODUCT", review, true);
        }

        log.info("用户 {} 回复评价 {}", respondentId, reviewId);

        return reviewRepository.save(review);
    }

    @Override
    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("评价不存在"));
    }

    @Override
    public List<Review> getReviewsByOrder(Long orderId) {
        return reviewRepository.findByOrderId(orderId);
    }

    @Override
    public Page<Review> getProductReviews(Long productId, int page, int size, String sortBy) {
        Pageable pageable;

        switch (sortBy) {
            case "latest":
                pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
                break;
            case "hot":
                pageable = PageRequest.of(page, size, Sort.by("likeCount").descending());
                break;
            case "rating":
                pageable = PageRequest.of(page, size, Sort.by("rating").descending());
                break;
            default:
                pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        }

        return reviewRepository.findByProductIdAndType(productId, ReviewType.PRODUCT_REVIEW, pageable);
    }

    @Override
    public ReviewStats getProductReviewStats(Long productId) {
        return reviewStatsRepository.findByTargetIdAndTargetType(productId, "PRODUCT")
                .orElseGet(() -> createEmptyStats(productId, "PRODUCT"));
    }

    @Override
    public ReviewStats getSellerReviewStats(Long sellerId) {
        return reviewStatsRepository.findByTargetIdAndTargetType(sellerId, "SELLER")
                .orElseGet(() -> createEmptyStats(sellerId, "SELLER"));
    }

    @Override
    public ReviewStats getBuyerReviewStats(Long buyerId) {
        return reviewStatsRepository.findByTargetIdAndTargetType(buyerId, "BUYER")
                .orElseGet(() -> createEmptyStats(buyerId, "BUYER"));
    }

    private ReviewStats createEmptyStats(Long targetId, String targetType) {
        ReviewStats stats = new ReviewStats();
        stats.setTargetId(targetId);
        stats.setTargetType(targetType);
        return reviewStatsRepository.save(stats);
    }

    @Override
    public Map<Integer, Long> getProductRatingDistribution(Long productId) {
        List<Object[]> results = reviewRepository.countRatingsByProductId(productId);

        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0L);
        }

        for (Object[] result : results) {
            Integer rating = (Integer) result[0];
            Long count = (Long) result[1];
            distribution.put(rating, count);
        }

        return distribution;
    }

    @Override
    @Transactional
    public Review likeReview(Long reviewId, Long userId) {
        Review review = getReviewById(reviewId);

        // 这里可以添加检查用户是否已点赞的逻辑
        // 目前简单实现，直接增加点赞数
        review.setLikeCount(review.getLikeCount() + 1);

        log.info("用户 {} 点赞评价 {}", userId, reviewId);

        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public Review unlikeReview(Long reviewId, Long userId) {
        Review review = getReviewById(reviewId);

        if (review.getLikeCount() > 0) {
            review.setLikeCount(review.getLikeCount() - 1);
        }

        log.info("用户 {} 取消点赞评价 {}", userId, reviewId);

        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = getReviewById(reviewId);

        // 验证权限
        boolean canDelete = review.getBuyerId().equals(userId) ||
                review.getSellerId().equals(userId);

        if (!canDelete) {
            throw new RuntimeException("无权删除此评价");
        }

        // 更新统计信息
        updateReviewStats(review, false);

        // 删除评价
        reviewRepository.delete(review);

        log.info("用户 {} 删除评价 {}", userId, reviewId);
    }

    private void updateReviewStats(Review review, boolean isAdd) {
        // 更新商品统计
        if (review.getProductId() != null &&
                (review.getType() == ReviewType.PRODUCT_REVIEW ||
                        review.getType() == ReviewType.APPEND_REVIEW)) {
            updateReviewStatsForTarget(review.getProductId(), "PRODUCT", review, isAdd);
        }

        // 更新卖家统计
        if (review.getType() == ReviewType.SELLER_REVIEW) {
            updateReviewStatsForTarget(review.getSellerId(), "SELLER", review, isAdd);
        }

        // 更新买家统计
        if (review.getType() == ReviewType.BUYER_REVIEW) {
            updateReviewStatsForTarget(review.getBuyerId(), "BUYER", review, isAdd);
        }
    }

    private void updateReviewStatsForTarget(Long targetId, String targetType,
                                            Review review, boolean isAdd) {
        ReviewStats stats = reviewStatsRepository
                .findByTargetIdAndTargetType(targetId, targetType)
                .orElseGet(() -> {
                    ReviewStats newStats = new ReviewStats();
                    newStats.setTargetId(targetId);
                    newStats.setTargetType(targetType);
                    return newStats;
                });

        if (isAdd) {
            stats.updateStats(review);
        } else {
            stats.removeStats(review);
        }

        reviewStatsRepository.save(stats);
    }

    @Override
    public Map<String, Object> checkReviewEligibility(Long orderId, Long userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("订单不存在"));

            // 检查权限
            if (!order.getBuyerId().equals(userId)) {
                result.put("eligible", false);
                result.put("reason", "只能评价自己的订单");
                return result;
            }

            // 检查订单状态
            if (order.getStatus() != OrderStatus.COMPLETED) {
                result.put("eligible", false);
                result.put("reason", "只有已完成的订单可以评价");
                return result;
            }

            // 检查是否已评价
            if (reviewRepository.existsByOrderIdAndBuyerId(orderId, userId)) {
                result.put("eligible", false);
                result.put("reason", "该订单已评价");
                return result;
            }

            result.put("eligible", true);
            result.put("order", order);

        } catch (Exception e) {
            result.put("eligible", false);
            result.put("reason", e.getMessage());
        }

        return result;
    }

    @Override
    public List<Review> getHotReviews(Long productId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<Review> hotReviews = reviewRepository.findHotReviews(productId, pageable);
        return hotReviews.getContent();
    }

    @Override
    public List<Review> getReviewsWithImages(Long productId, int limit) {
        List<Review> reviewsWithImages = reviewRepository.findReviewsWithImages(productId);
        return reviewsWithImages.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Review> getUserReviews(Long userId, ReviewType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());

        if (type == ReviewType.PRODUCT_REVIEW) {
            // 用户作为买家的商品评价
            return reviewRepository.findByBuyerIdAndType(userId, type, pageable);
        } else if (type == ReviewType.SELLER_REVIEW) {
            // 用户作为卖家的评价
            return reviewRepository.findBySellerIdAndType(userId, type, pageable);
        } else if (type == ReviewType.BUYER_REVIEW) {
            // 用户作为买家的评价（被卖家评价）
            return reviewRepository.findByBuyerIdAndType(userId, type, pageable);
        } else {
            return Page.empty();
        }
    }

    @Override
    public Map<Long, ReviewStats> batchGetProductReviewStats(List<Long> productIds) {
        Map<Long, ReviewStats> result = new HashMap<>();

        for (Long productId : productIds) {
            ReviewStats stats = getProductReviewStats(productId);
            result.put(productId, stats);
        }

        return result;
    }
}
