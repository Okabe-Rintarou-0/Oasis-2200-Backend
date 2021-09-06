package com.game.serviceimpl;

import com.game.context.RoomContext;
import com.game.entity.CharacterInfo;
import com.game.entity.Room;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @Title:
 * @Package
 * @Description: CombatServiceImplTest
 * @date 2021/9/6 15:12
 */
public class CombatServiceImplTest {
    @Mock
    RoomContext roomContext;

    @Mock
    FrameSyncServiceImpl frameSyncService;

    @Mock
    SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    CombatServiceImpl combatService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("测试接受战斗")
    public void testAcceptCombat() {
        Mockito.when(roomContext.knowUserInWhichRoom(1))
                .thenReturn(1);
        Mockito.when(roomContext.existsRoom(1))
                .thenReturn(true);
        Mockito.when(roomContext.getRoomInfo(1))
                .thenReturn(new Room(1));
        Assertions.assertTrue(combatService.acceptCombat(1));

        Mockito.when(roomContext.knowUserInWhichRoom(1))
                .thenReturn(-1);
        Mockito.when(roomContext.existsRoom(1))
                .thenReturn(false);
        Mockito.when(roomContext.getRoomInfo(1))
                .thenReturn(new Room(1));
        Assertions.assertFalse(combatService.acceptCombat(1));

        Mockito.when(roomContext.knowUserInWhichRoom(1))
                .thenReturn(-1);
        Mockito.when(roomContext.existsRoom(1))
                .thenReturn(true);
        Mockito.when(roomContext.getRoomInfo(1))
                .thenReturn(new Room(1));
        Assertions.assertFalse(combatService.acceptCombat(1));
    }

    @Test
    @DisplayName("测试拒绝")
    public void testDeny() {
        Mockito.when(roomContext.knowUserInWhichRoom(1))
                .thenReturn(1);
        Mockito.when(roomContext.existsRoom(1))
                .thenReturn(true);
        Mockito.when(roomContext.getRoomInfo(1))
                .thenReturn(new Room(1));
        Assertions.assertTrue(combatService.denyCombat(1));

        Mockito.when(roomContext.knowUserInWhichRoom(1))
                .thenReturn(-1);
        Mockito.when(roomContext.existsRoom(1))
                .thenReturn(false);
        Mockito.when(roomContext.getRoomInfo(1))
                .thenReturn(new Room(1));
        Assertions.assertFalse(combatService.denyCombat(1));

        Mockito.when(roomContext.knowUserInWhichRoom(1))
                .thenReturn(-1);
        Mockito.when(roomContext.existsRoom(1))
                .thenReturn(true);
        Mockito.when(roomContext.getRoomInfo(1))
                .thenReturn(new Room(1));
        Assertions.assertFalse(combatService.denyCombat(1));
    }

    @Test
    @DisplayName("测试结束战斗")
    public void testEndCombat() {
        Mockito.when(roomContext.knowUserInWhichRoom(-1))
                .thenReturn(-1);
        Mockito.when(roomContext.knowUserInWhichRoom(2))
                .thenReturn(-1);
        combatService.endCombat(-1, 1);

        combatService.endCombat(2, 1);

        combatService.endCombat(1, -1);

        Mockito.when(roomContext.knowUserInWhichRoom(1))
                .thenReturn(1);
        combatService.endCombat(1, 1);
        combatService.endCombat(1, 0);
    }

    @Test
    @DisplayName("测试上传用户信息")
    public void testUploadPlayerInfo() {
        List<CharacterInfo> infos = new ArrayList<>();
        Mockito.when(roomContext.knowUserInWhichRoom(-1))
                .thenReturn(-1);
        Mockito.when(roomContext.knowUserInWhichRoom(1))
                .thenReturn(1);
        Mockito.when(roomContext.knowUserInWhichRoom(2))
                .thenReturn(1);

        Mockito.when(roomContext.existsRoom(-1)).thenReturn(false);
        Mockito.when(roomContext.existsRoom(1)).thenReturn(true);

        Mockito.when(roomContext.getGameId(1, 1))
                .thenReturn(1);
        Mockito.when(roomContext.getGameId(2, 1))
                .thenReturn(0);
        Mockito.when(roomContext.addPlayerInfo(1, 0, infos))
                .thenReturn(true);
        Mockito.when(roomContext.addPlayerInfo(1, 1, infos))
                .thenReturn(true);

        Mockito.when(roomContext.readyToStart(1)).thenReturn(false);
        combatService.uploadPlayerInfo(2, infos);
        Mockito.when(roomContext.readyToStart(1)).thenReturn(true);
        combatService.uploadPlayerInfo(1, infos);
        combatService.uploadPlayerInfo(-1, infos);
    }
}
