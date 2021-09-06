package com.game.daoimpl;

import com.game.context.RoomContext;
import com.game.dto.RoomDto;
import com.game.entity.Room;
import com.game.entity.User;
import com.game.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class RoomDaoUnitTest {
    @Mock
    private RoomContext roomContext;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoomDaoImpl roomDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        //two rooms
        //room1 host: lzh
        //room2 host: bc
        //room3 host: lc client: zby
        Mockito.when(roomContext.getRoomInfo(0)).thenReturn(new Room(0));
        Mockito.when(roomContext.getRoomInfo(1)).thenReturn(new Room(1));
        Room room_3 = new Room(2);
        room_3.addMember(3);
        Mockito.when(roomContext.getRoomInfo(2)).thenReturn(room_3);

        Mockito.when(userRepository.findOne(0)).thenReturn(new User(0, null, "lzh"));
        Mockito.when(userRepository.findOne(1)).thenReturn(new User(1, null, "bc"));
        Mockito.when(userRepository.findOne(2)).thenReturn(new User(2, null, "lc"));
        Mockito.when(userRepository.findOne(3)).thenReturn(new User(3, null, "zby"));

        Mockito.when(roomContext.getMaxCount()).thenReturn(20);
        for (int i = 0; i < 20; ++i)
            Mockito.when(roomContext.existsRoom(i)).thenReturn(false);
        Mockito.when(roomContext.existsRoom(2)).thenReturn(true);
    }

    @Test
    @DisplayName("测试获取房间信息")
    public void testGetRoomInfo() {
        //测试房间1
        RoomDto roomDto = roomDao.getRoomInfo(0);
        System.out.println(roomDto);
        Assertions.assertEquals(roomDto.getRoomId(), 0);
        Assertions.assertEquals(roomDto.getGid(), -1);
        Assertions.assertEquals(roomDto.getHostName(), "lzh");
        Mockito.verify(roomContext).getRoomInfo(0);
        Mockito.verify(userRepository).findOne(0);

        //测试房间2
        roomDto = roomDao.getRoomInfo(1);
        System.out.println(roomDto);
        Assertions.assertEquals(roomDto.getRoomId(), 1);
        Assertions.assertEquals(roomDto.getGid(), -1);
        Assertions.assertEquals(roomDto.getHostName(), "bc");
        Mockito.verify(roomContext).getRoomInfo(1);
        Mockito.verify(userRepository).findOne(1);

        //测试房间3
        roomDto = roomDao.getRoomInfo(2);
        System.out.println(roomDto);
        Assertions.assertEquals(roomDto.getRoomId(), 2);
        Assertions.assertEquals(roomDto.getGid(), -1);
        Assertions.assertEquals(roomDto.getHostName(), "lc");
        Assertions.assertEquals(roomDto.getClientNames().size(), 1);
        Assertions.assertTrue(roomDto.getClientNames().contains("zby"));
        Mockito.verify(roomContext).getRoomInfo(2);
        Mockito.verify(userRepository).findOne(2);
    }

    @Test
    @DisplayName("测试获取所有房间信息")
    public void testGetRoomInfos() {
        RoomDto roomDto = roomDao.getRoomInfo(2);
        List<RoomDto> expected = new ArrayList<>();
        roomDto.setGid(0);
        expected.add(roomDto);
        Assertions.assertEquals(expected, roomDao.getRoomInfos());
        Mockito.verify(roomContext).getMaxCount();
        Mockito.verify(roomContext, Mockito.times(2)).getRoomInfo(2);
        for (int i = 0; i < 20; ++i)
            Mockito.verify(roomContext).existsRoom(i);
        roomDao.clearRooms();
    }
}
