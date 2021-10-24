package com.zst.week11.q9.module.order.message;

import com.zst.week11.q9.message.MessageCodecUtils;
import com.zst.week11.q9.module.order.dto.OrderMessageDTO;
import com.zst.week11.q9.module.order.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderMessagePublisher {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void publishNewOrder(String price, String shippingAddress, long userId) {
        OrderMessageDTO dto = new OrderMessageDTO(userId, shippingAddress, price);
        stringRedisTemplate.convertAndSend(Order.class.getName(), MessageCodecUtils.encode(dto));
    }
}
