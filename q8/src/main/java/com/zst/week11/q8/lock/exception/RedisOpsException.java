package com.zst.week11.q8.lock.exception;

/**
 * 用于标记Redis操作失败的异常类
 */
public class RedisOpsException extends RuntimeException {
    public RedisOpsException(String message) {
        super(message);
    }
}
