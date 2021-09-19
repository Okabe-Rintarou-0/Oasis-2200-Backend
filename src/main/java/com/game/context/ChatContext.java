package com.game.context;

import com.game.annotation.Context;
import com.game.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Context
public class ChatContext {
    private ConcurrentLinkedQueue<String> eventQueue = new ConcurrentLinkedQueue<>();

    public void addEvent(String chatStr) {
        eventQueue.add(chatStr);
        if (eventQueue.size() > 30) {
            eventQueue.remove();
        }
    }

    public List<String> getEventQueue() {
        return new ArrayList<>(eventQueue);
    }

    public void clearEventQueue() {
        eventQueue.clear();
    }
}
