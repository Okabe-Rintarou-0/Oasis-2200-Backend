package com.game.context;

import com.game.annotation.Context;
import com.game.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Context
public class ChatContext {
    private ConcurrentLinkedQueue<ChatMessage> eventQueue = new ConcurrentLinkedQueue<>();

    public boolean addEvent(ChatMessage chatMessage) {
        enqueue(chatMessage);
        return true;
    }

    private void enqueue(ChatMessage chatMessage) {
        eventQueue.add(chatMessage);
        if (eventQueue.size() > 20) {
            eventQueue.remove();
        }
    }

    public List<ChatMessage> getEventQueue() {
        return new ArrayList<>(eventQueue);
    }

    public void clearEventQueue() {
        eventQueue.clear();
    }
}
