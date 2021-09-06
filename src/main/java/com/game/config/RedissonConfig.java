package com.game.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lzh
 * @Title: Redisson配置类
 * @Package
 * @Description:
 * @date 2021/8/24 14:37
 */

@Configuration
public class RedissonConfig {
    @Value("${spring.redis.host}")
    String host;

    @Value("${spring.redis.port}")
    String masterPort;

//    @Value("${spring.redis.cluster.nodes}")
//    String redisNodes;

    @Value("${spring.redis.password}")
    String pwd;

    @Bean
    RedissonClient redisson() {
        Config config = new Config();
        String redisUrlFormat = "redis://%s:%s";
//        if (redisNodes != null) {
//            String[] redisNodeArray = redisNodes.split(",");
//            for (int i = 0; i < redisNodeArray.length; ++i)
//                redisNodeArray[i] = redisNodeArray[i] = String.format("redis://%s", redisNodeArray[i]);
//            config
//                    .useMasterSlaveServers()
//                    .setMasterAddress(redisNodeArray[0])
//                    .addSlaveAddress(redisNodeArray);
//        }

        config
                .useSingleServer()//目前用的是单点模式
                .setAddress(String.format(redisUrlFormat, host, masterPort))
                .setPassword(pwd);
        return Redisson.create(config);
    }

    @Bean
    String sharedRoomLockKey() {
        return "SHARED_ROOM_LOCK_KEY";
    }
}
