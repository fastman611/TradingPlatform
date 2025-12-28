package com.kb.trading.entity;

public enum ProductCategory {
    ELECTRONICS("电子产品"),
    CLOTHING("服装鞋帽"),
    FURNITURE("家具家居"),
    BOOKS("图书音像"),
    SPORTS("运动户外"),
    DAILY_USE("日用百货"),
    DIGITAL("数码产品"),
    VEHICLES("车辆运输"),
    REAL_ESTATE("房产租售"),
    SERVICES("生活服务"),
    OTHER("其他");

    private final String description;

    ProductCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
