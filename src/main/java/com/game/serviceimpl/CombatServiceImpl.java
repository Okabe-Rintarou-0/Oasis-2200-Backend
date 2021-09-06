package com.game.serviceimpl;

import com.game.constants.NetworkConstants;
import com.game.context.RoomContext;
import com.game.entity.CharacterInfo;
import com.game.service.CombatService;
import com.game.utils.jwtUtils.JwtUtil;
import com.game.utils.logUtils.LogUtil;
import com.game.utils.messageUtils.MessageUtil;
import com.game.utils.messageUtils.StompMessage;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CombatServiceImpl implements CombatService {

    @Autowired
    RoomContext roomContext;

    @Autowired
    FrameSyncServiceImpl frameSyncService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public boolean acceptCombat(int myId) {
        int roomId = roomContext.knowUserInWhichRoom(myId);
        //如果我是房主才能够接受对战。
        if (roomContext.existsRoom(roomId) && roomContext.getRoomInfo(roomId).getHostId() == myId) {
            String dest = String.format(NetworkConstants.TOPIC_ROOM_FORMAT, roomId + "");
            StompMessage stompMessage = MessageUtil.createStompMessage("room", "accept");
            simpMessagingTemplate.convertAndSend(dest, stompMessage); //广播以开始游戏.
            return true;
        }
        return false;
    }

    @Override
    public boolean denyCombat(int myId) {
        int roomId = roomContext.knowUserInWhichRoom(myId);
        //如果我是房主才能够拒绝对战。
        if (roomContext.existsRoom(roomId) && roomContext.getRoomInfo(roomId).getHostId() == myId) {
            roomContext.deleteRoom(roomId, myId);
            String dest = String.format(NetworkConstants.TOPIC_ROOM_FORMAT, roomId + "");
            //解散房间用户
            StompMessage stompMessage = MessageUtil.createStompMessage("room", "dismiss");
            simpMessagingTemplate.convertAndSend(dest, stompMessage); //广播以取消游戏.
            return true;
        }
        return false;
    }

    @Override
    //结束战斗
    public void endCombat(int myId, int winner) {
        int roomId = roomContext.knowUserInWhichRoom(myId);
        if (myId >= 0 && winner >= 0 && roomId >= 0) { //如果相关信息合法
            roomContext.forceDeleteRoom(roomId); //强制删除房间（其实就是不检查权限）
            frameSyncService.stopSync(roomId); //停止帧同步
            String dest = String.format(NetworkConstants.TOPIC_ROOM_FORMAT, roomId + "");
            JSONObject data = new JSONObject();
            data.put("winner", winner); //附带获胜者信息
            StompMessage stompMessage = MessageUtil.createStompMessage("room", "end", data);
            simpMessagingTemplate.convertAndSend(dest, stompMessage);
        }
    }

    @Override
    public boolean uploadPlayerInfo(int myId, List<CharacterInfo> info) {
        int roomId = roomContext.knowUserInWhichRoom(myId);
        if (roomContext.existsRoom(roomId)) {
            int gameId = roomContext.getGameId(myId, roomId);
            LogUtil.print(String.format("receive %s from %d", info, myId));
            if (roomContext.addPlayerInfo(roomId, gameId, info)) {
                if (roomContext.readyToStart(roomId)) {
                    String dest = String.format(NetworkConstants.TOPIC_ROOM_FORMAT, roomId + "");
                    StompMessage stompMessage = MessageUtil.createStompMessage("room", "start",
                            JSONObject.fromObject(roomContext.getPlayerInfo(roomId)));
                    simpMessagingTemplate.convertAndSend(dest, stompMessage);
                }
                return true;
            }
        }
        return false;
    }
}
