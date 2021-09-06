package com.game.service;

import com.game.entity.CharacterInfo;

import java.util.List;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/25 14:51
 */
public interface CombatCacheService {
    boolean acceptCombat(int myId);

    boolean denyCombat(int myId);

    void endCombat(int myId, int winner);

    boolean uploadPlayerInfo(int myId, List<CharacterInfo> info);
}
