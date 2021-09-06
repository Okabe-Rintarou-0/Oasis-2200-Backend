package com.game.listener;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.game.utils.redisUtils.RedisReceiverDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.util.concurrent.CountDownLatch;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/20 19:16
 */
@Configuration
public class RedisMessageListener {
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            @Autowired
                                                    MessageListenerAdapter roomJoinListenerAdapter,
                                            @Autowired
                                                    MessageListenerAdapter chatListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
//                ObjectMapper.DefaultTyping.NON_FINAL,
//                JsonTypeInfo.As.WRAPPER_OBJECT);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        roomJoinListenerAdapter.setSerializer(jackson2JsonRedisSerializer);
        container.addMessageListener(roomJoinListenerAdapter, new PatternTopic("room"));
        chatListenerAdapter.setSerializer(jackson2JsonRedisSerializer);
        container.addMessageListener(chatListenerAdapter, new PatternTopic("chat"));
        return container;
    }

    @Bean
    CountDownLatch latch() {
        return new CountDownLatch(1);
    }

    @Bean
    MessageListenerAdapter chatListenerAdapter(RedisReceiverDelegate receiver) {
        return new MessageListenerAdapter(receiver, "receiveChatMessage");
    }

    @Bean
    MessageListenerAdapter roomJoinListenerAdapter(RedisReceiverDelegate receiver) {
        return new MessageListenerAdapter(receiver, "receiveRoomJoinMessage");
    }
}
