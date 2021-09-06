package com.game.serviceimpl;

import com.game.annotation.ClusterSafe;
import com.game.constants.NetworkConstants;
import com.game.dao.RoomCacheDao;
import com.game.dto.RoomDto;
import com.game.dto.RoomFeatureDto;
import com.game.service.RoomCacheService;
import com.game.utils.logUtils.LogUtil;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import com.game.utils.redisUtils.RedisMessageManager;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/24 9:26
 */
@Service
public class RoomCacheServiceImpl implements RoomCacheService {
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisMessageManager redisMessageManager;

    @Autowired
    private String sharedRoomLockKey;

//    @Qualifier("roomCacheDaoJedisPoolImpl")
    @Qualifier("roomCacheDaoJedisImpl")
//    @Qualifier("roomCacheDaoImpl")
    @Autowired
    RoomCacheDao roomCacheDao;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Value("${room.maxCount}")
    Integer maxCount;

    @Value("${room.sectionCount}")
    int sectionCount;

    @Value("${room.sectionSize}")
    int sectionSize;

    @Value("${room.sectionKeySuffix}")
    String sectionKeySuffix;

    @Autowired
    String intraNetIp;

    @Override
    public Map<String, Map<String, RoomFeatureDto>> getRoomFeatures() {
        Map<String, Map<String, RoomFeatureDto>> roomFeatures = new HashMap<>();
        RLock fairLock = redissonClient.getFairLock(sharedRoomLockKey);
        try {
            boolean res = fairLock.tryLock(30, 5, TimeUnit.SECONDS);
            if (res) {
                for (int i = 0; i < sectionCount; ++i) {
                    String sectionKey = sectionKeySuffix + i;
                    roomFeatures.put(sectionKey, roomCacheDao.getRoomFeatures(i));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (fairLock.isHeldByCurrentThread())
                fairLock.unlock();
        }
        return roomFeatures;
    }

    @Override
    public Integer getLastRoomIndex() {
        return roomCacheDao.getLastRoomIndex();
    }

    @Override
    @ClusterSafe
    public Map<String, Integer> getUserStates() {
        RLock fairLock = redissonClient.getFairLock(sharedRoomLockKey);
        try {
            boolean res = fairLock.tryLock(30, 5, TimeUnit.SECONDS);
            if (res) {
                return roomCacheDao.getUserStates();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (fairLock.isHeldByCurrentThread())
                fairLock.unlock();
        }
        return null;
    }

    @Override
    @ClusterSafe
    public Message createRoom(int hostId) {
        if (hostId < 0) {
            return MessageUtil.createMessage(MessageUtil.STAT_INVALID, "创建房间失败！");
        }
        RLock fairLock = redissonClient.getFairLock(sharedRoomLockKey);
        try {
            boolean res = fairLock.tryLock(30, 5, TimeUnit.SECONDS);
            if (res) {
                if (roomCacheDao.inWhichRoom(hostId) >= 0) //如果host已经在某个房间里了
                    return MessageUtil.createMessage(MessageUtil.STAT_INVALID, "创建房间失败！");
                //look for available Room.
                int roomId = tryGetAvailableRoomId();
                if (roomId >= 0) {
                    RoomFeatureDto roomFeatureDto = new RoomFeatureDto(hostId);
                    roomCacheDao.addRoom(hostId, roomId, roomFeatureDto);
                    return MessageUtil.createMessage(MessageUtil.STAT_OK, "创建房间成功",
                            net.sf.json.JSONObject.fromObject(roomCacheDao.getRoomInfo(roomId, roomFeatureDto)));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (fairLock.isHeldByCurrentThread())
                fairLock.unlock();
        }
        LogUtil.info("Shared room is full now");
        return MessageUtil.createMessage(MessageUtil.STAT_INVALID, "创建房间失败！");
    }

    @Override
    @ClusterSafe
    public RoomDto joinRoom(int clientId) {
        if (clientId < 0) {
            return null;
        }
        int roomId = -1;
        RoomDto roomDto = null;
        RLock fairLock = redissonClient.getFairLock(sharedRoomLockKey);
        try {
            boolean res = fairLock.tryLock(30, 5, TimeUnit.SECONDS);
            if (res) {
                //如果client已经在其他房间里面了
                if (roomCacheDao.inWhichRoom(clientId) >= 0) {
                    return null;
                }
                roomId = tryGetJoinableRoomId();
                if (roomId < 0) return null; //没房间的话就无法加入；
                roomDto = roomCacheDao.addRoomMember(roomId, clientId);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (fairLock.isHeldByCurrentThread())
                fairLock.unlock();
        }
        if (roomDto != null) {
            String dest = String.format(NetworkConstants.TOPIC_ROOM_FORMAT, roomId + "");
            simpMessagingTemplate.convertAndSend(dest, MessageUtil.createStompMessage("room", "join"));
            redisMessageManager.sendObject("room", MessageUtil.createRedisMessage(intraNetIp, roomId + ""));
            LogUtil.info("user " + clientId + " join room " + roomId + " succeed");
        }
        return roomDto;
    }

//    @Override
//    @ClusterSafe
//    public RoomDto getRoom(int clientId) {
//        RLock fairLock = redissonClient.getFairLock(sharedRoomLockKey);
//        RoomDto roomDto = null;
//        try {
//            boolean res = fairLock.tryLock(30, 5, TimeUnit.SECONDS);
//            if (res) {
//                int roomId = roomCacheDao.inWhichRoom(clientId);
//                roomDto = roomCacheDao.getRoomInfo(roomId, roomCacheDao.getRoomFeature(roomId));
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            if (fairLock.isHeldByCurrentThread())
//                fairLock.unlock();
//        }
//        return roomDto;
//    }

    @Override
    @ClusterSafe
    public void removeRoom(int roomId) {
        if (roomId < 0) {
            return;
        }
        RLock fairLock = redissonClient.getFairLock(sharedRoomLockKey);
        try {
            boolean res = fairLock.tryLock(30, 5, TimeUnit.SECONDS);
            if (res) {
                roomCacheDao.removeRoom(roomId);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (fairLock.isHeldByCurrentThread())
                fairLock.unlock();
        }
    }

    @Override
    public void clearRoomContext() {
        RLock fairLock = redissonClient.getFairLock(sharedRoomLockKey);
        try {
            boolean res = fairLock.tryLock(30, 5, TimeUnit.SECONDS);
            if (res) {
                roomCacheDao.clearContext();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (fairLock.isHeldByCurrentThread())
                fairLock.unlock();
        }
    }

    //获取可用的房间号用于加入。
    @Override
    public int tryGetJoinableRoomId() {
        return roomCacheDao.getFirstCreatedRoomId();
    }

    //获取可用的房间号用于创建。
    @Override
    public int tryGetAvailableRoomId() {
        int deletedRoomId = roomCacheDao.getFirstDeletedRoomId();
        System.out.println("del = " + deletedRoomId);
        if (deletedRoomId >= 0) return deletedRoomId;

        int lastRoomIndex = getLastRoomIndex();
        int roomId = lastRoomIndex;
        if (lastRoomIndex < 0) lastRoomIndex = 0;
        do {
            roomId = (roomId + 1) % maxCount;
            if (!roomCacheDao.existsRoom(roomId))
                return roomId;
        } while (roomId != lastRoomIndex);
        return -1;
    }
}
