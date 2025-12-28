package com.kb.trading.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@Table(name = "product")
public class Product extends BaseEntity{
    @Column(nullable = false)
    private String title;           // 商品标题

    @Column(columnDefinition = "TEXT")
    private String description;     // 商品描述

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;       // 商品价格

    @Column(precision = 10, scale = 2)
    private BigDecimal originalPrice; // 原价（用于显示折扣）

    @Column(name = "main_image")
    private String mainImage;       // 主图URL

    @Column(name = "image",length = 2000)// 增加长度限制
    private String images;          // 多张图片URL，用逗号分隔


    private String category;        // 商品分类：如"电子产品","服装","家具"等

    private String location;        // 商品所在地（同城交易重要字段）

    @Column(nullable = false)
    private Integer status = 1;     // 状态：0-下架 1-上架 2-已售出 3-已删除

    private Integer stock = 1;      // 库存（默认1，表示二手物品）

    @Column(nullable = false)
    private Long sellerId;          // 卖家ID（关联User表）

    private String sellerName;      // 卖家昵称（冗余字段，避免频繁join查询）

    private String contactPhone;    // 联系电话

    private Integer viewCount = 0;  // 浏览次数

    private Integer likeCount = 0;  // 收藏/点赞数
    // 添加获取点赞状态的方法（需要传入用户ID）
    public Boolean getLikedByUser(Long userId) {
        // 这个方法需要在Service中实现，这里只是定义
        return null;
    }
    // 添加一个辅助方法，方便操作图片列表
    public void addImage(String imageUrl) {
        if (this.images == null || this.images.isEmpty()) {
            this.images = imageUrl;
        } else {
            this.images = this.images + "," + imageUrl;
        }
    }

    public List<String> getImageList() {
        if (this.images == null || this.images.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(this.images.split(","));
    }

    public void setImageList(List<String> imageList) {
        if (imageList == null || imageList.isEmpty()) {
            this.images = null;
        } else {
            this.images = String.join(",", imageList);
        }
    }
}
