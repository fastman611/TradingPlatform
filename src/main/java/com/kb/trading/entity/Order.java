package com.kb.trading.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Entity
@Table(name = "orders")
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String orderNo;           // 订单号（唯一）

    @Column(nullable = false)
    private Long buyerId;             // 买家ID

    private String buyerName;         // 买家姓名（冗余字段）

    @Column(nullable = false)
    private Long sellerId;            // 卖家ID

    private String sellerName;        // 卖家姓名（冗余字段）

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;   // 订单总金额

    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO; // 优惠金额

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal payableAmount; // 应付金额（总金额-优惠）

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING_PAYMENT; // 订单状态

    @Column(length = 500)
    private String buyerAddress;      // 收货地址

    private String buyerPhone;        // 收货电话

    private String buyerNote;         // 买家留言

    @Column(length = 500)
    private String sellerNote;        // 卖家备注

    private Date paymentTime;         // 付款时间

    private Date shippingTime;        // 发货时间

    private Date deliveryTime;        // 送达时间

    private Date completedTime;       // 完成时间

    private String shippingCompany;   // 物流公司

    private String trackingNumber;    // 物流单号

    @Column(length = 1000)
    private String cancelReason;      // 取消原因

    @Column(precision = 10, scale = 2)
    private BigDecimal refundAmount;  // 退款金额

    private String refundReason;      // 退款原因
}
