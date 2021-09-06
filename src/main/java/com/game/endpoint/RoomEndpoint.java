package com.game.endpoint;

import com.game.context.RoomContext;
import com.game.entity.Room;
import com.game.entity.RoomGlobalFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author lzh
 * @Title: 添加房间endpoint
 * @Package
 * @Description: Expose Room Endpoint
 * @date 2021/8/14 9:45
 */
@Component
@Endpoint(id = "room")
public class RoomEndpoint {
    private final RoomGlobalFeature roomGlobalFeature = new RoomGlobalFeature();

    @Autowired
    private RoomContext roomContext;

    @PostConstruct
    public void init() {

    }

    @ReadOperation
    public RoomGlobalFeature getRoomGlobalFeature() {
        Map<String, Room> allRooms = roomContext.getAllRooms();
        roomGlobalFeature.setRooms(allRooms);
        roomGlobalFeature.setRoomCount(allRooms.size());
        return roomGlobalFeature;
    }
}
