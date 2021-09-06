package com.game.daoimpl;

import com.game.entity.User;
import com.game.entity.UserAuthority;
import com.game.repository.UserAuthorityRepository;
import com.game.repository.UserRepository;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class UserDaoUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAuthorityRepository userAuthorityRepository;

    @InjectMocks
    private UserDaoImpl userDao;

    private final User testUser = new User(1, null, "123");

    private final UserAuthority testUserAuthority = new UserAuthority("lzh", "123", "lzh@sjtu.edu.cn", 0);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(userRepository.findOne(0)).thenReturn(new User(0, null, "lzh"));
        Mockito.when(userRepository.getArchiveId(0)).thenReturn("123");

        Mockito.when(userAuthorityRepository.getOne(0)).thenReturn(new UserAuthority("123", "123", "123", 1));

        Mockito.when(userAuthorityRepository.findOne("lzh", "123"))
                .thenReturn(testUserAuthority);

        Mockito.when(userRepository.save(testUser)).thenReturn(testUser);

        Mockito.when(userAuthorityRepository.save(testUserAuthority)).thenReturn(testUserAuthority);

        Mockito.when(userAuthorityRepository.findByUsername("lzh")).thenReturn(testUserAuthority);
    }

    @Test
    @DisplayName("测试获取用户昵称")
    public void testGetNickName() {
        User user = new User(0, null, "lzh");
        MatcherAssert.assertThat(user.getNickname(), Matchers.is(userDao.getUserNickname(0)));
        Mockito.verify(userRepository).findOne(0);
    }

    @Test
    @DisplayName("测试获取用户存档Id")
    public void testGetArchiveId() {
        MatcherAssert.assertThat(userDao.getArchiveId(0), Matchers.is("123"));
        Mockito.verify(userRepository).getArchiveId(0);
    }

    @Test
    @DisplayName("测试上传新的存档Id")
    public void testUpdateArchiveId() {
        userDao.updateArchiveId(0, "999");
        Mockito.verify(userRepository).setArchiveId(0, "999");
    }

    @Test
    @DisplayName("测试用户登录信息认证")
    public void testFindOne() {
        MatcherAssert.assertThat(userDao.findUserAuthorityByUsernameAndPwd("lzh", "123"),
                Matchers.is(new UserAuthority("lzh", "123", "lzh@sjtu.edu.cn", 0)));
        Mockito.verify(userAuthorityRepository).findOne("lzh", "123");
    }

    @Test
    @DisplayName("测试获取用户权限")
    public void testGetUserAuthority() {
        MatcherAssert.assertThat(userDao.getUserAuthority(0),
                Matchers.is(new UserAuthority("123", "123", "123", 1)));
        Mockito.verify(userAuthorityRepository).getOne(0);
    }

    @Test
    @DisplayName("测试保存用户")
    public void testSaveUser() {
        MatcherAssert.assertThat(userDao.saveUser(testUser.getUserId(), testUser.getNickname()),
                Matchers.is(testUser));
        Mockito.verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("测试保存用户")
    public void testSaveUserAuthority() {
        MatcherAssert.assertThat(
                userDao.saveUserAuthority(testUserAuthority.getUsername(), testUserAuthority.getPassword(), testUserAuthority.getEmail(), testUserAuthority.getIdentity()),
                Matchers.is(testUserAuthority));
        Mockito.verify(userAuthorityRepository).save(testUserAuthority);
    }

    @Test
    @DisplayName("测试根据用户名获取权限信息")
    public void testGetUserAuthorityByUsername() {
        MatcherAssert.assertThat(
                userDao.findUserAuthorityByUsername("lzh"),
                Matchers.is(testUserAuthority));
        Mockito.verify(userAuthorityRepository).findByUsername("lzh");
    }
}
