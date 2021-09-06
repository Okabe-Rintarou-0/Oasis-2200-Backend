package com.game.daoimpl;

import com.game.dao.RoomDao;
import com.game.dto.RoomDto;
import com.game.dto.RoomFeatureDto;
import com.game.repository.UserRepository;
import com.game.context.RoomContext;
import com.game.entity.Room;
import com.game.utils.jwtUtils.JwtUtil;
import com.game.utils.sessionUtils.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class RoomDaoImpl implements RoomDao {

    @Autowired
    RoomContext roomContext;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<RoomDto> getRoomInfos() {
        List<RoomDto> roomDtoList = new ArrayList<>();
        int maxCount = roomContext.getMaxCount();
        for (int i = 0; i < maxCount; ++i) {
            if (roomContext.existsRoom(i)) {
                Room thisRoom = roomContext.getRoomInfo(i);
                Set<String> clientNames = new HashSet<>();
                for (Map.Entry<Integer, Integer> entry : thisRoom.getClients().entrySet()) {
                    String clientName = userRepository.findOne(entry.getKey()).getNickname();
                    clientNames.add(clientName);
                }
                String hostName = userRepository.findOne(thisRoom.getHostId()).getNickname();
                roomDtoList.add(new RoomDto(i, 0, hostName, clientNames)); //0 here has no meaning.
            }
        }
        return roomDtoList;
    }

    @Override
    public void clearRooms() {
        roomContext.clearRoomContext();
    }

    @Override
    public RoomDto getRoomInfo(int roomId) {
        Integer myId = JwtUtil.getUserId();
        Room room = roomContext.getRoomInfo(roomId);
        String hostName = userRepository.findOne(room.getHostId()).getNickname();
        Set<String> clientNames = new HashSet<>();
        for (Map.Entry<Integer, Integer> entry : room.getClients().entrySet()) {
            String thisClientName = userRepository.findOne(entry.getKey()).getNickname();
            clientNames.add(thisClientName);
        }
        return new RoomDto(roomId, room.getGameId(myId), hostName, clientNames);
    }
}
