package com.game.daoimpl;

import com.game.dao.UserDao;
import com.game.entity.User;
import com.game.entity.UserAuthority;
import com.game.repository.UserAuthorityRepository;
import com.game.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    UserAuthorityRepository userAuthorityRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public UserAuthority getUserAuthority(int userId) {
        return userAuthorityRepository.getOne(userId);
    }

    @Override
    public UserAuthority findUserAuthorityByUsernameAndPwd(String username, String password) {
        return userAuthorityRepository.findOne(username, password);
    }

    @Override
    public String getArchiveId(Integer userId) {
        return userRepository.getArchiveId(userId);
    }

    @Override
    public String getUserNickname(Integer userId) {
        User user = userRepository.findOne(userId);
        return user != null ? user.getNickname() : null;
    }

    @Override
    public void updateArchiveId(Integer userId, String newId) {
        userRepository.setArchiveId(userId, newId);
    }

    @Override
    public UserAuthority saveUserAuthority(String username, String password, String email, int identity) {
        return userAuthorityRepository.save(new UserAuthority(username, password, email, identity));
    }

    @Override
    public User saveUser(int userId, String nickname) {
        return userRepository.save(new User(userId, null, nickname));
    }

    @Override
    public void removeUser(String username) {
        userAuthorityRepository.removeUserAuthorityByUsername(username);
    }

    @Override
    public UserAuthority findUserAuthorityByUsername(String username) {
        return userAuthorityRepository.findByUsername(username);
    }
}
