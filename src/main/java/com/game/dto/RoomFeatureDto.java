package com.game.dto;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lzh
 * @Title: 给Redis用的房间信息Dto
 * @Package
 * @Description:
 * @date 2021/8/24 9:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomFeatureDto implements Serializable {
    int hostId;
    Set<Integer> clientsId = new HashSet<>();

    public RoomFeatureDto(int hostId) {
        this.hostId = hostId;
    }

    public boolean isFull() {
        return clientsId.size() == 1;
    }

    public boolean canJoin(int myId) {
        return !isFull() && hostId != myId;
    }

    @Override
    public String toString(){
        return JSON.toJSONString(this);
    }
}
