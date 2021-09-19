package com.game.serviceimpl;

import com.game.constants.NetworkConstants;
import com.game.context.FrameSyncContext;
import com.game.context.RoomContext;
import com.game.dao.RoomCacheDao;
import com.game.entity.Frame;
import com.game.service.FrameSyncService;
import com.game.utils.logUtils.LogUtil;
import com.game.utils.messageUtils.MessageUtil;
import com.game.utils.messageUtils.StompMessage;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class FrameSyncServiceImpl implements FrameSyncService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    RoomContext roomContext;

    //    @Qualifier("roomCacheDaoJedisPoolImpl")
    @Qualifier("roomCacheDaoJedisImpl")
//    @Qualifier("roomCacheDaoImpl")
    @Autowired
    RoomCacheDao roomCacheDao;

    @Value("${cluster.strategy}")
    String strategy;

    @Autowired
    private FrameSyncContext frameSyncContext;

    @Override
    public boolean startSync(Integer roomId) {  //房间开始同步
        Runnable frameSyncTask = () -> {
            frameSyncContext.toNextFrame(roomId);
            String dest = String.format(NetworkConstants.TOPIC_ROOM_FORMAT, roomId.toString()); //"/topics/rooms/%s"
            int disconnectedUser = frameSyncContext.checkDisconnection(roomId);
            //no one has disconnected
            if (disconnectedUser < 0) {
                StompMessage stompMessage = MessageUtil.createStompMessage("frame", "frame", frameSyncContext.getFrameDataOfThisFrame(roomId));
                LogUtil.info("Send frame to room " + roomId + " with msg " + stompMessage.data.toString());
                simpMessagingTemplate.convertAndSend(dest, stompMessage);
                frameSyncContext.clearFrame(roomId);
            } else { //someone has disconnected
                LogUtil.print(String.format("user %d has disconnected!%n", disconnectedUser));
                if (strategy.equals("shared")) {
                    roomCacheDao.removeRoom(roomId);
                } else {
                    roomContext.forceDeleteRoom(roomId);
                }
                frameSyncContext.removeFrameSyncScheduler(roomId);
                frameSyncContext.removePingPong(roomId);
                JSONObject data = new JSONObject();
                int winner = disconnectedUser == 0 ? 1 : 0;
                data.put("winner", winner); //附带获胜者信息
                StompMessage stompMessage = MessageUtil.createStompMessage("room", "end", data);
                simpMessagingTemplate.convertAndSend(dest, stompMessage);
            }
        };
        //开启一个interval毫秒为周期的定时任务
        return frameSyncContext.addFrameSyncScheduler(roomId, frameSyncTask);
    }

    @Override
    public boolean stopSync(Integer roomId) {
        return frameSyncContext.removeFrameSyncScheduler(roomId);
    }

    @Override
    public void addFrame(Integer roomId, Integer gameId, Frame frame) {
        if (roomId == null || roomId < 0 || gameId == null || gameId < 0) {
            return;
        }
//        LogUtil.print("Receive Frame: " + JSON.toJSONString(frame));
        frameSyncContext.addFrame(roomId, gameId, frame);
    }
}
