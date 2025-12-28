package com.kb.trading.entity;
/**
 * 订单状态枚举
 */
public enum OrderStatus {
    PENDING_PAYMENT("待付款"),      // 下单后，等待付款
    PAID("已付款"),                 // 已支付，等待发货
    SHIPPED("已发货"),              // 已发货，等待收货
    DELIVERED("已送达"),            // 已送达，等待确认
    COMPLETED("已完成"),            // 订单完成
    CANCELLED("已取消"),            // 订单取消
    REFUNDING("退款中"),            // 退款申请中
    REFUNDED("已退款");             // 已退款

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
