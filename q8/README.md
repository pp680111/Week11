## 分布式锁

加锁操作由set nx机制来实现，提供有超时时间和无超时时间两种加锁方式

解锁操作由lua脚本来实现移除key时检查对应的val值是否正确的情况

后续可以参照ReentrantLock进行优化完善，比如提供tryLock和阻塞的获取锁的方式

问题：
1. 缺少队列机制，可能会出现锁的竞争者超长时间无法获取到锁的问题

## 库存计数器

增加计数器时先判断key是否存在，不存在则执行set操作初始化计数器，存在则执行incrby操作增加对应数值

减少计数器时先判断key是否存在以及key的数值是否满足本次操作所需要减少的数值，满足条件时执行decrby减少对应数值，不满足时返回0表示操作失败



## 问题记录

1. RedisScript创建时指定了一个返回值类型，如果lua脚本里返回的类型与RedisScript指定的返回值类型不同的话，那么执行时的返回值就是空

   ```
   // 创建时指定了Long类型为返回值类型
   RedisScript<Long> script = RedisScript.of(resource, Long.class);
   
   // ARGV[1]的类型为String，返回此值的话redisTemplate.execute方法返回的值就是null，必须通过tonumber函数将返回值转换为数字类型，execute方法返回值才是非空
   if redis.call('get', KEYS[1]) then
       return redis.call('incrby', KEYS[1], ARGV[1])
   else
       redis.call('set',KEYS[1], ARGV[1]);
       return tonumber(ARGV[1]);
   end
   ```

   