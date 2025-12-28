package com.kb.trading.controller;
import com.kb.trading.entity.Review;
import com.kb.trading.entity.ReviewStats;
import com.kb.trading.entity.ReviewType;
import com.kb.trading.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // 创建商品评价
    @PostMapping("/product/create")
    public ResponseEntity<Map<String, Object>> createProductReview(
            @RequestParam Long orderId,
            @RequestParam Long buyerId,
            @RequestParam Long productId,
            @RequestParam Integer rating,
            @RequestParam String content,
            @RequestParam(required = false) List<String> imageUrls,
            @RequestParam(required = false, defaultValue = "false") Boolean isAnonymous) {

        try {
            Review review = reviewService.createProductReview(
                    orderId, buyerId, productId, rating, content, imageUrls, isAnonymous);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "评价成功");
            response.put("review", review);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 创建买家评价（卖家评价买家）
    @PostMapping("/buyer/create")
    public ResponseEntity<Map<String, Object>> createBuyerReview(
            @RequestParam Long orderId,
            @RequestParam Long sellerId,
            @RequestParam Long buyerId,
            @RequestParam Integer rating,
            @RequestParam String content) {

        try {
            Review review = reviewService.createBuyerReview(
                    orderId, sellerId, buyerId, rating, content);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "买家评价成功");
            response.put("review", review);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 追加评价
    @PostMapping("/append")
    public ResponseEntity<Map<String, Object>> createAppendReview(
            @RequestParam Long parentReviewId,
            @RequestParam Long userId,
            @RequestParam String content,
            @RequestParam(required = false) List<String> imageUrls) {

        try {
            Review review = reviewService.createAppendReview(
                    parentReviewId, userId, content, imageUrls);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "追加评价成功");
            response.put("review", review);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 回复评价
    @PostMapping("/reply")
    public ResponseEntity<Map<String, Object>> replyToReview(
            @RequestParam Long reviewId,
            @RequestParam Long respondentId,
            @RequestParam String replyContent) {

        try {
            Review review = reviewService.replyToReview(reviewId, respondentId, replyContent);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "回复成功");
            response.put("review", review);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取评价详情
    @GetMapping("/{reviewId}")
    public ResponseEntity<Map<String, Object>> getReview(@PathVariable Long reviewId) {
        try {
            Review review = reviewService.getReviewById(reviewId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("review", review);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取订单评价
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderReviews(@PathVariable Long orderId) {
        try {
            List<Review> reviews = reviewService.getReviewsByOrder(orderId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("reviews", reviews);
            response.put("count", reviews.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取商品评价列表（分页）
    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy) {

        try {
            Page<Review> reviewPage = reviewService.getProductReviews(productId, page, size, sortBy);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("reviews", reviewPage.getContent());
            response.put("totalPages", reviewPage.getTotalPages());
            response.put("totalElements", reviewPage.getTotalElements());
            response.put("currentPage", page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取商品评价统计
    @GetMapping("/product/{productId}/stats")
    public ResponseEntity<Map<String, Object>> getProductReviewStats(@PathVariable Long productId) {
        try {
            ReviewStats stats = reviewService.getProductReviewStats(productId);
            Map<Integer, Long> distribution = reviewService.getProductRatingDistribution(productId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);
            response.put("ratingDistribution", distribution);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取卖家评价统计
    @GetMapping("/seller/{sellerId}/stats")
    public ResponseEntity<Map<String, Object>> getSellerReviewStats(@PathVariable Long sellerId) {
        try {
            ReviewStats stats = reviewService.getSellerReviewStats(sellerId);

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

    // 获取买家评价统计
    @GetMapping("/buyer/{buyerId}/stats")
    public ResponseEntity<Map<String, Object>> getBuyerReviewStats(@PathVariable Long buyerId) {
        try {
            ReviewStats stats = reviewService.getBuyerReviewStats(buyerId);

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

    // 点赞评价
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<Map<String, Object>> likeReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) {

        try {
            Review review = reviewService.likeReview(reviewId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "点赞成功");
            response.put("review", review);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 取消点赞
    @PostMapping("/{reviewId}/unlike")
    public ResponseEntity<Map<String, Object>> unlikeReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) {

        try {
            Review review = reviewService.unlikeReview(reviewId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "取消点赞成功");
            response.put("review", review);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 删除评价
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Map<String, Object>> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) {

        try {
            reviewService.deleteReview(reviewId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "评价删除成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 检查是否可以评价
    @GetMapping("/check-eligibility")
    public ResponseEntity<Map<String, Object>> checkReviewEligibility(
            @RequestParam Long orderId,
            @RequestParam Long userId) {

        try {
            Map<String, Object> eligibility = reviewService.checkReviewEligibility(orderId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("eligibility", eligibility);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取热门评价
    @GetMapping("/product/{productId}/hot")
    public ResponseEntity<Map<String, Object>> getHotReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "5") int limit) {

        try {
            List<Review> hotReviews = reviewService.getHotReviews(productId, limit);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("hotReviews", hotReviews);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取带图评价
    @GetMapping("/product/{productId}/with-images")
    public ResponseEntity<Map<String, Object>> getReviewsWithImages(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<Review> reviewsWithImages = reviewService.getReviewsWithImages(productId, limit);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("reviewsWithImages", reviewsWithImages);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取用户评价列表
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserReviews(
            @PathVariable Long userId,
            @RequestParam ReviewType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Page<Review> userReviews = reviewService.getUserReviews(userId, type, page, size);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("reviews", userReviews.getContent());
            response.put("totalPages", userReviews.getTotalPages());
            response.put("totalElements", userReviews.getTotalElements());
            response.put("currentPage", page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 批量获取商品评价统计
    @PostMapping("/batch-stats")
    public ResponseEntity<Map<String, Object>> batchGetProductReviewStats(
            @RequestBody List<Long> productIds) {

        try {
            Map<Long, ReviewStats> statsMap = reviewService.batchGetProductReviewStats(productIds);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("statsMap", statsMap);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取评价类型列表
    @GetMapping("/types")
    public ResponseEntity<Map<String, Object>> getReviewTypes() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);

        List<Map<String, String>> types = List.of(
                Map.of("code", "PRODUCT_REVIEW", "name", "商品评价", "description", "买家对商品的评价"),
                Map.of("code", "SELLER_REVIEW", "name", "卖家评价", "description", "买家对卖家的评价"),
                Map.of("code", "BUYER_REVIEW", "name", "买家评价", "description", "卖家对买家的评价"),
                Map.of("code", "APPEND_REVIEW", "name", "追加评价", "description", "买家追加的评价")
        );

        response.put("types", types);
        return ResponseEntity.ok(response);
    }
}
