package com.game.serviceimpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.game.context.ChatContext;
import com.game.models.ChatMessage;
import com.game.serviceimpl.ChatServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;


@RunWith(MockitoJUnitRunner.class)
public class ChatServiceTest {
    @Mock
    ChatContext chatContext;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private ChatServiceImpl chatService;

    @Test
    @DisplayName("测试所有接口")
    public void testSendMessage() {
        chatService.sendMessage("category", "name", "description");
        chatService.clearMessages();

        List<ChatMessage> messageList = new ArrayList<>();
        Mockito.when(chatContext.getEventQueue()).thenReturn(messageList);
        Assertions.assertEquals(chatService.getMessages(), messageList);
    }

//    @Test
//    @DisplayName("测试发送警告消息")
//    public void testSendWarning(){
//        ChatMessage auditEvent = new ChatMessage();
//        auditEvent.setCategory("chat");
//        auditEvent.setDescription("description");
//        auditEvent.setName("message");
//        auditEvent.setCount(1);
//        auditEvent.setLevel("Warning");
//        auditEventService.sendWarning("chat","message","description");
//        Mockito.verify(simpMessagingTemplate).convertAndSend("/topics/chat",JSON.toJSONString(auditEvent, SerializerFeature.BrowserCompatible));
//    }
//
//    @Test
//    @DisplayName("测试发送出错消息")
//    public void testSendError(){
//        ChatMessage auditEvent = new ChatMessage();
//        auditEvent.setCategory("chat");
//        auditEvent.setDescription("description");
//        auditEvent.setName("message");
//        auditEvent.setCount(1);
//        auditEvent.setLevel("Error");
//        auditEventService.sendError("chat","message","description");
//        Mockito.verify(simpMessagingTemplate).convertAndSend("/topics/chat",JSON.toJSONString(auditEvent, SerializerFeature.BrowserCompatible));
//    }


}
