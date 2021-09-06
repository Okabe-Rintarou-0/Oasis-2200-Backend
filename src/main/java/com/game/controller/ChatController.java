package com.game.controller;

import com.game.models.ChatMessage;
import com.game.service.ChatService;
import com.game.utils.jwtUtils.JwtUtil;
import com.game.utils.logUtils.LogUtil;
import com.game.utils.messageUtils.MessageUtil;
import com.game.utils.redisUtils.RedisMessageManager;
import com.game.utils.wordUtils.WordFilter;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "聊天模块")
@RestController
public class ChatController {

    @Autowired
    private ChatService service;

    @Autowired
    String intraNetIp;

    @Autowired
    RedisMessageManager redisMessageManager;

    @MessageMapping("/chatroom/{token}")
    @SendTo("/topics/chat")
    public void sendChatMessage(String message, @DestinationVariable(value = "token") String token) {
        String username = JwtUtil.getUsernameFromToken(token);
        if (username == null) {
            return;
        }
        message= WordFilter.doFilter(message);
        service.sendMessage("chat", username, message);
        LogUtil.print("receive chat message!");
        LogUtil.print(username + ": " + message);
        redisMessageManager.sendObject("chat", MessageUtil.createRedisMessage(intraNetIp, message));
    }

    @RequestMapping(value = "/getChatMessages", method = RequestMethod.GET)
    public List<ChatMessage> getChatMessages() {
//        LogUtil.print("request: getChatMessages");
        return service.getMessages();
    }

    //测试用接口
    @RequestMapping(value = "/clearChatMessages", method = RequestMethod.GET)
    public void clearChatMessages() {
        service.clearMessages();
    }
}
