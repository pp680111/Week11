package com.zst.week11.q9.module.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderMessageDTO {
    private long userId;
    private String shippingAddress;
    private String price;
}
