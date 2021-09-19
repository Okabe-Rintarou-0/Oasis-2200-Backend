package com.game.service;

import com.game.models.ChatMessage;

import java.util.List;
import java.util.Map;

public interface ChatService {
   void sendMessage(String username, String message);
   List<String> getMessages();
   void clearMessages();

}
