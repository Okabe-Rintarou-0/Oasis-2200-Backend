package com.game.service;

import com.game.entity.Frame;
import com.game.entity.PlayerStatus;

public interface FrameSyncService {
    boolean startSync(Integer roomId);

    boolean stopSync(Integer roomId);

    void addFrame(Integer roomId, Integer gameId, Frame frame);
}
