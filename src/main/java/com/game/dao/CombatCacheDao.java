package com.game.dao;

import com.game.entity.CharacterInfo;
import com.game.entity.PlayerStatus;

import java.util.List;
import java.util.Map;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/25 15:30
 */
public interface CombatCacheDao {
     boolean uploadPlayerInfo(int roomId, int gameId, List<CharacterInfo> infos);

     Map<String, List<CharacterInfo>> getPlayerInfo(int roomId);

     void clearPlayerInfo(int roomId);

     boolean readyToStart(int roomId);
}
