package com.zst.week11.q1.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

//@Configuration
public class RedisCacheConfiguration {
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public RedisCacheManager redisCacheManager() {
        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory).build();
    }
}
