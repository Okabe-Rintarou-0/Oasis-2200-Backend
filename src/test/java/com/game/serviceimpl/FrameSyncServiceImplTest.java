package com.game.serviceimpl;

import com.game.context.FrameSyncContext;
import com.game.dao.RoomCacheDao;
import com.game.dao.RoomDao;
import com.game.entity.Frame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/9/6 14:38
 */
public class FrameSyncServiceImplTest {
    @Mock
    FrameSyncContext frameSyncContext;

    @Mock
    RoomCacheDao roomDao;

    @Mock
    SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    FrameSyncServiceImpl frameSyncService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("测试开启帧同步")
    public void testStartSync() {
        Mockito.when(frameSyncContext.checkDisconnection(1))
                .thenReturn(-1);
        Mockito.when(frameSyncContext.addFrameSyncScheduler(Mockito.anyInt(), Mockito.any(Runnable.class)))
                .thenReturn(true);
        Assertions.assertTrue(frameSyncService.startSync(1));

        Mockito.when(frameSyncContext.checkDisconnection(1))
                .thenReturn(0);
        Assertions.assertTrue(frameSyncService.startSync(1));
    }

    @Test
    @DisplayName("测试停止帧同步")
    public void testStopSync() {
        Mockito.when(frameSyncContext.removeFrameSyncScheduler(1))
                .thenReturn(true);
        Assertions.assertTrue(frameSyncService.stopSync(1));

        Mockito.when(frameSyncContext.removeFrameSyncScheduler(1))
                .thenReturn(false);
        Assertions.assertFalse(frameSyncService.stopSync(1));
    }

    @Test
    @DisplayName("测试上传帧")
    public void testAddFrame() {
        frameSyncService.addFrame(1, 1, new Frame());
        frameSyncService.addFrame(-1, 1, new Frame());
        frameSyncService.addFrame(1, -1, new Frame());
        frameSyncService.addFrame(null, -1, new Frame());
        frameSyncService.addFrame(null, null, new Frame());
    }
}
