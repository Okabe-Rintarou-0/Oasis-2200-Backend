package com.game.service;

import com.game.dto.RoomDto;
import com.game.dto.RoomFeatureDto;
import com.game.utils.messageUtils.Message;

import java.util.Map;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/24 9:25
 */
public interface RoomCacheService {
    Map<String, Map<String, RoomFeatureDto>> getRoomFeatures();

    Integer getLastRoomIndex();

    Map<String, Integer> getUserStates();

    Message createRoom(int hostId);

    RoomDto joinRoom(int clientId);

//    RoomDto getRoom(int clientId);

    void removeRoom(int roomId);

    void clearRoomContext();

    int tryGetAvailableRoomId();

    int tryGetJoinableRoomId();
}
