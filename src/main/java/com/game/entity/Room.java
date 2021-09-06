package com.game.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;


@Data
@NoArgsConstructor
public class Room {
    private int hostId;
    //< userId gameId(在游戏中的id) > host 的gameId为0;
    private ConcurrentHashMap<Integer, Integer> clients;
    private Date createdTime;
    private boolean started = false;

    private final int capacity = 2;

    private PriorityBlockingQueue<Integer> availableSlots;  //for thread-safe

    public void addMember(int clientId) {
        clients.put(clientId, Objects.requireNonNull(availableSlots.poll()));
    }

    public boolean isFull() {
        return clients.size() + 1 == capacity;
    }

    public boolean isEmpty() {
        return hostId == -1;
    }

    public void removeMember(int clientId) {
        availableSlots.add(clients.remove(clientId));
    }

    public boolean hasStarted() {
        return started;
    }

    public void startGame() {
        started = true;
    }

    public void endGame() {
        started = false;
    }

    //是否存在ID为myId的用户？
    public boolean existsMe(Integer myId) {
        for (Map.Entry<Integer, Integer> entry : clients.entrySet()) {
            if (entry.getKey().equals(myId)) {
                return true;
            }
        }
        return hostId == myId;
    }

    public Room(int _hostId) {
        hostId = _hostId;
        clients = new ConcurrentHashMap<>();
        createdTime = new Date();
        availableSlots = new PriorityBlockingQueue<>();
        for (int i = 1; i < capacity; ++i) {
            availableSlots.add(i);
        }
    }

    public int getGameId(int userId) {
        if (userId == hostId) {
            return 0;
        }
        if (!clients.containsKey(userId)) {
            return -1;
        }
        return clients.get(userId);
    }
}
