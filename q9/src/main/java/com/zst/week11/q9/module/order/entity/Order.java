package com.zst.week11.q9.module.order.entity;

import lombok.Data;

@Data
public class Order {
    private long id;
    private long userId;
    private String shippingAddress;
    private int status;
    private String price;
    private long createTime;
    private long updateTime;
}
