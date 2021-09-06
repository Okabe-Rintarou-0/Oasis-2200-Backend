package com.game.context;

import com.game.context.RoomContext;
import com.game.entity.Room;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest //真的去跑一下环境而不是用mock模拟
public class RoomContextUnitTest {
    @Autowired
    private RoomContext roomContext;

    @Test
    @DisplayName("测试创建房间")
    //测试滚动创建房间。
    public void testAddRoom() {
        //创建100个房间（理论上这100个都会创建成功，并且会正好达到房间数量的上限。）
        for (int i = 0; i < 100; ++i) {
            int roomId = roomContext.addRoom(i);
            Assertions.assertEquals(roomId, i); //if successfully creates a room, it will return roomId
        }

        for (int i = 100; i < 200; ++i) {
            int roomId = roomContext.addRoom(i);
            Assertions.assertEquals(roomId, -1); //room number has reached upper bound.
        }

        roomContext.clearRoomContext(); //清除所有房间信息
        //已经创建房间的用户会成为房主，因而无法再次创建新房间。
        for (int i = 0; i < 50; ++i) {
            int roomId = roomContext.addRoom(i);
            Assertions.assertEquals(roomId, i);

            //already be a host of a room, has no right to create another
            roomId = roomContext.addRoom(i);
            Assertions.assertEquals(roomId, -1);
        }
    }

    @Test
    @DisplayName("测试删除所有房间信息")
    public void testClearAll() {
        Assertions.assertNull(roomContext.getRoomInfo(0));
        roomContext.addRoom(0);
        Assertions.assertNotNull(roomContext.getRoomInfo(0));
        roomContext.clearRoomContext();
        Assertions.assertNull(roomContext.getRoomInfo(0));
    }

    @Test
    @DisplayName("测试获取房间信息")
    public void testGetRoomInfo() {
        Assertions.assertNull(roomContext.getRoomInfo(0));
        roomContext.addRoom(0);
        Room room = roomContext.getRoomInfo(0);
        Assertions.assertEquals(room.getHostId(), 0);
    }

    @Test
    @DisplayName("测试添加房间成员")
    public void testAddRoomMember() {
        //A room with only host 0
        roomContext.addRoom(0);
        Room room = roomContext.getRoomInfo(0);
        Assertions.assertEquals(room.getClients().size(), 0);

        //add room member 1
        roomContext.addRoomMember(0, 1);
        room = roomContext.getRoomInfo(0);
        Assertions.assertEquals(room.getClients().size(), 1);
        Assertions.assertEquals(room.getClients().get(1), 1);

        //let user 2 to create a new room
        //1 and 0 are in room 0, so they are not allowed to join room 2.
        roomContext.addRoom(2);
        Assertions.assertFalse(roomContext.addRoomMember(1, 0));
        Assertions.assertFalse(roomContext.addRoomMember(1, 1));
    }

    @Test
    @DisplayName("测试加入随机房间")
    public void testAddRandomRoomMember() {
        //A room with only host 0
        roomContext.addRoom(0);
        Room room = roomContext.getRoomInfo(0);
        Assertions.assertEquals(room.getClients().size(), 0);

        //Another room with only host 1
        roomContext.addRoom(1);
        room = roomContext.getRoomInfo(0);
        Assertions.assertEquals(room.getClients().size(), 0);

        //add room member 2
        roomContext.addRandomRoomMember(2);
        room = roomContext.getRoomInfo(0);
        Assertions.assertEquals(room.getClients().size(), 1);
        Assertions.assertEquals(room.getClients().get(2), 1);

        //first-fit, so room 1 has no clients
        room = roomContext.getRoomInfo(1);
        Assertions.assertEquals(room.getClients().size(), 0);

        //user who is already in a room is not allowed to join another room.
        Assertions.assertFalse(roomContext.addRandomRoomMember(0));
        Assertions.assertFalse(roomContext.addRandomRoomMember(1));
        Assertions.assertFalse(roomContext.addRandomRoomMember(2));
    }

    @Test
    @DisplayName("测试强制删除房间(不检查用户权限)")
    public void testForceDeleteRoom() {
        //user 0 create a new room.
        roomContext.addRoom(0);
        //room 0 should not be null
        Assertions.assertNotNull(roomContext.getRoomInfo(0));

        //then force delete the room 0
        roomContext.forceDeleteRoom(0);
        //user 0 should be in no room
        Assertions.assertEquals(roomContext.knowUserInWhichRoom(0), -1);
        //room 0 no longer exists
        Assertions.assertNull(roomContext.getRoomInfo(0));

        //user 0 create a new room 1
        roomContext.addRoom(0);
        //user 1 join this room
        roomContext.addRoomMember(0, 1);
        //then force delete the room.
        roomContext.forceDeleteRoom(1);
        //user 0 and 1 should be in no room.
        Assertions.assertEquals(roomContext.knowUserInWhichRoom(0), -1);
        Assertions.assertEquals(roomContext.knowUserInWhichRoom(1), -1);
        //room 1 no longer exists
        Assertions.assertNull(roomContext.getRoomInfo(1));

        //force delete an originally non-existent room.
        roomContext.forceDeleteRoom(2);
        Assertions.assertNull(roomContext.getRoomInfo(2));
    }

