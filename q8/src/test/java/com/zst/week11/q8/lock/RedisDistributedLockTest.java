package com.zst.week11.q8.lock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.locks.LockSupport;
import java.util.stream.IntStream;

@SpringBootTest
public class RedisDistributedLockTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testLock() {
        redisTemplate.delete("key");

        RedisDistributedLock lock = new RedisDistributedLock();
        long stamp = lock.lock("key");
        Assertions.assertNotEquals(-1, stamp);

        long stamp2 = lock.lock("key");
        Assertions.assertEquals(-1, stamp2);
    }

    @Test
    public void testConcurrentAcquireLock() {
        redisTemplate.delete("key");
        IntStream.range(0, 10).forEach(i -> {
            Thread t = new Thread(() -> {
                RedisDistributedLock lock = new RedisDistributedLock();
                for (;;) {
                    long stamp = lock.lock("key");
                    if (stamp == -1) {
                        System.err.println(String.format("线程{%s}获取分布式锁失败，暂停5s", Thread.currentThread().getName()));
                        LockSupport.parkNanos(5 * 1000 * 1000 * 1000L);
                    } else {
                        System.err.println(String.format("线程{%s}获取分布式锁成功, 其他线程等待默认10s之后锁释放", Thread.currentThread().getName()));
                        break;
                    }
                }
            });
            t.setName("Thread " + i);
            t.start();
        });
        LockSupport.park();
    }

    @Test
    public void testUnlock() {
        redisTemplate.delete("key");

        RedisDistributedLock lock = new RedisDistributedLock();
        long stamp = lock.lock("key");

        lock.unlock("key", stamp);
        Assertions.assertNull(redisTemplate.opsForValue().get("key"));
    }

    @Test
    public void testUnlockNotExistStamp() {
        redisTemplate.delete("key");

        RedisDistributedLock lock = new RedisDistributedLock();
        lock.lock("key");

        Assertions.assertThrows(IllegalStateException.class, () -> lock.unlock("key", System.currentTimeMillis()));
    }

    @Test
    public void testUnlockNotExistsKey() {
        redisTemplate.delete("key");
        RedisDistributedLock lock = new RedisDistributedLock();
        Assertions.assertThrows(IllegalStateException.class, () -> lock.unlock("key", System.currentTimeMillis()));
    }

    @Test
    public void testConcurrentLockAndUnlock() {
        redisTemplate.delete("key");
        IntStream.range(0, 10).forEach(i -> {
            Thread t = new Thread(() -> {
                RedisDistributedLock lock = new RedisDistributedLock();
                for (;;) {
                    long stamp = lock.lock("key");
                    long pauseTime = (long) (Math.random() * 10000);
                    if (stamp == -1) {
                        System.err.println(String.format("线程{%s}获取分布式锁失败，暂停%dms后重新尝试", Thread.currentThread().getName(), pauseTime));
                        LockSupport.parkNanos(pauseTime * 1000 * 1000L);
                    } else {
                        System.err.println(String.format("线程{%s}获取分布式锁成功, 等待%dms后释放", Thread.currentThread().getName(), pauseTime));
                        LockSupport.parkNanos(pauseTime * 1000 * 1000L);
                        lock.unlock("key", stamp);
                        System.err.println(String.format("线程{%s}释放分布式锁成功, 等待%dms后重新获取", Thread.currentThread().getName(), pauseTime));
                        LockSupport.parkNanos(pauseTime * 1000 * 1000L);
                    }
                }
            });
            t.setName("Thread " + i);
            t.start();
        });
        LockSupport.park();
    }
}
