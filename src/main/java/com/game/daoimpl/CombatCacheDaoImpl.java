package com.game.daoimpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.game.dao.CombatCacheDao;
import com.game.entity.CharacterInfo;
import com.game.entity.CharacterInfoCollection;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/25 15:36
 */
@Component("combatCacheDaoImpl")
public class CombatCacheDaoImpl implements CombatCacheDao {
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    String sharedRoomLockKey;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;


    private void removeKeyIfExists(String key) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.delete(key);
        }
    }

    @PostConstruct
    public void initPlayerInfos() {
        removeKeyIfExists("playerInfo");

        Map<String, CharacterInfoCollection> playerInfos = new HashMap<>();
        redisTemplate.boundHashOps("playerInfo").putAll(playerInfos);
    }

    @Override
    public boolean uploadPlayerInfo(int roomId, int gameId, List<CharacterInfo> infos) {
        if (roomId < 0 || gameId < 0) {
            return false;
        }
        Map<Object, Object> entries = redisTemplate.boundHashOps("playerInfo").entries();
        if (entries == null) {
            return false;
        }
        CharacterInfoCollection collection = JSONObject.parseObject(
                JSON.toJSONString(entries.get(roomId + "")), CharacterInfoCollection.class);
        if (collection == null) {
            collection = new CharacterInfoCollection();
        }
        collection.addCharacter(gameId + "", infos);
        redisTemplate.boundHashOps("playerInfo").put(roomId + "", collection);
        return true;
    }

    @Override
    public Map<String, List<CharacterInfo>> getPlayerInfo(int roomId) {
        if (roomId < 0) return null;
        Map<Object, Object> entries = redisTemplate.boundHashOps("playerInfo").entries();
        if (entries == null) return null;
        CharacterInfoCollection collection = JSONObject.parseObject(
                JSON.toJSONString(entries.get(roomId + "")), CharacterInfoCollection.class);
        return collection.getData();
    }

    @Override
    public void clearPlayerInfo(int roomId) {
        redisTemplate.boundHashOps("playerInfo").delete(roomId + "");
    }

    @Override
    public boolean readyToStart(int roomId) {
        Map<Object, Object> entries = redisTemplate.boundHashOps("playerInfo").entries();
        if (entries == null) return false;
        CharacterInfoCollection collection = JSONObject.parseObject(
                JSON.toJSONString(entries.get(roomId + "")), CharacterInfoCollection.class);
        return collection.isFull();
    }
}
