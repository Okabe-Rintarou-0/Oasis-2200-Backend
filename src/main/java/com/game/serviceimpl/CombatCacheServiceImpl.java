package com.game.serviceimpl;

import com.game.annotation.ClusterSafe;
import com.game.constants.NetworkConstants;
import com.game.dao.CombatCacheDao;
import com.game.dao.RoomCacheDao;
import com.game.dto.RoomFeatureDto;
import com.game.entity.CharacterInfo;
import com.game.service.CombatCacheService;
import com.game.service.FrameSyncService;
import com.game.utils.jwtUtils.JwtUtil;
import com.game.utils.logUtils.LogUtil;
import com.game.utils.messageUtils.MessageUtil;
import com.game.utils.messageUtils.StompMessage;
import net.sf.json.JSONObject;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/25 14:52
 */
@Service
public class CombatCacheServiceImpl implements CombatCacheService {
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    String sharedRoomLockKey;

    @Qualifier("combatCacheDaoJedisImpl")
    @Autowired
    CombatCacheDao combatCacheDao;

    //    @Qualifier("roomCacheDaoJedisPoolImpl")
    @Qualifier("roomCacheDaoJedisImpl")
//    @Qualifier("roomCacheDaoImpl")
    @Autowired
    RoomCacheDao roomCacheDao;

    @Autowired
    FrameSyncService frameSyncService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Value("${room.maxCount}")
    int maxCount;

    @Override
    @ClusterSafe
    public boolean acceptCombat(int myId) {
        if (myId < 0) {
            return false;
        }
        int roomId = -1;
        boolean acceptSucceed = false;
        RLock fairLock = redissonClient.getFairLock(sharedRoomLockKey);
        try {
            //第一个参数是timeout不多做解释
            //第二个参数是leaseTime，超过这个时间，无论拿锁的线程是否做完业务都会放锁。
            boolean res = fairLock.tryLock(30, 5, TimeUnit.SECONDS);
            if (res) {
                roomId = roomCacheDao.inWhichRoom(myId);
                acceptSucceed = roomCacheDao.canAcceptOrDeny(myId, roomId);
                LogUtil.info("successfully accept");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (fairLock.isHeldByCurrentThread())
                fairLock.unlock();
        }
        if (acceptSucceed) {
            String dest = String.format(NetworkConstants.TOPIC_ROOM_FORMAT, roomId + "");
            StompMessage stompMessage = MessageUtil.createStompMessage("room", "accept");
            simpMessagingTemplate.convertAndSend(dest, stompMessage); //广播以开始游戏.
        }
        return acceptSucceed;
    }

    @Override
    @ClusterSafe
    public boolean denyCombat(int myId) {
        if (myId < 0) {
            return false;
        }
        boolean denySucceed = false;
        int roomId = -1;
        RLock fairLock = redissonClient.getFairLock(sharedRoomLockKey);
        try {
            //第一个参数是timeout不多做解释
            //第二个参数是leaseTime，超过这个时间，无论拿锁的线程是否做完业务都会放锁。
            boolean res = fairLock.tryLock(30, 5, TimeUnit.SECONDS);
            if (res) {
                roomId = roomCacheDao.inWhichRoom(myId);
                denySucceed = roomCacheDao.canAcceptOrDeny(myId, roomId);
                LogUtil.info("successfully deny");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (fairLock.isHeldByCurrentThread())
                fairLock.unlock();
        }
        if (denySucceed) {
            String dest = String.format(NetworkConstants.TOPIC_ROOM_FORMAT, roomId + "");
            //解散房间用户
            StompMessage stompMessage = MessageUtil.createStompMessage("room", "dismiss");
            simpMessagingTemplate.convertAndSend(dest, stompMessage); //广播以取消游戏.
        }
        return denySucceed;
    }

    @Override
    @ClusterSafe
    //结束战斗
    public void endCombat(int myId, int winner) {
        int roomId = -1;
        boolean succeed = false;
        RLock fairLock = redissonClient.getFairLock(sharedRoomLockKey);
        try {
            //第一个参数是timeout不多做解释
            //第二个参数是leaseTime，超过这个时间，无论拿锁的线程是否做完业务都会放锁。
            boolean res = fairLock.tryLock(30, 5, TimeUnit.SECONDS);
            if (res) {
                roomId = roomCacheDao.inWhichRoom(myId);
                if (myId >= 0 && winner >= 0 && roomId >= 0) { //如果相关信息合法
                    succeed = true;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (fairLock.isHeldByCurrentThread())
                fairLock.unlock();
        }
        if (succeed) {
            roomCacheDao.removeRoom(roomId);
            String dest = String.format(NetworkConstants.TOPIC_ROOM_FORMAT, roomId + "");
            JSONObject data = new JSONObject();
            data.put("winner", winner); //附带获胜者信息
            frameSyncService.stopSync(roomId); //停止帧同步
            StompMessage stompMessage = MessageUtil.createStompMessage("room", "end", data);
            simpMessagingTemplate.convertAndSend(dest, stompMessage);
        }
    }

    @Override
    @ClusterSafe
    public boolean uploadPlayerInfo(int myId, List<CharacterInfo> info) {
        boolean readyToStart = false;
        boolean uploadSucceed = false;
        int roomId = -1;
        RLock fairLock = redissonClient.getFairLock(sharedRoomLockKey);
        try {
            //第一个参数是timeout不多做解释
            //第二个参数是leaseTime，超过这个时间，无论拿锁的线程是否做完业务都会放锁。
            boolean res = fairLock.tryLock(30, 5, TimeUnit.SECONDS);
            if (res) {
                roomId = roomCacheDao.inWhichRoom(myId);
                RoomFeatureDto roomFeature = roomCacheDao.getRoomFeature(roomId);
                if (roomFeature != null) {
                    int gameId = myId == roomFeature.getHostId() ? 0 : 1;
                    LogUtil.print(String.format("receive %s from %d", info, myId));
                    if (combatCacheDao.uploadPlayerInfo(roomId, gameId, info)) {
                        readyToStart = combatCacheDao.readyToStart(roomId);
                        uploadSucceed = true;
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (fairLock.isHeldByCurrentThread())
                fairLock.unlock();
        }
        if (readyToStart) {
            String dest = String.format(NetworkConstants.TOPIC_ROOM_FORMAT, roomId + "");
            StompMessage stompMessage = MessageUtil.createStompMessage("room", "start",
                    JSONObject.fromObject(combatCacheDao.getPlayerInfo(roomId)));
            simpMessagingTemplate.convertAndSend(dest, stompMessage);
            combatCacheDao.clearPlayerInfo(roomId); //删除
        }
        return uploadSucceed;
    }
}
