package com.game.daoimpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.game.annotation.ClusterSafe;
import com.game.dao.RoomCacheDao;
import com.game.dto.RoomDto;
import com.game.dto.RoomFeatureDto;
import com.game.repository.UserRepository;
import com.game.utils.jwtUtils.JwtUtil;
import com.game.utils.logUtils.LogUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/9/2 10:08
 */
@Component("roomCacheDaoJedisPoolImpl")
public class RoomCacheDaoJedisPoolImpl implements RoomCacheDao {
    @Autowired
    private JedisPool jedisPool;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    UserRepository userRepository;

    @Autowired
    String sharedRoomLockKey;

    @Value("${room.maxCount}")
    int maxCount;

    @Value("${room.sectionCount}")
    int sectionCount;

    @Value("${room.sectionSize}")
    int sectionSize;

    @Value("${room.sectionKeySuffix}")
    String sectionKeySuffix;

    private void removeKeyIfExists(Jedis jedis, String key) {
        if (jedis != null && jedis.isConnected()) {
            if (jedis.exists(key)) {
                jedis.del(key);
            }
        }
    }

    @Override
    public Map<String, RoomFeatureDto> getRoomFeatures(int sectionId) {
        Map<String, RoomFeatureDto> roomFeatures = new HashMap<>();
        String sectionKey = sectionKeySuffix + sectionId;
        Jedis jedis = jedisPool.getResource();
        Map<String, String> entries = jedis.hgetAll(sectionKey);
        if (entries != null) {
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                roomFeatures.put(entry.getKey(),
                        JSONObject.parseObject(entry.getValue(), RoomFeatureDto.class));
            }
        }
        jedis.close();
        return roomFeatures;
    }

    @Override
    public RoomDto getRoomInfo(int roomId, RoomFeatureDto roomFeatureDto) {
        String hostName = userRepository.findOne(roomFeatureDto.getHostId()).getNickname();
        Set<String> clientNames = new HashSet<>();
        for (Integer clientId : roomFeatureDto.getClientsId()) {
            String thisClientName = userRepository.findOne(clientId).getNickname();
            clientNames.add(thisClientName);
        }
        int myId = JwtUtil.getUserId();
        int gid = roomFeatureDto.getHostId() == myId ? 0 : 1;
        return new RoomDto(roomId, gid, hostName, clientNames);
    }

    @Override
    public RoomFeatureDto getRoomFeature(int roomId) {
        if (roomId < 0) {
            return null;
        }
        String sectionKey = sectionKeySuffix + (roomId / sectionSize);
        Jedis jedis = jedisPool.getResource();
        String roomFeatureStr = jedis.hget(sectionKey, roomId + "");
        System.out.println("str: " + roomFeatureStr);
        jedis.close();
        return roomFeatureStr == null ?
                null
                : JSON.parseObject(roomFeatureStr, RoomFeatureDto.class);
    }

    @ClusterSafe
    @Override
    public int getLastRoomIndex() {
        Integer roomIdx = null;
        RLock fairLock = redissonClient.getFairLock(sharedRoomLockKey);
        try {
            boolean res = fairLock.tryLock(30, 5, TimeUnit.SECONDS);
            if (res) {
                Jedis jedis = jedisPool.getResource();
                roomIdx = Integer.parseInt(jedis.get("roomIdx"));
                jedis.close();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (fairLock.isHeldByCurrentThread())
                fairLock.unlock();
        }
        return roomIdx == null ? -1 : roomIdx;
    }

    @Override
    public Map<String, Integer> getUserStates() {
        Map<String, Integer> userStates = new HashMap<>();
        Jedis jedis = jedisPool.getResource();
        Map<String, String> entries = jedis.hgetAll("users");
        if (entries != null) {
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                userStates.put(entry.getKey(), Integer.parseInt(entry.getValue()));
            }
        }
        jedis.close();
        return userStates;
    }

    @Override
    public int inWhichRoom(int myId) {
        Jedis jedis = jedisPool.getResource();
        String obj = jedis.hget("users", myId + "");
        jedis.close();
        return obj == null ? -1 : Integer.parseInt(obj);
    }

    @Override
    public void addRoom(int hostId, int roomId, RoomFeatureDto roomFeature) {
        String sectionKey = sectionKeySuffix + (roomId / sectionSize);
        Jedis jedis = jedisPool.getResource();
        jedis.hset(sectionKey, roomId + "", roomFeature.toString());
        jedis.hset("users", hostId + "", roomId + "");
        jedis.set("roomIdx", roomId + "");
        jedis.rpush("created", roomId + "");
        jedis.close();
        System.out.println("addRoom hostId: " + hostId + " roomId: " + roomId);
    }

    @Override
    public RoomDto addRoomMember(int roomId, int clientId) {
        RoomFeatureDto roomFeature = getRoomFeature(roomId);//如果房间可以加入
        System.out.println("roomId: " + roomId + " client: " + clientId + " " + roomFeature.toString());
        roomFeature.getClientsId().add(clientId);
        String sectionKey = sectionKeySuffix + (roomId / sectionSize);
        Jedis jedis = jedisPool.getResource();
        jedis.hset(sectionKey, roomId + "", roomFeature.toString());
        jedis.hset("users", clientId + "", roomId + "");
        jedis.close();
        return getRoomInfo(roomId, roomFeature);
    }

    @Override
    public void removeRoom(int roomId) {
        RoomFeatureDto roomFeature = getRoomFeature(roomId);
        if (roomFeature != null) {
            String sectionKey = sectionKeySuffix + (roomId / sectionSize);
            Jedis jedis = jedisPool.getResource();
            jedis.hdel(sectionKey, roomId + "");
            jedis.hset("users", roomFeature.getHostId() + "", "-1");
            for (Integer clientId : roomFeature.getClientsId()) {
                jedis.hset("users", clientId + "", "-1");
            }
            jedis.rpush("deleted", roomId + "");
            jedis.close();
            LogUtil.info("Successfully remove room: " + roomId);
        }
    }

    @PostConstruct
    @Override
    public void clearContext() {
        Jedis jedis = jedisPool.getResource();
        removeKeyIfExists(jedis, "users");
        removeKeyIfExists(jedis, "roomIdx");
        removeKeyIfExists(jedis, "created");
        removeKeyIfExists(jedis, "deleted");

        for (int i = 0; i < sectionCount; ++i) {
            String sectionKey = sectionKeySuffix + i;
            removeKeyIfExists(jedis, sectionKey);
        }
        jedis.set("roomIdx", "-1");
        jedis.close();

        System.out.println("初始化完成");
    }

    @Override
    public boolean existsRoom(int roomId) {
        String sectionKey = sectionKeySuffix + (roomId / sectionSize);
        Jedis jedis = jedisPool.getResource();
        boolean exist = jedis.hexists(sectionKey, roomId + "");
        jedis.close();
        return exist;
    }

    @Override
    @ClusterSafe
    public void tryDeleteMyRoom(int myId) {
        if (myId < 0) {
            return;
        }
        RLock fairLock = redissonClient.getFairLock(sharedRoomLockKey);
        try {
            //第一个参数是timeout不多做解释
            //第二个参数是leaseTime，超过这个时间，无论拿锁的线程是否做完业务都会放锁。
            boolean res = fairLock.tryLock(30, 5, TimeUnit.SECONDS);
            if (res) {
                int roomId = inWhichRoom(myId);
                RoomFeatureDto roomFeature = getRoomFeature(roomId);
                if (roomFeature != null && roomFeature.getHostId() == myId) {
                    removeRoom(roomId);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (fairLock.isHeldByCurrentThread())
                fairLock.unlock();
        }
    }

    @Override
    public boolean canAcceptOrDeny(int myId, int roomId) {
        RoomFeatureDto roomFeature = getRoomFeature(roomId);
        //如果我是房主才能够接受或删除对战。
        return roomFeature != null && roomFeature.getHostId() == myId && roomFeature.isFull();
    }

    @Override
    public int getFirstDeletedRoomId() {
        Jedis jedis = jedisPool.getResource();
        String roomIdStr = jedis.rpop("deleted");
        jedis.close();
        return roomIdStr == null ? -1 : Integer.parseInt(roomIdStr);
    }

    @Override
    public void addDeletedRoomId(int roomId) {
        if (roomId >= 0) {
            Jedis jedis = jedisPool.getResource();
            jedis.rpush("deleted", roomId + "");
            jedis.close();
        }
    }
}
