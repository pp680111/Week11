package com.zst.week11.q8.counter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

@SpringBootTest
public class RedisNonNegativeCounterTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    public void prepareTestEnv() {
        redisTemplate.delete("stock");
    }

    @Test
    public void testInitCounter() {
        RedisNonNegativeCounter counter = new RedisNonNegativeCounter();
        Assertions.assertEquals(1, counter.incr("stock"));
    }

    @Test
    public void testIncr() {
        RedisNonNegativeCounter counter = new RedisNonNegativeCounter();
        long init = counter.incr("stock");
        long newCount = counter.incr("stock");
        Assertions.assertEquals(init + 1, newCount);
    }

    @Test
    public void testDecr() {
        RedisNonNegativeCounter counter = new RedisNonNegativeCounter();
        long init = counter.incrBy("stock", 10);
        long newCount = counter.decr("stock");
        Assertions.assertEquals(init - 1, newCount);
    }

    @Test
    public void testDecrNotExist() {
        RedisNonNegativeCounter counter = new RedisNonNegativeCounter();
        Assertions.assertThrows(IllegalStateException.class, () -> counter.decr("stock"));
    }

    @Test
    public void testDecrNotEnough() {
        RedisNonNegativeCounter counter = new RedisNonNegativeCounter();
        counter.incrBy("stock", 10);
        Assertions.assertThrows(IllegalStateException.class, () -> counter.decrBy("stock", 11));
    }

    @Test
    public void testDecrExactlyEnough() {
        RedisNonNegativeCounter counter = new RedisNonNegativeCounter();
        counter.incrBy("stock", 10);
        Assertions.assertDoesNotThrow(() -> counter.decrBy("stock", 10));
    }

    /**
     * 模拟并发减商品库存的情况，每次扣减库存成功时计数器+1
     * 一开始库存数量为1000，一共5001次尝试扣减库存，当操作次数对100取模为0的时候补充10库存数
     * 正确的结果是计数器次数为1500
     */
    @Test
    public void testConcurrentOperate() {
        RedisNonNegativeCounter counter = new RedisNonNegativeCounter();
        counter.incrBy("PS5", 1000);
        LongAdder adder = new LongAdder();
        IntStream.range(0, 5000).parallel().forEach(i -> {
            try {
                if (i % 100 == 0) {
                    counter.incrBy("PS5", 10);
                }
                counter.decr("PS5");
                adder.increment();
            } catch (Exception e) {
            }
        });
        Assertions.assertEquals(1500, adder.intValue());
    }
}
