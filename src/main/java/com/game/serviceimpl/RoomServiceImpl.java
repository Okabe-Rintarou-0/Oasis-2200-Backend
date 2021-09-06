package com.game.serviceimpl;

import com.game.constants.NetworkConstants;
import com.game.context.RoomContext;
import com.game.dao.RoomDao;
import com.game.dao.UserDao;
import com.game.dto.RoomDto;
import com.game.service.RoomService;
import com.game.utils.jwtUtils.JwtUtil;
import com.game.utils.logUtils.LogUtil;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    RoomContext roomContext;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    UserDao userDao;

    @Autowired
    RoomDao roomDao;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public Message addRoom(int hostId) {
        int roomId;
        if ((roomId = roomContext.addRoom(hostId)) < 0) {
            return MessageUtil.createMessage(MessageUtil.STAT_INVALID, "房间数已达到上限或已创建过房间");
        } else {
            RoomDto roomDto = roomDao.getRoomInfo(roomId);
            return MessageUtil.createMessage(MessageUtil.STAT_OK, "创建成功", JSONObject.fromObject(roomDto));
        }
    }

    @Override
    public List<RoomDto> getAllRooms() {
        return roomDao.getRoomInfos();
    }

    @Override
    public void forceDeleteRoom(int roomId) {
        roomContext.forceDeleteRoom(roomId);
    }

    @Override
    public boolean leaveRoom(int roomId) {
        Integer userId = JwtUtil.getUserId();
        if (roomContext.leaveRoom(roomId, userId)) {
            String subscribeUrl = String.format("/topics/rooms/%s", roomId);
            if (roomContext.existsRoom(roomId)) {
                simpMessagingTemplate.convertAndSend(subscribeUrl, MessageUtil.createStompMessage("rooms", "leave"));
            } else {//如果房主离开了或者最后一个人离开了 房间自动销毁
                simpMessagingTemplate.convertAndSend(subscribeUrl, MessageUtil.createStompMessage("rooms", "dismiss"));
            }
            return true;
        }
        return false;
    }

    @Override
    public RoomDto joinRoom(int roomId) {
        Integer clientId = JwtUtil.getUserId();
        if (roomContext.addRoomMember(roomId, clientId)) {
            LogUtil.print("Join room with id: " + roomId + " success.");
            simpMessagingTemplate.convertAndSend(String.format("/topics/rooms/%d", roomId), MessageUtil.createStompMessage("rooms", "join"));
            return roomDao.getRoomInfo(roomId);
        }
        return null;
    }

    @Override
    public void clearRooms() {
        roomDao.clearRooms();
    }

    @Override
    public RoomDto joinRandomValidRoom(int clientId) {  //加入一个随机房间
        int roomId = roomContext.knowUserInWhichRoom(clientId);
        if (roomId != -1) { // the player has joined a room, just return
            return roomDao.getRoomInfo(roomId);
        }
        if (roomContext.addRandomRoomMember(clientId)) {
            roomId = roomContext.knowUserInWhichRoom(clientId);
            System.out.println(roomId);
            if (roomId == -1) //由于房间全部满了等原因，没成功进房的情况
                return null;
            LogUtil.print("Join room with id: " + roomId + " success.");
            String dest = String.format(NetworkConstants.TOPIC_ROOM_FORMAT, roomId + "");
            simpMessagingTemplate.convertAndSend(dest, MessageUtil.createStompMessage("room", "join"));
            return roomDao.getRoomInfo(roomId);
        }
        return null;
    }

    @Override
    public int getCurrentRoom(int userId) {
        return roomContext.knowUserInWhichRoom(userId);
    }

    @Override
    public int getCurrentGameId(int userId) {
        return roomContext.getGameId(userId, roomContext.knowUserInWhichRoom(userId));
    }
}
