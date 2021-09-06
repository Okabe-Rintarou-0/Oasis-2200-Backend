package com.game.context;

import com.game.annotation.Context;
import com.game.dao.RoomCacheDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;

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

//    @Qualifier("roomCacheDaoJedisPoolImpl")
    @Qualifier("roomCacheDaoJedisImpl")
//    @Qualifier("roomCacheDaoImpl")
    @Autowired
    RoomCacheDao roomCacheDao;

    //绑定websocket和User
    public void bind(String wsSessionId, Integer userId) {
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
    }

    //释放相关资源
    public void releaseConcernedResources(String wsSessionId) {
        Integer userId = websocketToUser.get(wsSessionId);
        if (userId != null) {
            roomCacheDao.tryDeleteMyRoom(userId);
        }
    }
}
