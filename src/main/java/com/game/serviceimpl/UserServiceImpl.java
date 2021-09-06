package com.game.serviceimpl;

import com.game.dao.UserDao;
import com.game.entity.UserAuthority;
import com.game.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/28 16:01
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;

    @Override
    public UserAuthority getUserAuthority(int userId) {
        return userDao.getUserAuthority(userId);
    }
}
