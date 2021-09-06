package com.game.serviceimpl;

import com.game.context.StatusSyncContext;
import com.game.service.StatusSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description: Test Status Synchronization Service
 * @date 2021/8/13 13:12
 */

public class StatusSyncServiceUnitTest {
    @Mock
    StatusSyncContext statusSyncContext;

    @Mock
    SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    StatusSyncService statusSyncService;

    @BeforeEach
    public void setUp() {

    }
}
