package com.kb.trading.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "review")
@Data
@EqualsAndHashCode(callSuper = true)
public class Review extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private Long orderId;              // 关联的订单ID

    @Column(name = "product_id")
    private Long productId;            // 关联的商品ID

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;              // 买家ID

    private String buyerName;          // 买家昵称（冗余）

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;             // 卖家ID

    private String sellerName;         // 卖家昵称（冗余）

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewType type = ReviewType.PRODUCT_REVIEW; // 评价类型

    private Integer rating = 5;        // 评分 1-5星，默认5星

    @Column(columnDefinition = "TEXT")
    private String content;            // 评价内容

    @Column(name = "image_urls", length = 2000)
    private String imageUrls;          // 评价图片URL，逗号分隔

    private Boolean isAnonymous = false; // 是否匿名评价

    private Boolean hasReply = false;  // 是否有回复

    @Column(columnDefinition = "TEXT")
    private String replyContent;       // 回复内容

    private Integer replyCount = 0;    // 回复数量（用于追加评价）

    private Integer likeCount = 0;     // 点赞数

    private Integer viewCount = 0;     // 查看次数

    private Long parentId;             // 父评价ID（用于追加评价）

    // 注意：删除了所有 @ManyToOne 关联关系，避免字段名冲突

    // 获取图片列表
    public List<String> getImageList() {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return new ArrayList<>();
        }
        return List.of(imageUrls.split(","));
    }

    // 设置图片列表
    public void setImageList(List<String> images) {
        if (images == null || images.isEmpty()) {
            this.imageUrls = null;
        } else {
            this.imageUrls = String.join(",", images);
        }
    }

    // 添加图片
    public void addImage(String imageUrl) {
        List<String> images = getImageList();
        images.add(imageUrl);
        setImageList(images);
    }
}
