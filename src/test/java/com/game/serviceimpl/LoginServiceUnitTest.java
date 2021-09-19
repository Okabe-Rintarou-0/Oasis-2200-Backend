package com.game.serviceimpl;

import com.game.dao.UserDao;
import com.game.entity.UserAuthority;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/9/5 18:54
 */
public class LoginServiceUnitTest {
    @Mock
    private UserDao userDao;

    @InjectMocks
    private LoginServiceImpl loginService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("测试登录")
    public void testLogin() {
        Mockito.when(userDao.findUserAuthorityByUsernameAndPwd("lzh", "123"))
                .thenReturn(new UserAuthority(1, "123", "123", "123", 1));
        Mockito.when(userDao.findUserAuthorityByUsernameAndPwd("111", "111"))
                .thenReturn(null);

        Message message = loginService.login("lzh", "123");
        Assertions.assertEquals(message.status, MessageUtil.STAT_OK);
        message = loginService.login("111", "111");
        Assertions.assertEquals(message.status, MessageUtil.STAT_INVALID);
    }
}
