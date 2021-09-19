package com.game.dao;

import com.game.dto.RoomDto;
import com.game.dto.RoomFeatureDto;

import java.util.Map;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/25 14:59
 */
public interface RoomCacheDao {
    Map<String, RoomFeatureDto> getRoomFeatures(int sectionId);

    RoomDto getRoomInfo(int roomId, RoomFeatureDto roomFeatureDto);

    RoomFeatureDto getRoomFeature(int roomId);

    int getLastRoomIndex();

    Map<String, Integer> getUserStates();

    int inWhichRoom(int myId);

    void addRoom(int hostId, int roomId, RoomFeatureDto roomFeature);

    RoomDto addRoomMember(int roomId, int clientId);

    void removeRoom(int roomId);

    void clearContext();

    boolean existsRoom(int roomId);

    void tryDeleteMyRoom(int myId);

    boolean canAcceptOrDeny(int myId, int roomId);

    int getFirstDeletedRoomId();

    void addDeletedRoomId(int roomId);
}
