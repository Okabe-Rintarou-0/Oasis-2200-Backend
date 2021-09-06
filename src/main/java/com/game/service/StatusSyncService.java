package com.game.service;

import com.game.entity.PlayerStatus;

import java.util.List;
import java.util.Map;

public interface StatusSyncService {
    void statusSync(Integer roomId, Map<String, List<PlayerStatus>> status);
}