    @Test
    @DisplayName("测试条件删除房间（会去判断是否有权限删除）")
    public void testDeleteRoom() {
        //user 0 creates a new room.
        roomContext.addRoom(0);
        //room 0 should not be null
        Assertions.assertNotNull(roomContext.getRoomInfo(0));

        //user 1 has no right to delete room 0, for he/she is not the host.
        roomContext.deleteRoom(0, 1);
        Assertions.assertNotNull(roomContext.getRoomInfo(0));

        //user 0 is the host, so he can delete the room.
        roomContext.deleteRoom(0, 0);
        Assertions.assertNull(roomContext.getRoomInfo(0));
        Assertions.assertEquals(roomContext.knowUserInWhichRoom(0), -1);

        //user 0 creates a new room.
        roomContext.addRoom(0);
        //user 1 joins this room.
        roomContext.addRoomMember(1, 1);
        //user 0 deletes the room.
        roomContext.deleteRoom(1, 0);
        Assertions.assertNull(roomContext.getRoomInfo(1));
        Assertions.assertEquals(roomContext.knowUserInWhichRoom(0), -1);
        Assertions.assertEquals(roomContext.knowUserInWhichRoom(1), -1);
    }

    @Test
    @DisplayName("测试获取用户所在房间号")
    public void testUserInWhichRoom() {
        //user 0 and user 1 are not in any room.
        Assertions.assertEquals(roomContext.knowUserInWhichRoom(0), -1);
        Assertions.assertEquals(roomContext.knowUserInWhichRoom(1), -1);

        //user 0 creates a room
        roomContext.addRoom(0);
        Assertions.assertEquals(roomContext.knowUserInWhichRoom(0), 0);

        //user 1 joins this room
        roomContext.addRoomMember(0, 1);
        Assertions.assertEquals(roomContext.knowUserInWhichRoom(1), 0);

        //delete this room, then they will dismiss（房间解散）.
        roomContext.forceDeleteRoom(0);
        Assertions.assertEquals(roomContext.knowUserInWhichRoom(0), -1);
        Assertions.assertEquals(roomContext.knowUserInWhichRoom(1), -1);
    }

    @Test
    @DisplayName("测试获取游戏gid")
    public void getGid() { //gid is namely gameId.
        //room doesn't exist.
        Assertions.assertEquals(roomContext.getGameId(0, 0), -1);
        //user doesn't exist.
        Assertions.assertEquals(roomContext.getGameId(-1, 0), -1);

        //user 0 creates a room.
        roomContext.addRoom(0);
        //host's gid is 0.
        Assertions.assertEquals(roomContext.getGameId(0, 0), 0);

        //user 1 joins this room
        roomContext.addRoomMember(0, 1);
        Assertions.assertEquals(roomContext.getGameId(1, 0), 1);

        //delete room 0
        roomContext.forceDeleteRoom(0);

        //room doesn't exist.
        Assertions.assertEquals(roomContext.getGameId(0, 0), -1);
        Assertions.assertEquals(roomContext.getGameId(1, 0), -1);
    }

    @Test
    @DisplayName("测试上传playerInfo")
    public void testUploadPlayerInfo() {
        //正常上传
        Assertions.assertFalse(roomContext.readyToStart(0));
        Assertions.assertFalse(roomContext.readyToStart(-1));

        Assertions.assertNull(roomContext.getPlayerInfo(0));
        roomContext.addRoom(0);
        roomContext.addPlayerInfo(0, 0, new ArrayList<>());
        Assertions.assertNotNull(roomContext.getPlayerInfo(0));
        Assertions.assertFalse(roomContext.readyToStart(0));
        roomContext.addRoomMember(0, 1);
        roomContext.addPlayerInfo(0, 1, new ArrayList<>());
        Assertions.assertNotNull(roomContext.getPlayerInfo(0).get("0"));
        Assertions.assertNotNull(roomContext.getPlayerInfo(0).get("1"));
        Assertions.assertTrue(roomContext.readyToStart(0));

        //上传到不存在的房间
        Assertions.assertNull(roomContext.getPlayerInfo(-1));
        Assertions.assertFalse(roomContext.addPlayerInfo(-1, 0, new ArrayList<>()));
        Assertions.assertFalse(roomContext.addPlayerInfo(0, -1, new ArrayList<>()));
        Assertions.assertNull(roomContext.getPlayerInfo(-1));
    }
}
