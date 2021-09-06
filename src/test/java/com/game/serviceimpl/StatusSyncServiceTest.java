package com.game.serviceimpl;

import com.game.context.StatusSyncContext;
import com.game.service.StatusSyncService;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/9/6 15:06
 */
public class StatusSyncServiceTest {
    @Mock
    StatusSyncContext statusSyncContext;

    @Mock
    SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    StatusSyncServiceImpl statusSyncService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("测试状态同步")
    public void testStatusSync() {
        statusSyncService.statusSync(1, new HashMap<>());
        statusSyncService.statusSync(-1, new HashMap<>());
        statusSyncService.statusSync(null, new HashMap<>());
    }
}
