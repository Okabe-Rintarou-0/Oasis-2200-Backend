package com.game.daoimpl;

import com.game.dto.RoomFeatureDto;
import com.game.entity.Room;
import com.game.entity.User;
import com.game.properties.RoomProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/9/5 15:43
 */
public class RoomCacheDaoUnitTest {
    @Mock
    RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    RoomCacheDaoImpl roomCacheDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(roomCacheDao, "sectionSize", 50);

        for (int i = 0; i < 50; ++i) {
//            Mockito.when(redisTemplate.boundHashOps())
        }
    }

    @Test
    @DisplayName("测试创建房间")
    public void testCreateRoom() {
        roomCacheDao.addRoom(1, 0, new RoomFeatureDto());
    }

}
