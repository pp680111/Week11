package com.zst.week11.q9.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisConfiguration {
    @Autowired
    private RedisConnectionFactory factory;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container =  new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.start();
        return container;
    }
}
