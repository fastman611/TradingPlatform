package com.kb.trading.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name = "review_stats")
public class ReviewStats extends BaseEntity{
    @Column(name = "target_id", nullable = false)
    private Long targetId;              // 统计目标ID（商品ID或用户ID）

    @Column(name = "target_type", nullable = false)
    private String targetType;          // 统计目标类型：PRODUCT, SELLER, BUYER

    private Integer totalReviews = 0;   // 总评价数

    private Integer rating1Count = 0;   // 1星评价数
    private Integer rating2Count = 0;   // 2星评价数
    private Integer rating3Count = 0;   // 3星评价数
    private Integer rating4Count = 0;   // 4星评价数
    private Integer rating5Count = 0;   // 5星评价数

    @Column(precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO; // 平均评分

    private Integer withImageCount = 0; // 带图评价数

    private Integer appendCount = 0;    // 追加评价数

    private Integer replyCount = 0;     // 回复评价数

    // 更新统计信息
    public void updateStats(Review review) {
        this.totalReviews++;

        // 更新评分统计
        switch (review.getRating()) {
            case 1: rating1Count++; break;
            case 2: rating2Count++; break;
            case 3: rating3Count++; break;
            case 4: rating4Count++; break;
            case 5: rating5Count++; break;
        }

        // 更新平均分
        int totalScore = rating1Count + rating2Count * 2 + rating3Count * 3 +
                rating4Count * 4 + rating5Count * 5;
        this.averageRating = BigDecimal.valueOf(totalScore)
                .divide(BigDecimal.valueOf(totalReviews), 2, BigDecimal.ROUND_HALF_UP);

        // 统计带图评价
        if (review.getImageUrls() != null && !review.getImageUrls().isEmpty()) {
            this.withImageCount++;
        }

        // 统计追加评价
        if (review.getType() == ReviewType.APPEND_REVIEW) {
            this.appendCount++;
        }

        // 统计有回复的评价
        if (review.getHasReply()) {
            this.replyCount++;
        }
    }

    // 移除评价时更新统计
    public void removeStats(Review review) {
        this.totalReviews--;

        // 更新评分统计
        switch (review.getRating()) {
            case 1: rating1Count--; break;
            case 2: rating2Count--; break;
            case 3: rating3Count--; break;
            case 4: rating4Count--; break;
            case 5: rating5Count--; break;
        }

        // 重新计算平均分
        if (totalReviews > 0) {
            int totalScore = rating1Count + rating2Count * 2 + rating3Count * 3 +
                    rating4Count * 4 + rating5Count * 5;
            this.averageRating = BigDecimal.valueOf(totalScore)
                    .divide(BigDecimal.valueOf(totalReviews), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            this.averageRating = BigDecimal.ZERO;
        }

        // 更新其他统计
        if (review.getImageUrls() != null && !review.getImageUrls().isEmpty()) {
            this.withImageCount--;
        }

        if (review.getType() == ReviewType.APPEND_REVIEW) {
            this.appendCount--;
        }

        if (review.getHasReply()) {
            this.replyCount--;
        }
    }
}
