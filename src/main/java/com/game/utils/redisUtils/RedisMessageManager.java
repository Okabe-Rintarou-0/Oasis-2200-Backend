package com.game.utils.redisUtils;

import com.alibaba.fastjson.JSON;
import com.game.dto.RoomFeatureDto;
import com.game.entity.Room;
import com.game.utils.logUtils.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description: To send redis messages.
 * @date 2021/8/21 10:48
 */
@Component
public class RedisMessageManager {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public void sendObject(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }
}
