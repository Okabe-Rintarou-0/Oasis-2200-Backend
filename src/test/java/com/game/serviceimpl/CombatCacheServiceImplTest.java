package com.game.serviceimpl;

import com.game.dao.CombatCacheDao;
import com.game.dao.RoomCacheDao;
import com.game.service.FrameSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.redisson.api.RedissonClient;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/9/6 15:38
 */
public class CombatCacheServiceImplTest {
    @Mock
    RedissonClient redissonClient;

    @Mock
    String sharedRoomLockKey;

    @Mock
    CombatCacheDao combatCacheDao;

    @Mock
    RoomCacheDao roomCacheDao;

    @Mock
    FrameSyncService frameSyncService;

    @Mock
    SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    CombatCacheServiceImpl combatCacheService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("测试接受战斗")
    public void testAcceptCombat() {
        combatCacheService.acceptCombat(-1);
    }

    @Test
    @DisplayName("测试拒绝")
    public void testDeny() {
        combatCacheService.denyCombat(-1);
    }

    @Test
    @DisplayName("测试结束战斗")
    public void testEndCombat() {
    }

    @Test
    @DisplayName("测试上传用户信息")
    public void testUploadPlayerInfo() {
    }
}
