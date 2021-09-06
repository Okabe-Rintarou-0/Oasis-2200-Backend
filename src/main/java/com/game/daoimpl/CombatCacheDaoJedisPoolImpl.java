package com.game.daoimpl;

import com.alibaba.fastjson.JSONObject;
import com.game.dao.CombatCacheDao;
import com.game.entity.CharacterInfo;
import com.game.entity.CharacterInfoCollection;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/9/2 10:27
 */
@Component("combatCacheDaoJedisPoolImpl")
public class CombatCacheDaoJedisPoolImpl implements CombatCacheDao {
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    String sharedRoomLockKey;

    @Autowired
    JedisPool jedisPool;

    @PostConstruct
    public void initPlayerInfos() {
        Jedis jedis = jedisPool.getResource();
        if (jedis.exists("playerInfo")) {
            jedis.del("playerInfo");
        }
        jedis.close();
    }

    @Override
    public boolean uploadPlayerInfo(int roomId, int gameId, List<CharacterInfo> infos) {
        if (roomId < 0 || gameId < 0) {
            return false;
        }
        Jedis jedis = jedisPool.getResource();
        Map<String, String> entries = jedis.hgetAll("playerInfo");
        if (entries != null) {
            CharacterInfoCollection collection =
                    JSONObject.parseObject(entries.get(roomId + ""), CharacterInfoCollection.class);
            if (collection == null) {
                collection = new CharacterInfoCollection();
            }
            collection.addCharacter(gameId + "", infos);
            jedis.hset("playerInfo", roomId + "", collection.toString());
            jedis.close();
            return true;
        }
        return false;
    }

    @Override
    public Map<String, List<CharacterInfo>> getPlayerInfo(int roomId) {
        if (roomId < 0) return null;
        Jedis jedis = jedisPool.getResource();
        Map<String, String> entries = jedis.hgetAll("playerInfo");
        jedis.close();
        if (entries == null) return null;
        CharacterInfoCollection collection = JSONObject.parseObject(entries.get(roomId + ""), CharacterInfoCollection.class);
        return collection.getData();
    }

    @Override
    public void clearPlayerInfo(int roomId) {
        Jedis jedis = jedisPool.getResource();
        if (jedis.hexists("playerInfo", roomId + "")) {
            jedis.hdel("playerInfo", roomId + "");
        }
        jedis.close();
    }

    @Override
    public boolean readyToStart(int roomId) {
        Jedis jedis = jedisPool.getResource();
        Map<String, String> entries = jedis.hgetAll("playerInfo");
        jedis.close();
        if (entries == null) return false;
        CharacterInfoCollection collection =
                JSONObject.parseObject(entries.get(roomId + ""), CharacterInfoCollection.class);
        return collection.isFull();
    }
}
