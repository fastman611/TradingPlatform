package com.kb.trading.entity;

public enum ReviewType {
    PRODUCT_REVIEW("商品评价"),      // 对商品的评价
    SELLER_REVIEW("卖家评价"),       // 对卖家的评价
    BUYER_REVIEW("买家评价"),        // 对买家的评价（卖家评价买家）
    APPEND_REVIEW("追加评价");       // 追加评价

    private final String description;

    ReviewType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
