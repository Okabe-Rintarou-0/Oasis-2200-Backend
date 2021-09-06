package com.game.service;

import com.game.dto.RoomDto;
import com.game.utils.messageUtils.Message;

import java.util.List;

public interface RoomService {
    Message addRoom(int hostId);

    void forceDeleteRoom(int roomId);

    boolean leaveRoom(int roomId);

    List<RoomDto> getAllRooms();

    void clearRooms();

    RoomDto joinRoom(int roomId);

    RoomDto joinRandomValidRoom(int clientId);

    int getCurrentRoom(int userId);

    int getCurrentGameId(int userId);
}
