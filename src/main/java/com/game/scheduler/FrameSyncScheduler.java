package com.game.scheduler;

import com.game.context.RoomContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class FrameSyncScheduler {

    @Autowired
    RoomContext roomContext;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

//    @Scheduled(fixedRate = 66)//66ms
//    public void broadcastFrames() {
//        ConcurrentHashMap<Integer, Room> rooms = roomContext.getRooms();
//        for (Map.Entry<Integer, Room> entry : rooms.entrySet()) {
//            int roomId = entry.getKey();
//            Room room = entry.getValue();
//            if (room.hasStarted()) {
//                simpMessagingTemplate.convertAndSend(String.format("/topics/rooms/%d", roomId),
//                        MessageUtil.createStompMessage("rooms", "broadcast"));
//            }
//        }
//    }
}
