package com.kb.trading.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
@Entity
@Table(name = "cart_item", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "product_id"})
})
@Data
@EqualsAndHashCode(callSuper = true)
public class CartItem extends BaseEntity {
    @Column(name = "user_id", nullable = false)
    private Long userId;          // 用户ID

    @Column(name = "product_id", nullable = false)
    private Long productId;       // 商品ID

    @Column(nullable = false)
    private Integer quantity = 1; // 购买数量

    @Column(precision = 10, scale = 2)
    private BigDecimal unitPrice; // 商品单价（缓存）

    private String productTitle;  // 商品标题（缓存）

    private String productImage;  // 商品图片（缓存）

    private String productSpec;   // 商品规格（如颜色、尺寸）

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;  // 小计金额

    // 关联商品实体
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    // 关联用户实体
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /**
     * 计算小计金额
     */
    public void calculateSubtotal() {
        if (unitPrice != null && quantity != null) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
