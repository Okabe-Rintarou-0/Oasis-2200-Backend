package com.game.serviceimpl;

import com.game.dao.RoomCacheDao;
import com.game.dao.UserDao;
import com.game.entity.User;
import com.game.entity.UserAuthority;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import com.game.utils.redisUtils.RedisMessageManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/9/5 19:09
 */
public class RegisterServiceImplTest {
    @Mock
    private UserDao userDao;

    @InjectMocks
    private RegisterServiceImpl registerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("测试注册")
    public void testRegister() {
        UserAuthority testAuthority = new UserAuthority(1, "123", "123", "123", 1);
        Mockito.when(userDao.findUserAuthorityByUsername("lzh"))
                .thenReturn(testAuthority);
        Mockito.when(userDao.findUserAuthorityByUsername("111"))
                .thenReturn(null);
        Map<String, String> params = new HashMap<>();
        Message message = registerService.register(params);
        Assertions.assertEquals(message.status, MessageUtil.STAT_INVALID);

        message = registerService.registerWithoutVCode(params);
        Assertions.assertEquals(message.status, MessageUtil.STAT_INVALID);

        params.put("username", "lzh");
        params.put("password", "123");
        params.put("email", "123");
        params.put("vcode", "123");

        message = registerService.register(params);
        Assertions.assertEquals(message.status, MessageUtil.STAT_INVALID);

        message = registerService.registerWithoutVCode(params);
        Assertions.assertEquals(message.status, MessageUtil.STAT_INVALID);

        params.put("username", "111");
        message = registerService.register(params);
        Assertions.assertEquals(message.status, MessageUtil.STAT_INVALID);

//        Mockito.when(userDao.saveUser(Mockito.anyInt(), Mockito.anyString())).thenReturn(new User());
        Mockito.when(userDao.saveUserAuthority(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(new UserAuthority(1, "123", "123", "123", 1));
        message = registerService.registerWithoutVCode(params);
        Assertions.assertEquals(message.status, MessageUtil.STAT_OK);
    }

}
