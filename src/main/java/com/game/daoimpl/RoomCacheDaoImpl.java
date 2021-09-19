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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

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
 * @Description: 标注了ClusterSafe的可以不上锁直接用
 * @date 2021/8/25 15:00
 */
@Component("roomCacheDaoImpl")
public class RoomCacheDaoImpl implements RoomCacheDao {
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

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    private void removeKeyIfExists(String key) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.delete(key);
        }
    }

    //由roomId和roomFeatureDto转换成RoomDto
    //这其实一开始写这个函数是为了适配原先的接口，这导致这个函数有点奇怪
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

    //根据房间号获取房间信息
    @Override
    public RoomFeatureDto getRoomFeature(int roomId) {
        if (roomId < 0) {
            return null;
        }
        String sectionKey = sectionKeySuffix + (roomId / sectionSize);
        Object obj = redisTemplate.boundHashOps(sectionKey).get(roomId + "");
        return obj == null ?
                null
                : JSON.parseObject(JSON.toJSONString(obj), RoomFeatureDto.class);
    }

    //根据区块号获取房间信息
    @Override
    public Map<String, RoomFeatureDto> getRoomFeatures(int sectionId) {

        Map<String, RoomFeatureDto> roomFeatures = new HashMap<>();
        String sectionKey = sectionKeySuffix + sectionId;
        Map<Object, Object> entries = redisTemplate.boundHashOps(sectionKey).entries();
        if (entries != null) {
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                roomFeatures.put(entry.getKey().toString(),
                        JSONObject.parseObject(JSON.toJSONString(entry.getValue()), RoomFeatureDto.class));
            }
        }
        return roomFeatures;
    }

    //获取所有用户状态（即< 用户Id, 用户所在房间号 >）
    @Override
    public Map<String, Integer> getUserStates() {
        Map<String, Integer> userStates = new HashMap<>();
        Map<Object, Object> entries = redisTemplate.boundHashOps("users").entries();
        if (entries != null) {
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                userStates.put(entry.getKey().toString(), (Integer) entry.getValue());
            }
        }
        return userStates;
    }

    //“我”在哪间房间？
    @Override
    public int inWhichRoom(int myId) {
        Object obj = redisTemplate.boundHashOps("users").get(myId + "");
        return obj == null ? -1 : (Integer) obj;
    }

    //获取上一个新建的房间的编号
    @Override
    @ClusterSafe
    public int getLastRoomIndex() {
        Integer roomIdx = null;
        RLock fairLock = redissonClient.getFairLock(sharedRoomLockKey);
        try {
            boolean res = fairLock.tryLock(30, 5, TimeUnit.SECONDS);
            if (res) {
                roomIdx = (Integer) redisTemplate.boundValueOps("roomIdx").get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (fairLock.isHeldByCurrentThread())
                fairLock.unlock();
        }
        return roomIdx == null ? -1 : roomIdx;
    }

    //添加新房间
    @Override
    public void addRoom(int hostId, int roomId, RoomFeatureDto roomFeature) {
        String sectionKey = sectionKeySuffix + (roomId / sectionSize);
        redisTemplate.boundHashOps(sectionKey).put(roomId + "", roomFeature);
        redisTemplate.boundHashOps("users").put(hostId + "", roomId);
        redisTemplate.boundValueOps("roomIdx").set(roomId);
        System.out.println("addRoom hostId: " + hostId + " roomId: " + roomId);
    }

    //优化了一下数据结构，被删除的房间(创建之后断开连接、战斗结束、拒绝匹配都会造成房间的删除)
    //被删除的房间号会进入这个删除队列当中，以便下一个创房者使用
    @Override
    public int getFirstDeletedRoomId() {
        Long size = redisTemplate.boundListOps("deleted").size();
        if (size != null && size > 0) {
            Object obj = redisTemplate.boundListOps("deleted").leftPop();
            if (obj != null)
                return (int) obj;
        }
        return -1;
    }

    @Override
    public void addDeletedRoomId(int roomId) {
        if (roomId >= 0) {
            redisTemplate.boundListOps("deleted").rightPush(roomId);
        }
    }

    @Override
    public RoomDto addRoomMember(int roomId, int clientId) {
        RoomFeatureDto roomFeature = getRoomFeature(roomId);//如果房间可以加入
        System.out.println("roomId: " + roomId + " client: " + clientId + " " + roomFeature.toString());
        roomFeature.getClientsId().add(clientId);
        String sectionKey = sectionKeySuffix + (roomId / sectionSize);
        redisTemplate.boundHashOps(sectionKey).put(roomId + "", roomFeature);
        redisTemplate.boundHashOps("users").put(clientId + "", roomId);

        return getRoomInfo(roomId, roomFeature);
    }

    @Override
    public void removeRoom(int roomId) {
        RoomFeatureDto roomFeature = getRoomFeature(roomId);
        if (roomFeature != null) {
            String sectionKey = sectionKeySuffix + (roomId / sectionSize);
            redisTemplate.boundHashOps(sectionKey).delete(roomId + "");
            redisTemplate.boundHashOps("users").put(roomFeature.getHostId() + "", -1);
            for (Integer clientId : roomFeature.getClientsId()) {
                redisTemplate.boundHashOps("users").put(clientId + "", -1);
            }
            redisTemplate.boundListOps("deleted").rightPush(roomId);
            LogUtil.info("Successfully remove room: " + roomId);
        }
    }

    //@PostConstruct 代表在该类构建完毕之后会执行下面的函数，会做一次初始化工作。
    @PostConstruct
    @Override
    public void clearContext() {
        removeKeyIfExists("users");
        removeKeyIfExists("roomIdx");
        removeKeyIfExists("deleted");

        HashMap<String, Object> roomFeatures = new HashMap<>();
        for (int i = 0; i < sectionCount; ++i) {
            String sectionKey = sectionKeySuffix + i;
            removeKeyIfExists(sectionKey);
            redisTemplate.boundHashOps(sectionKey).putAll(roomFeatures);
        }

        HashMap<String, Integer> userStates = new HashMap<>();
        redisTemplate.boundHashOps("users").putAll(userStates);
        redisTemplate.opsForValue().set("roomIdx", -1);

        System.out.println("初始化完成");
    }

    @Override
    public boolean existsRoom(int roomId) {
        String sectionKey = sectionKeySuffix + (roomId / sectionSize);
        return Boolean.TRUE.equals(redisTemplate.boundHashOps(sectionKey).hasKey(roomId + ""));
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
}
