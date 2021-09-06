package com.game.serviceimpl;

import com.game.dao.RoomCacheDao;
import com.game.utils.redisUtils.RedisMessageManager;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/9/5 17:49
 */

public class RoomCacheServiceImplUnitTest {
    @Mock
    private RoomCacheDao roomDao;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private RedisMessageManager redisMessageManager;

    @Mock
    String sharedRoomLockKey;

    @InjectMocks
    private RoomCacheServiceImpl roomService; //把[mock]像[bean]一样注入roomService

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
}
