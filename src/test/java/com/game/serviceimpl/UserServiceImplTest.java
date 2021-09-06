package com.game.serviceimpl;

import com.game.dao.UserDao;
import com.game.entity.UserAuthority;
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
 * @date 2021/9/6 16:07
 */
public class UserServiceImplTest {
    @Mock
    UserDao userDao;

    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        UserAuthority testAuthority = new UserAuthority("123", "123", "123", 1);
        Mockito.when(userDao.getUserAuthority(1))
                .thenReturn(testAuthority);
        Mockito.when(userDao.getUserAuthority(-1))
                .thenReturn(null);
    }

    @Test
    @DisplayName("测试获取用户权限")
    public void testGetUserAuth() {
        UserAuthority testAuthority = new UserAuthority("123", "123", "123", 1);
        Assertions.assertEquals(userService.getUserAuthority(1), testAuthority);

        Assertions.assertNull(userService.getUserAuthority(-1));
    }
}
