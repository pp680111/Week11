package com.zst.week11.q9.module.order.message;

import com.zst.week11.q9.message.MessageCodecUtils;
import com.zst.week11.q9.module.order.dto.OrderMessageDTO;
import com.zst.week11.q9.module.order.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class OrderMessageSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(OrderMessageSubscriber.class);
    @Autowired
    private RedisMessageListenerContainer container;

    @PostConstruct
    public void postConstruct() {
        container.addMessageListener(orderMessageListener(), ChannelTopic.of(Order.class.getName()));
    }

    private MessageListener orderMessageListener() {
        return (msg, topic) -> {
            OrderMessageDTO dto = MessageCodecUtils.decode(msg, OrderMessageDTO.class);
            if (dto == null) {
                logger.error("received order message is empty");
            } else {
                // 此处省略调用处理订单数据的逻辑
                logger.info("receive order " + dto.toString());
            }
        };
    }
}
