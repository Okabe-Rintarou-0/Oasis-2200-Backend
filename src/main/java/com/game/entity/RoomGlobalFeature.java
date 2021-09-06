package com.game.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author lzh
 * @Title: 房间全局信息实体类
 * @Package
 * @Description: Provide an entity for monitor
 * @date 2021/8/14 10:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomGlobalFeature {
    private int roomCount;
    // < roomId, roomInfo >
    private Map<String, Room> rooms;
}
