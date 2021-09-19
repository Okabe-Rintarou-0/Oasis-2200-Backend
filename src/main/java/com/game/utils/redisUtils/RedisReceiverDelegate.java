package com.game.utils.redisUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.game.constants.NetworkConstants;
import com.game.context.ChatContext;
import com.game.utils.logUtils.LogUtil;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import com.game.utils.messageUtils.RedisMessage;
import com.game.utils.messageUtils.StompMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/20 19:38
 */
@Component
public class RedisReceiverDelegate {
    @Autowired
    private CountDownLatch countDownLatch;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ChatContext chatContext;

    @Autowired
    private String intraNetIp;

    public void receiveChatMessage(Object message) {
        RedisMessage redisMessage = JSONObject.parseObject(JSON.toJSONString(message), RedisMessage.class);
        if (!redisMessage.intraNetIp.equals(intraNetIp)) {
            String chatStr=redisMessage.message;
            LogUtil.info("redis receiveChatMessage: "+chatStr);
            simpMessagingTemplate.convertAndSend("/topics/chat", chatStr);
            chatContext.addEvent(chatStr);
        }
        countDownLatch.countDown();
    }

    public void receiveRoomJoinMessage(Object message) {
        LogUtil.print(String.format("Receive message: %s%n", JSON.toJSONString(message)));
        RedisMessage redisMessage = JSONObject.parseObject(JSON.toJSONString(message), RedisMessage.class);
        if (!redisMessage.intraNetIp.equals(intraNetIp)) {
            String roomId = redisMessage.message;
            String dest = String.format(NetworkConstants.TOPIC_ROOM_FORMAT, roomId);
            StompMessage stompMessage = MessageUtil.createStompMessage("room", "join");
            simpMessagingTemplate.convertAndSend(dest, stompMessage);
        }
        countDownLatch.countDown();
    }
}
