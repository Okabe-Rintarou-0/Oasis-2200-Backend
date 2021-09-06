package com.game.dao;

import com.game.entity.User;
import com.game.entity.UserAuthority;

public interface UserDao {
    UserAuthority getUserAuthority(int userId);

    UserAuthority findUserAuthorityByUsernameAndPwd(String username, String password);
    UserAuthority findUserAuthorityByUsername(String username);

    String getArchiveId(Integer userId);

    String getUserNickname(Integer userId);

    void updateArchiveId(Integer userId, String newId);

    UserAuthority saveUserAuthority(String username, String password, String email, int identity);

    User saveUser(int userId, String nickname);
}
