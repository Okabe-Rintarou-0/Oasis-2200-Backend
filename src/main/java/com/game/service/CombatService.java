package com.game.service;

import com.game.entity.CharacterInfo;

import java.util.List;

public interface CombatService {
    boolean acceptCombat(int myId);

    boolean denyCombat(int myId);

    void endCombat(int myId, int winner);

    boolean uploadPlayerInfo(int myId, List<CharacterInfo> info);
}
