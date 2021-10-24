package com.zst.week11.q1.module.order.dto;

import lombok.Data;

@Data
public class OrderDTO {
    private long id;
    private long userId;
    private String shippingAddress;
    private int status;
    private String price;
}
