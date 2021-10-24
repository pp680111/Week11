package com.zst.week11.q9.module.order.message;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OrderMessagePublisherTest {
    @Autowired
    private OrderMessagePublisher messagePublisher;

    @Test
    public void testPublishOrder() {
        messagePublisher.publishNewOrder("3899", "广州市天河区", System.currentTimeMillis());
    }
}
