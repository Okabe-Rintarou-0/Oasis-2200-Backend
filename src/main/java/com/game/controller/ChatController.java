package com.game.controller;

import com.game.service.ChatService;
import com.game.utils.jwtUtils.JwtUtil;
import com.game.utils.logUtils.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "聊天模块")
@RequestMapping("/chat")
@RestController
public class ChatController {

    @Autowired
    private ChatService service;

    @MessageMapping("/chat/{token}")
    @SendTo("/topics/chat")
    public void sendChatMessage(String message, @DestinationVariable(value = "token") String token) {
        String username = JwtUtil.getUsernameFromToken(token);
        if (username == null) {
            LogUtil.print("chat: no username");
            return;
        }
        service.sendMessage(username, message);
        LogUtil.print("receive chat message: "+username+" - "+message);
    }

    @ApiOperation(value = "获取历史聊天信息",notes = "获取历史聊天信息")
    @RequestMapping(value = "/history/get", method = RequestMethod.GET)
    public List<String> getHistory() {
        return service.getMessages();
    }

    //测试用接口
    @ApiOperation(value = "清除历史聊天信息",notes = "清除历史聊天信息")
    @RequestMapping(value = "/history/clear", method = RequestMethod.GET)
    public void clearChatMessages() {
        service.clearMessages();
    }
}
