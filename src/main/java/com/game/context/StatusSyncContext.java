package com.game.context;

import com.game.annotation.Context;
import com.game.entity.PlayerStatus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Context
public class StatusSyncContext {
    ConcurrentHashMap<Integer, Map<String, List<PlayerStatus>>> roomPlayerStatuses;

    private boolean needSync = false;

    StatusSyncContext() {
        roomPlayerStatuses = new ConcurrentHashMap<>();
    }

    public void syncStatus(int roomId, Map<String, List<PlayerStatus>> status) {
        roomPlayerStatuses.put(roomId, status);
        needSync = true;
    }

    public boolean needSynchronize() {
        return needSync;
    }
}

