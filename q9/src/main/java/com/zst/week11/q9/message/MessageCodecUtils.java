package com.zst.week11.q9.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;

public class MessageCodecUtils {
    private static final Logger logger = LoggerFactory.getLogger(MessageCodecUtils.class);

    public static <T> T decode(Message msg, Class<T> clazz) {
        try {
            String content = new String(msg.getBody(), StandardCharsets.UTF_8);
            return JSONObject.parseObject(content, clazz);
        } catch (Exception e) {
            logger.error("message decode error", e);
        }
        return null;
    }

    public static Object encode(Object msg) {
        Assert.notNull(msg, "encode message cannot empty");
        return JSON.toJSONString(msg);
    }
}
