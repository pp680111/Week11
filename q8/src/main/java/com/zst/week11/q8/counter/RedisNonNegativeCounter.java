package com.zst.week11.q8.counter;

import com.zst.week11.q8.exception.RedisOpsException;
import com.zst.week11.q8.utils.SpringContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.Optional;

public class RedisNonNegativeCounter {
    private static final Logger logger = LoggerFactory.getLogger(RedisNonNegativeCounter.class);

    private StringRedisTemplate redisTemplate;
    private static RedisScript<Long> incrScript;
    private static RedisScript<Long> decrScript;

    public RedisNonNegativeCounter() {
        redisTemplate = SpringContextUtils.getBean(StringRedisTemplate.class);
        try {
            incrScript = RedisScript.of(new ClassPathResource("lua/counterIncr.lua"), Long.class);
            decrScript = RedisScript.of(new ClassPathResource("lua/counterDecr.lua"), Long.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 指定key对应的计数器加1
     * @param key 计数器key
     * @return 新的计数器数值
     */
    public long incr(String key) {
        return incrBy(key, 1);
    }

    /**
     * 指定key对应的计数器加上指定数量，当计数器不存在时初始化计数器的值为amount
     * @param key 计数器key
     * @param amount 增加的数量
     * @return 新的计数器数值
     * @throws RedisOpsException redis操作失败时抛出此异常
     */
    public long incrBy(String key, long amount) {
        if (StringUtils.isEmpty(key) || amount < 0) {
            throw new IllegalArgumentException();
        }
        if (incrScript == null) {
            throw new RuntimeException("initialize RedisStockCounter error, counterIncr.lua script load failed");
        }

        return Optional.ofNullable(redisTemplate.execute(incrScript, Collections.singletonList(key), String.valueOf(amount)))
                .orElseThrow(() -> new RedisOpsException(String.format("decrease counter %s failed", key)));
    }

    /**
     * 指定key对应的计数器减1
     * @param key 计数器key
     * @return 新的计数器数值
     */
    public long decr(String key) {
        return decrBy(key, 1);
    }

    /**
     * 指定key对应的计数器减去指定数量
     * @param key 计数器key
     * @param amount 减少的数量
     * @return 新的计数器数值
     * @throws RedisOpsException redis操作失败时抛出此异常
     * @throws IllegalStateException 当计数器不存在或者不满足减少数量时，抛出异常
     */
    public long decrBy(String key, long amount) {
        if (StringUtils.isEmpty(key) || amount < 0) {
            throw new IllegalArgumentException();
        }
        if (decrScript == null) {
            throw new RuntimeException("initialize RedisStockCounter error, counterDecr.lua script load failed");
        }

        Long afterVal = Optional.ofNullable(redisTemplate.execute(decrScript, Collections.singletonList(key), String.valueOf(amount)))
                .orElseThrow(() -> new RedisOpsException(String.format("decrease counter %s failed", key)));
        if (afterVal < 0) {
            throw new IllegalStateException();
        }
        return afterVal;
    }
}
