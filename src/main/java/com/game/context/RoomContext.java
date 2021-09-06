package com.game.context;

import com.alibaba.fastjson.JSON;
import com.game.annotation.Context;
import com.game.entity.CharacterInfo;
import com.game.utils.logUtils.LogUtil;
import com.game.entity.Room;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Context
public class RoomContext {
    //房间内容
    private int currentIdx = 0; //当前创建的房间ID。写的比较简单，就像滚动数组（或者时钟算法）一样。
    private final int MAX_COUNT = 500; //最大房间数
    //< 房间号，对应的房间信息 >
    private final ConcurrentHashMap<Integer, Room> rooms;
    //< 房间号，对应的玩家数据（以map方式存储） >
    private final ConcurrentHashMap<Integer, Map<String, List<CharacterInfo>>> playerInfos;
    //< 用户ID，对应的房间ID >
    private final ConcurrentHashMap<Integer, Integer> userInWhichRoom;
    //< Websocket Session Id, User Id >

    //Constructor
    public RoomContext() {
        rooms = new ConcurrentHashMap<>();
        userInWhichRoom = new ConcurrentHashMap<>();
        playerInfos = new ConcurrentHashMap<>();
    }

    public void clearRoomContext() {
        rooms.clear();
        userInWhichRoom.clear();
        playerInfos.clear();
    }

    //是否可以开启游戏（已经收集完成所有的玩家信息）
    public boolean readyToStart(int roomId) {
        if (existsRoom(roomId)) {
            return playerInfos.get(roomId).size() == 2; //双人对战
        }
        return false;
    }

    public Map<String, List<CharacterInfo>> getPlayerInfo(int roomId) {
        return playerInfos.get(roomId);
    }

    //添加玩家信息
    public boolean addPlayerInfo(int roomId, int gameId, List<CharacterInfo> info) {
        if (playerInfos.containsKey(roomId) && gameId >= 0) {
            playerInfos.get(roomId).put(gameId + "", info);
            return true;
        }
        return false;
    }

    //强制删除房间 不检查权限
    public void forceDeleteRoom(int roomId) {
        Room room = getRoomInfo(roomId);
        //如果不存在房间 或者 “我”不是房主，则无法删除该房间。
        if (!existsRoom(roomId)) {
            return;
        }
        //更新房间里所有玩家所在的房间号，-1代表不在任何房间。
        //clients
        for (Map.Entry<Integer, Integer> entry : room.getClients().entrySet()) {
            userInWhichRoom.put(entry.getKey(), -1);
        }
        userInWhichRoom.put(room.getHostId(), -1); //host
        rooms.remove(roomId); //删除房间
    }

    /*
    public List<String> getAllPlayerInfos(int roomId) {
        if (playerInfos.containsKey(roomId)) {
            List<String> playInfoList = new ArrayList<>();
            Map<String, List<CharacterInfo>> thisPlayerInfo = playerInfos.get(roomId);
            for (Map.Entry<String, List<CharacterInfo>> entry : thisPlayerInfo.entrySet()) {
                playInfoList.add(entry.getKey());
            }
            return playInfoList;
        }
        return null;
    }
    */

    public void bindWebsocketToUser(String wsSessionId, Integer userId) {

    }

    public void clearPlayerInfo(int roomId) {
        if (playerInfos.containsKey(roomId)) {
            playerInfos.get(roomId).clear();
        }
    }

    public int getMaxCount() {
        return MAX_COUNT;
    }

    public Room getRoomInfo(int roomId) {
        return rooms.get(roomId);
    }

    public boolean isFull() {
        return rooms.size() == MAX_COUNT;
    }

    public boolean existsRoom(int roomId) {
        return rooms.get(roomId) != null;
    }

