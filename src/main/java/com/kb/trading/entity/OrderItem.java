package com.kb.trading.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name = "order_item")
public class OrderItem extends BaseEntity{
    @Column(nullable = false)
    private Long orderId;             // 所属订单ID

    @Column(nullable = false)
    private Long productId;           // 商品ID

    private String productTitle;      // 商品标题（冗余）

    private String productImage;      // 商品图片（冗余）

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;     // 商品单价

    @Column(nullable = false)
    private Integer quantity;         // 购买数量

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;      // 小计金额（单价×数量）

    @Column(length = 500)
    private String productSpec;       // 商品规格（如颜色、尺寸）

    // 关联到商品实体（可选，根据需求）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", insertable = false, updatable = false)
    private Product product;

    // 关联到订单实体（可选）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", insertable = false, updatable = false)
    private Order order;
}
