package com.game.context;

import com.game.annotation.Context;
import com.game.dao.RoomCacheDao;
import com.game.utils.logUtils.LogUtil;
import com.game.utils.messageUtils.MessageUtil;
import com.game.utils.messageUtils.StompMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/29 18:52
 */
@Context
public class WebsocketContext {
    private final Map<String, Integer> websocketToUser = new HashMap<>();
    private static final long WS_DISCONNECT_THRESH = 5000L;
    //    @Qualifier("roomCacheDaoJedisPoolImpl")
    @Qualifier("roomCacheDaoJedisImpl")
//    @Qualifier("roomCacheDaoImpl")
    @Autowired
    private RoomCacheDao roomCacheDao;

    @Autowired
    private FrameSyncContext frameSyncContext;

    private final ConcurrentHashMap<String, Long> heartBeats = new ConcurrentHashMap<>();

    //绑定websocket和User
    public void bind(String wsSessionId, Integer userId) {
        LogUtil.info("Bind ws " + wsSessionId + " to user " + userId);
        //如果先前有连接，释放相关资源
        if (websocketToUser.containsKey(wsSessionId)) {
            releaseConcernedResources(wsSessionId);
        }
        websocketToUser.put(wsSessionId, userId);
    }

    //解绑
    public void unbind(String wsSessionId) {
        releaseConcernedResources(wsSessionId);
        websocketToUser.remove(wsSessionId);
        heartBeats.remove(wsSessionId);
    }

    //释放相关资源
    public void releaseConcernedResources(String wsSessionId) {
        Integer userId = websocketToUser.get(wsSessionId);
        if (userId != null) {
            roomCacheDao.tryDeleteMyRoom(userId);
//            combatCacheDao.clearPlayerInfo();
        }
    }

    public StompMessage createHeartBeatMsg() {
        return MessageUtil.createStompMessage("heart-beat", System.currentTimeMillis() + "");
    }

    private boolean combatStarted(String wsSessionId) {
        Integer userId = websocketToUser.get(wsSessionId);
        if (userId != null) {
            int roomId = roomCacheDao.inWhichRoom(userId);
            return frameSyncContext.combatStarted(roomId);
        }
        return false;
    }

    public void updateHeartBeat(String wsSessionId, Long timestamp) {
        heartBeats.put(wsSessionId, timestamp);
    }

    @Scheduled(fixedRate = 2000L)
    public void checkAlive() {
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Long> entry : heartBeats.entrySet()) {
            String wsSessionId = entry.getKey();
            Long timestamp = entry.getValue();
            if (combatStarted(wsSessionId)) {
                heartBeats.remove(wsSessionId);
                continue;
            }
            if (now - timestamp >= WS_DISCONNECT_THRESH) {
                unbind(wsSessionId);
                LogUtil.info("ws " + wsSessionId + " has been detected disconnected by heart-beat");
            } else {
                LogUtil.info("ws" + wsSessionId + " is still alive.");
            }
        }
    }
}