    public boolean addRandomRoomMember(int clientId) {
        for (Map.Entry<Integer, Room> entry : rooms.entrySet()) {   //遍历并发列表，但是性能不佳，因为是FirstFit
            if (!entry.getValue().isFull()) {
                boolean result = addRoomMember(entry.getKey(), clientId);
                if (result) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addRoomMember(int roomId, int clientId) {    //为房间添加玩家
        LogUtil.print("currentRoom is " + userInWhichRoom.get(clientId));
        System.out.println("currentRoom is " + userInWhichRoom.get(clientId));
        if (existsRoom(roomId)) { //如果存在房间
            LogUtil.print(String.valueOf(userInWhichRoom.get(clientId)));
            //若指定的用户在其他房间，则驳回请求
            if (userInWhichRoom.containsKey(clientId) && userInWhichRoom.get(clientId) >= 0) {
                return false;
            }
            Room room = getRoomInfo(roomId);
            System.out.println(JSON.toJSONString(room));
            //若当前房间已经有该用户或者房间已满，驳回。
            if (room.existsMe(clientId) || room.isFull()) {
                return false;
            }
            rooms.get(roomId).addMember(clientId);
            userInWhichRoom.put(clientId, roomId);
            return true;
        }
        return false;
    }

    public ConcurrentHashMap<Integer, Room> getRooms() {
        return rooms;
    }

    public int addRoom(int hostId) {
        if (hostId < 0) return -1; //非法用户id
        LogUtil.print("current room is " + userInWhichRoom.get(hostId));
        //如果房间已满或者创建者已经在其他房间了，则驳回
        if (isFull() || (userInWhichRoom.containsKey(hostId) && userInWhichRoom.get(hostId) >= 0)) {
            return -1;
        }
        int availableIdx = currentIdx;
        while (existsRoom(availableIdx)) { //如果当前房间不可用
            availableIdx = (currentIdx + 1) % MAX_COUNT; //寻找可以放置的房间
        }
        rooms.put(availableIdx, new Room(hostId)); //创建新房间
        userInWhichRoom.put(hostId, availableIdx); //更新用户所在房间号
        playerInfos.put(availableIdx, new HashMap<>());
        LogUtil.print("add room " + availableIdx);
        currentIdx = (availableIdx + 1) % MAX_COUNT; //类似clock algorithm 或 滚动数组
        ///TODO: 这边可以优化创建房间的逻辑
        return availableIdx;
    }

    public void deleteRoom(int roomId, int myId) {
        Room room = getRoomInfo(roomId);
        //如果不存在房间 或者 “我”不是房主，则无法删除该房间。
        if (!existsRoom(roomId) || myId != room.getHostId()) {
            return;
        }
        //更新房间里所有玩家所在的房间号，-1代表不在任何房间。
        //clients
        for (Map.Entry<Integer, Integer> entry : room.getClients().entrySet()) {
            userInWhichRoom.put(entry.getKey(), -1);
        }
        userInWhichRoom.put(myId, -1); //host
        rooms.remove(roomId); //删除房间
        playerInfos.remove(roomId);
    }

    public boolean leaveRoom(int roomId, int myId) {
        Room room = getRoomInfo(roomId);
        //如果不存在该房间，或者我不在该房间里，则我无法离开。
        if (!existsRoom(roomId) || !room.existsMe(myId)) {
            return false;
        }
        //如果我是房主，我离开房间会直接删除房间。
        if (room.getHostId() == myId) {
            deleteRoom(roomId, myId);
        } else {
            //如果我不是房主，那么更新相关状态。
            room.removeMember(myId);
            userInWhichRoom.put(myId, -1);
        }
        return true;
    }

    //获取指定的userId对应的用户在哪所房间里。
    public int knowUserInWhichRoom(int userId) {
        if (!userInWhichRoom.containsKey(userId)) {
            return -1;
        }
        return userInWhichRoom.get(userId);
    }

    //获取用户的gid。
    public int getGameId(int userId, int roomId) {
        if (!existsRoom(roomId)) {
            return -1;
        }
        return getRoomInfo(roomId).getGameId(userId);
    }

    public Map<String, Room> getAllRooms() {
        Map<String, Room> tgtRooms = new HashMap<>();
        for (Map.Entry<Integer, Room> entry : rooms.entrySet()) {
            tgtRooms.put(entry.getKey() + "", entry.getValue());
        }
        return tgtRooms;
    }

}

