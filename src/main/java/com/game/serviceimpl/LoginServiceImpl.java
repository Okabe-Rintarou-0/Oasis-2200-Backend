package com.game.serviceimpl;

import com.game.dao.UserDao;
import com.game.entity.UserAuthority;
import com.game.service.LoginService;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    UserDao userDao;

    @Override
    public Message login(String username, String password) {
        UserAuthority userAuthority = userDao.findUserAuthorityByUsernameAndPwd(username, password);
        if (userAuthority != null) {
            JSONObject responseData = JSONObject.fromObject(userAuthority);
            responseData.remove("password");
            return MessageUtil.createMessage(MessageUtil.STAT_OK, MessageUtil.MSG_LOGIN_SUCCEED, responseData);
        }
        return MessageUtil.createMessage(MessageUtil.STAT_INVALID, MessageUtil.MSG_LOGIN_INVALID);
    }

    @Override
    public UserAuthority findUserAuthorityByUsername(String username) {
        return userDao.findUserAuthorityByUsername(username);
    }
}
