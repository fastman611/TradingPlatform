package com.kb.trading.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "favorite", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "product_id"})
})
public class Favorite extends BaseEntity {
    @Column(name = "user_id", nullable = false)
    private Long userId;          // 收藏用户ID

    @Column(name = "product_id", nullable = false)
    private Long productId;       // 收藏商品ID

    private String note;          // 收藏备注（可选）

    // 关联商品实体（可选）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    // 关联用户实体（可选）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
