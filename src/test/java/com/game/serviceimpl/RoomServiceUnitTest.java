package com.game.serviceimpl;

import com.game.context.RoomContext;
import com.game.dao.RoomDao;
import com.game.dto.RoomDto;
import com.game.serviceimpl.RoomServiceImpl;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class RoomServiceUnitTest {
    @Mock
    private RoomContext roomContext; //[mock]代替[bean]

    @Mock
    private RoomDao roomDao;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private RoomServiceImpl roomService; //把[mock]像[bean]一样注入roomService

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(roomContext.addRoom(-1)).thenReturn(-1);
        Mockito.when(roomContext.addRoom(0)).thenReturn(0);

        Mockito.when(roomDao.getRoomInfo(0)).thenReturn(new RoomDto(0, 0, "lzh", new HashSet<>()));

        Mockito.when(roomContext.addRandomRoomMember(0)).thenReturn(true);
        Mockito.when(roomContext.addRandomRoomMember(-1)).thenReturn(false);

    }

    @Test
    @DisplayName("测试合法添加房间")
    public void testAddRoomValid() {
        Message result = roomService.addRoom(0);
        // 检查结果
        MatcherAssert.assertThat(result.status, Matchers.is(MessageUtil.STAT_OK));

        // 验证调用上面的roomService 方法后是否 roomContext.addRoom(0) 调用过
        Mockito.verify(roomContext).addRoom(0);
        Mockito.verify(roomDao).getRoomInfo(0);
    }

    @Test
    @DisplayName("测试非法添加房间")
    public void testAddRoomInvalid() {
        Message result = roomService.addRoom(-1);
        MatcherAssert.assertThat(result.status, Matchers.is(MessageUtil.STAT_INVALID));
        Mockito.verify(roomContext).addRoom(-1);
    }

    @Test
    @DisplayName("测试正常加入房间")
    public void testJoinRoomValid() {
        roomContext.addRoom(1); //用户1创房
        //用户0 已经有房间了
        Mockito.when(roomContext.knowUserInWhichRoom(0)).thenReturn(1);
        Mockito.when(roomDao.getRoomInfo(1)).thenReturn(new RoomDto(1, 0, null, new HashSet<>()));
        RoomDto roomDto = roomService.joinRandomValidRoom(0);
        MatcherAssert.assertThat(roomDto.getRoomId(), Matchers.is(1));

        //用户2加入房间
        Mockito.when(roomContext.knowUserInWhichRoom(0)).thenReturn(-1);
        Mockito.when(roomContext.addRandomRoomMember(0)).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                Mockito.when(roomContext.knowUserInWhichRoom(0)).thenReturn(2);
                return true;
            }
        });
        Mockito.when(roomDao.getRoomInfo(2)).thenReturn(new RoomDto(2, 0, null, new HashSet<>()));
        roomDto = roomService.joinRandomValidRoom(0);
        MatcherAssert.assertThat(roomDto.getRoomId(), Matchers.is(2));

        Mockito.when(roomContext.addRandomRoomMember(2)).thenReturn(true);
        Mockito.when(roomContext.knowUserInWhichRoom(2)).thenReturn(-1);
        roomDto = roomService.joinRandomValidRoom(2);
        Assertions.assertNull(roomDto);

        Mockito.when(roomContext.addRandomRoomMember(2)).thenReturn(false);
        roomDto = roomService.joinRandomValidRoom(2);
        Assertions.assertNull(roomDto);
//        Mockito.verify(roomContext).addRandomRoomMember(0);
        //Mockito.verify(roomContext).getRoomInfo(0);
    }

    @Test
    @DisplayName("测试获取当前房间号和GID")
    public void testJoinRoomInvalid() {
        Mockito.when(roomContext.knowUserInWhichRoom(4)).thenReturn(1);
        Mockito.when(roomContext.getGameId(4, 1)).thenReturn(1);
        Assertions.assertEquals(roomService.getCurrentRoom(4), 1);
        Assertions.assertEquals(roomService.getCurrentGameId(4), 1);

        roomService.clearRooms();
    }

    @Test
    @DisplayName("测试加入房间")
    public void testJoinRoom() {
        Mockito.when(roomContext.addRoomMember(1, -1)).thenReturn(true);
        Mockito.when(roomDao.getRoomInfo(1)).thenReturn(new RoomDto(1, -1, "123", new HashSet<>()));
        Assertions.assertEquals(roomService.joinRoom(1).getHostName(), "123");

        Mockito.when(roomContext.addRoomMember(1, -1)).thenReturn(false);
        Assertions.assertNull(roomService.joinRoom(1));
    }

    @Test
    @DisplayName("测试获取所有房间和删除房间")
    public void testGetAllRoomsAndDeleteRoom() {
        List<RoomDto> roomDtoList = new ArrayList<>();
        roomDtoList.add(new RoomDto(1, 1, "123", new HashSet<>()));
        Mockito.when(roomDao.getRoomInfos()).thenReturn(roomDtoList);
        Assertions.assertEquals(roomService.getAllRooms(), roomDtoList);

        roomService.forceDeleteRoom(1);
    }

    @Test
    @DisplayName("测试离开房间")
    public void testLeaveRoom() {
        Mockito.when(roomContext.leaveRoom(1,-1)).thenReturn(false);
        Assertions.assertFalse(roomService.leaveRoom(1));

        Mockito.when(roomContext.leaveRoom(1,-1)).thenReturn(true);
        Mockito.when(roomContext.existsRoom(1)).thenReturn(false);
        Assertions.assertTrue(roomService.leaveRoom(1));

        Mockito.when(roomContext.leaveRoom(1,-1)).thenReturn(true);
        Mockito.when(roomContext.existsRoom(1)).thenReturn(true);
        Assertions.assertTrue(roomService.leaveRoom(1));
    }
}
