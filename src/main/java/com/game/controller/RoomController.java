package com.game.controller;

import com.game.annotation.SkipToken;
import com.game.annotation.UserLoginToken;
import com.game.dto.RoomDto;
import com.game.service.RoomCacheService;
import com.game.service.RoomService;
import com.game.utils.jwtUtils.JwtUtil;
import com.game.utils.logUtils.LogUtil;
import com.game.utils.messageUtils.ArrayMessage;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import com.game.utils.testUtils.ClusterTestUtil;
import io.swagger.annotations.Api;
import jdk.nashorn.internal.ir.annotations.Ignore;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "房间模块")
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

    @RequestMapping(value = "/createRoom", method = RequestMethod.GET)
    public Message createRoom() {
        clusterTestUtil.logWhoImAndWhatIHaveCalled("createRoom");
        return roomCacheService.createRoom(JwtUtil.getUserId());
//        return roomService.addRoom(JwtUtil.getUserId());
    }

    //测试用接口
    @SkipToken
    @RequestMapping(value = "/getAllRooms", method = RequestMethod.GET)
    public ArrayMessage getAllRooms() {
        List<RoomDto> allRooms = roomService.getAllRooms();
        return MessageUtil.createArrayMessage(MessageUtil.STAT_OK, "所有房间信息", JSONArray.fromObject(allRooms));
    }

    @SkipToken
    @RequestMapping(value = "/clearAllRooms", method = RequestMethod.GET)
    public String clearAllRooms() {
        roomService.clearRooms();
        return "清除成功!";
    }

//    @SkipToken
//    @RequestMapping(value = "/refreshRooms", method = RequestMethod.GET)
//    public List<RoomDto> refreshRooms() {
//        return roomService.refreshRooms();
//    }

    @UserLoginToken
    @RequestMapping(value = "/joinRoom", method = RequestMethod.GET)
    public Message joinRoom() {
        clusterTestUtil.logWhoImAndWhatIHaveCalled("joinRoom");
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

    @RequestMapping(value = "/joinOrCreate", method = RequestMethod.GET)
    public Message joinOrCreateRoom() {
        Integer userId = JwtUtil.getUserId();
        clusterTestUtil.logWhoImAndWhatIHaveCalled("joinRoom");
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
