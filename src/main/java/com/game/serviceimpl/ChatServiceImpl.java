package com.game.serviceimpl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.game.context.ChatContext;
import com.game.models.ChatMessage;
import com.game.service.ChatService;
import com.game.utils.messageUtils.MessageUtil;
import com.game.utils.redisUtils.RedisMessageManager;
import com.game.utils.wordUtils.WordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

   @Autowired
   private ChatContext chatContext;

   @Autowired
   private SimpMessagingTemplate simpMessagingTemplate;

   @Autowired
   RedisMessageManager redisMessageManager;

   @Autowired
   String intraNetIp;

   @Override
   public void sendMessage(String username, String message) {
      ChatMessage chatMessage = new ChatMessage();
      String messageStr= null;
      try {
         messageStr = URLDecoder.decode(message, "UTF-8");
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      }
      messageStr= WordFilter.doFilter(messageStr);
      try {
         message= URLEncoder.encode(messageStr, "UTF-8");
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      }
      //chatMessage.setCategory(category);
      chatMessage.setDescription(message);
      chatMessage.setName(username);
      String chatStr=JSON.toJSONString(chatMessage, SerializerFeature.BrowserCompatible);
      chatContext.addEvent(chatStr);
      simpMessagingTemplate.convertAndSend("/topics/chat", chatStr);
      redisMessageManager.sendObject("chat", MessageUtil.createRedisMessage(intraNetIp, chatStr));
   }

   @Override
   public List<String> getMessages(){
      return chatContext.getEventQueue();
   }

   @Override
   public void clearMessages(){
      chatContext.clearEventQueue();
   }

}
