package com.game.daoimpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.game.dao.CombatCacheDao;
import com.game.entity.CharacterInfo;
import com.game.entity.CharacterInfoCollection;
import com.game.utils.logUtils.LogUtil;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/9/2 8:53
 */
@Component("combatCacheDaoJedisImpl")
public class CombatCacheDaoJedisImpl implements CombatCacheDao {
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    String sharedRoomLockKey;

    @Autowired
    Jedis jedis;

    @PostConstruct
    public void initPlayerInfos() {
        if (jedis.exists("playerInfo")) {
            jedis.del("playerInfo");
        }
    }

    @Override
    public boolean uploadPlayerInfo(int roomId, int gameId, List<CharacterInfo> infos) {
        if (roomId < 0 || gameId < 0) {
            return false;
        }
        Map<String, String> entries = jedis.hgetAll("playerInfo");
        if (entries == null) {
            return false;
        }
        CharacterInfoCollection collection =
                JSONObject.parseObject(entries.get(roomId + ""), CharacterInfoCollection.class);
        if (collection == null) {
            collection = new CharacterInfoCollection();
        }
        collection.addCharacter(gameId + "", infos);
        LogUtil.info("collection = " + collection.toString());
        jedis.hset("playerInfo", roomId + "", collection.toString());
        return true;
    }

    @Override
    public Map<String, List<CharacterInfo>> getPlayerInfo(int roomId) {
        if (roomId < 0) return null;
        Map<String, String> entries = jedis.hgetAll("playerInfo");
        if (entries == null) return null;
        CharacterInfoCollection collection = JSONObject.parseObject(entries.get(roomId + ""), CharacterInfoCollection.class);
        return collection.getData();
    }

    @Override
    public void clearPlayerInfo(int roomId) {
        if (jedis.hexists("playerInfo", roomId + "")) {
            jedis.hdel("playerInfo", roomId + "");
        }
    }

    @Override
    public boolean readyToStart(int roomId) {
        Map<String, String> entries = jedis.hgetAll("playerInfo");
        if (entries == null) return false;
        CharacterInfoCollection collection =
                JSONObject.parseObject(entries.get(roomId + ""), CharacterInfoCollection.class);
        return collection.isFull();
    }
}
