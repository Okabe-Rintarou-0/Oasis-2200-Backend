package com.game.dao;

import com.game.dto.RoomDto;
import com.game.dto.RoomFeatureDto;

import java.util.List;

public interface RoomDao {
    List<RoomDto> getRoomInfos();

    RoomDto getRoomInfo(int roomId);

    void clearRooms();
}
