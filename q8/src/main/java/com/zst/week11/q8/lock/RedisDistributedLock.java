package com.zst.week11.q8.lock;

import com.zst.week11.q8.lock.exception.RedisOpsException;
import com.zst.week11.q8.utils.SpringContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁
 */
public class RedisDistributedLock {
    private static final Logger logger = LoggerFactory.getLogger(RedisDistributedLock.class);
    /** 默认过期时间：30000ms*/
    private static final long DEFAULT_EXPIRED_TIME = 10_000L;
    private static RedisScript<Long> luaUnlockScript;
    private StringRedisTemplate stringRedisTemplate;


    static {
        try {
            Resource resource = new ClassPathResource("lua/unlock.lua");
            luaUnlockScript = RedisScript.of(resource, Long.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public RedisDistributedLock() {
        this.stringRedisTemplate = SpringContextUtils.getBean(StringRedisTemplate.class);
    }

    /**
     * 非阻塞的获取指定key的分布式锁
     * @param lockKey 锁key
     * @return 当成功获取锁时，返回一个时间戳，用于解锁。获取锁失败时返回-1
     * @throw IllegalArgumentException lockKey参数为空时抛出此异常
     * @throw RedisOpsException 执行redis操作返回null是抛出此异常
     */
    public long lock(String lockKey) {
        if (StringUtils.isEmpty(lockKey)) {
            throw new IllegalArgumentException("lockKey cannot empty");
        }

        long stamp = System.currentTimeMillis();
        if (acquire(lockKey, stamp, DEFAULT_EXPIRED_TIME)) {
            return stamp;
        } else {
            return -1L;
        }
    }

    /**
     * 非阻塞的释放指定key的redis分布式锁
     * @param lockKey
     * @param stamp 解锁时的凭证
     * @throw RedisOpsException 执行redis操作返回null是抛出此异常
     * @throw IllegalArgumentException lockKey参数为空时抛出此异常
     */
    public void unlock(String lockKey, long stamp) {
        if (StringUtils.isEmpty(lockKey)) {
            throw new IllegalArgumentException("lockKey cannot empty");
        }

        if (luaUnlockScript == null) {
            throw new RuntimeException("unlock script is empty");
        }

        if (!release(lockKey, stamp)) {
            throw new IllegalStateException("unlock failed");
        }
    }

    private boolean acquire(String lockKey, long lockStamp, long expiredTime) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().setIfAbsent(lockKey, String.valueOf(lockStamp),
                expiredTime, TimeUnit.MILLISECONDS)).orElseThrow(() -> new RedisOpsException("Acquire redis lock failed"));
    }

    private boolean release(String lockKey, long lockStamp) {
        return Optional.ofNullable(stringRedisTemplate.execute(luaUnlockScript, Collections.singletonList(lockKey), String.valueOf(lockStamp)))
                .orElseThrow(() -> new RedisOpsException("Release redis lock failed")) == 1;
    }
}
