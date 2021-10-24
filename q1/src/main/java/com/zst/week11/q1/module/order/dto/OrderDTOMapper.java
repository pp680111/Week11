package com.zst.week11.q1.module.order.dto;

import com.zst.week11.q1.module.order.entity.Order;

public class OrderDTOMapper {
    public static OrderDTO mapToDTO(Order entity) {
        if (entity == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setId(entity.getId());
        dto.setPrice(entity.getPrice());
        dto.setShippingAddress(entity.getShippingAddress());
        dto.setStatus(entity.getStatus());
        dto.setUserId(entity.getUserId());
        return dto;
    }
}
