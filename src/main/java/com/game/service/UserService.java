package com.game.service;

import com.game.entity.UserAuthority;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/28 16:01
 */
public interface UserService {
    UserAuthority getUserAuthority(int userId);
}
