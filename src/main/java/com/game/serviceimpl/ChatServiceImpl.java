package com.game.serviceimpl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.game.context.ChatContext;
import com.game.models.ChatMessage;
import com.game.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

   @Autowired
   private ChatContext chatContext;

   @Autowired
   private SimpMessagingTemplate brokerMessagingTemplate;

   @Override
   public void sendMessage(String category, String name, String description) {
      ChatMessage chatMessage = new ChatMessage();
      chatMessage.setCategory(category);
      chatMessage.setDescription(description);
      chatMessage.setName(name);
      //chatMessage.setTime(new Date());
      chatContext.addEvent(chatMessage);
      brokerMessagingTemplate.convertAndSend("/topics/chat", JSON.toJSONString(chatMessage, SerializerFeature.BrowserCompatible));
   }

   @Override
   public List<ChatMessage> getMessages(){
      return chatContext.getEventQueue();
   }

   @Override
   public void clearMessages(){
      chatContext.clearEventQueue();
   }

}
