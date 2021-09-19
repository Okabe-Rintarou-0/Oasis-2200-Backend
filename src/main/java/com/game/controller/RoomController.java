package com.game.controller;

import com.game.annotation.SkipToken;
import com.game.annotation.UserLoginToken;
import com.game.context.WebsocketContext;
import com.game.dto.RoomDto;
import com.game.pricipal.UserPrincipal;
import com.game.service.RoomCacheService;
import com.game.service.RoomService;
import com.game.utils.jwtUtils.JwtUtil;
import com.game.utils.logUtils.LogUtil;
import com.game.utils.messageUtils.ArrayMessage;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import com.game.utils.testUtils.ClusterTestUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "房间模块")
@RequestMapping("/room")
@RestController
public class RoomController {

    @Autowired
    RoomService roomService;

    @Autowired
    RoomCacheService roomCacheService;

    @Autowired
    ClusterTestUtil clusterTestUtil;

    @Value("${cluster.strategy}")
    String strategy;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    WebsocketContext websocketContext;

    @ApiOperation(value = "创建房间", notes = "创建一个供对战使用的房间")
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public Message createRoom() {
        clusterTestUtil.logWhoImAndWhatIHaveCalled("create");
        return roomCacheService.createRoom(JwtUtil.getUserId());
//        return roomService.addRoom(JwtUtil.getUserId());
    }

    @MessageMapping("/heart-beat")
    public void heartBeat(UserPrincipal userPrincipal, Long heartBeat) {
        LogUtil.info("receive heart-beat " + heartBeat + " from user " + userPrincipal.getName());
        if (heartBeat > 0) {
            websocketContext.updateHeartBeat(userPrincipal.getName(), heartBeat);
        }
//        simpMessagingTemplate.convertAndSendToUser(userPrincipal.getName(), NetworkConstants.TOPIC_HEART_BEAT, websocketContext.createHeartBeatMsg());
    }

    //测试用接口
    @SkipToken
    @ApiOperation(value = "获取所有房间信息", notes = "获取所有房间信息")
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public ArrayMessage getAllRooms() {
        List<RoomDto> allRooms = roomService.getAllRooms();
        return MessageUtil.createArrayMessage(MessageUtil.STAT_OK, "所有房间信息", JSONArray.fromObject(allRooms));
    }

    @SkipToken
    @ApiOperation(value = "清除房间信息", notes = "清除房间信息")
    @RequestMapping(value = "/clearAll", method = RequestMethod.GET)
    public String clearAllRooms() {
        roomService.clearRooms();
        return "清除成功!";
    }

    @UserLoginToken
    @ApiOperation(value = "加入房间", notes = "玩家加入一个房间进行对战")
    @RequestMapping(value = "/join", method = RequestMethod.GET)
    public Message joinRoom() {
        clusterTestUtil.logWhoImAndWhatIHaveCalled("join");
        RoomDto roomDto = roomCacheService.joinRoom(JwtUtil.getUserId());
        if (roomDto != null) {
            return MessageUtil.createMessage(MessageUtil.STAT_OK, "加入成功！", JSONObject.fromObject(roomDto));
        } else {
            return MessageUtil.createMessage(MessageUtil.STAT_INVALID, "加入失败");
        }
//        RoomDto roomDto = roomService.joinRandomValidRoom(JwtUtil.getUserId());
//        if (roomDto == null) {
//            return roomService.addRoom(JwtUtil.getUserId());
//        }
//        return MessageUtil.createMessage(MessageUtil.STAT_OK, "加入成功！", JSONObject.fromObject(roomDto));
    }

    @ApiOperation(value = "加入或创建房间", notes = "如果房间加入失败则创建房间")
    @RequestMapping(value = "/joinOrCreate", method = RequestMethod.GET)
    public Message joinOrCreateRoom() {
        Integer userId = JwtUtil.getUserId();
        RoomDto roomDto = roomCacheService.joinRoom(userId);
        if (roomDto != null) {
            return MessageUtil.createMessage(MessageUtil.STAT_OK, "加入成功！", JSONObject.fromObject(roomDto));
        } else {
            roomCacheService.createRoom(userId);
            return MessageUtil.createMessage(MessageUtil.STAT_OK, "创建成功！");
        }
//        RoomDto roomDto = roomService.joinRandomValidRoom(userId);
//        if (roomDto == null) {
//            return roomService.addRoom(userId);
//        }
//        return MessageUtil.createMessage(MessageUtil.STAT_OK, "加入成功！", JSONObject.fromObject(roomDto));
    }


//    @RequestMapping(value = "/removeRoom", method = RequestMethod.GET)
//    public void removeRoom(@RequestParam Integer roomId) {
//        LogUtil.print("Remove room: " + roomId);
//        if (strategy.equals("shared")) {
//            clusterTestUtil.logWhoImAndWhatIHaveCalled("removeRoom");
//            roomCacheService.removeRoom(roomId);
//        } else {
//            roomService.forceDeleteRoom(roomId);
//        }
//    }
//
//    @RequestMapping(value = "/leaveRoom", method = RequestMethod.GET)
//    public Message leaveRoom(@RequestParam Integer roomId) {
//        if (roomService.leaveRoom(roomId)) {
//            LogUtil.print("leave Room: " + roomId + " succeed!");
//            return MessageUtil.createMessage(MessageUtil.STAT_OK, "退出成功");
//        } else {
//            return MessageUtil.createMessage(MessageUtil.STAT_INVALID, "退出失败！");
//        }
//    }
//
//    @RequestMapping(value = "/getCurrentRoom", method = RequestMethod.GET)
//    public Message getCurrentRoom() {
//        int currentRoom = -1;
//        if (strategy.equals("shared")) {
//            RoomDto roomDto = roomCacheService.getRoom(JwtUtil.getUserId());
//            currentRoom = roomDto.getRoomId();
//        } else {
//            currentRoom = roomService.getCurrentRoom();
//        }
//        JSONObject data = new JSONObject();
//        data.put("roomId", currentRoom);
//        data.put("gid", roomService.getCurrentGameId());
//        return MessageUtil.createMessage(MessageUtil.STAT_OK, data);
//    }
}
