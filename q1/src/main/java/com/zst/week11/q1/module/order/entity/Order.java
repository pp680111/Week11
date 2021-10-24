package com.zst.week11.q1.module.order.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Order implements Serializable {
    private long id;
    private long userId;
    private String shippingAddress;
    private int status;
    private String price;
    private long createTime;
    private long updateTime;

    public static Order def() {
        Order entity = new Order();
        entity.setStatus(1);
        return entity;
    }
}
