package com.game.serviceimpl;

import com.game.dao.UserDao;
import com.game.entity.UserAuthority;
import com.game.service.RegisterService;
import com.game.utils.logUtils.LogUtil;
import com.game.utils.mailUtils.MailUtil;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import com.game.utils.sessionUtils.SessionUtil;
import com.game.utils.timeUtils.TimeUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/29 11:13
 */
@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    UserDao userDao;

    private int cmpVCode(String vcode) {
        String vcodeTime = SessionUtil.getVCode();
        String[] vcodeTimeArray = vcodeTime.split("#");

        //先比较验证码是否正确
        if (vcodeTimeArray[0].equals(vcode)) {
            //若正确，再判断是否超时
            if (TimeUtil.cmpTime(vcodeTimeArray[1])) {
                return 1;//验证成功
            } else {
                return 2;//验证码超时失效
            }
        }
        return 3;//验证码不匹配
    }

    @Override
    @Transactional
    public Message registerWithoutVCode(Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String email = params.get("email");
        if (username == null || password == null || email == null)
            return MessageUtil.createMessage(MessageUtil.STAT_INVALID, "参数错误！");
        UserAuthority userAuthority = userDao.findUserAuthorityByUsername(username);
        if (userAuthority != null) {
            LogUtil.print(MessageUtil.MSG_USERNAME_USED);
            return MessageUtil.createMessage(MessageUtil.STAT_INVALID, MessageUtil.MSG_USERNAME_USED);
        }
        userAuthority = userDao.saveUserAuthority(username, password, email, 1);
        userDao.saveUser(userAuthority.getUserId(), "123");
        return MessageUtil.createMessage(MessageUtil.STAT_OK, MessageUtil.MSG_REGISTER_SUCCEED);
    }

    @Override
    public boolean getVCode(String email) {
        //LogUtil.print("getVCode...service...");
        //随机生成5位验证码
        int x = (int) ((Math.random() * 9 + 1) * 10000);
        String text = Integer.toString(x);
        boolean flag = MailUtil.sendMail(email, text);
        if (flag) {
            //发送成功，把验证码和时间记录
            String nowTime = TimeUtil.getTime();

            //存入session  验证码#时间
            JSONObject data = new JSONObject();
            data.put("vcodeTime", text + "#" + nowTime);
            SessionUtil.setSession(data);
            LogUtil.print(SessionUtil.getVCode());
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public Message register(Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String email = params.get("email");
        String vcode = params.get("vcode");
        if (username == null || password == null || email == null || vcode == null)
            return MessageUtil.createMessage(MessageUtil.STAT_INVALID, "参数错误！");
        UserAuthority userAuthority = userDao.findUserAuthorityByUsername(username);
        if (userAuthority == null) {
            int flag = cmpVCode(vcode);
            switch (flag) {
                case 1: //验证成功
                    LogUtil.print(MessageUtil.MSG_REGISTER_SUCCEED);
                    userAuthority = userDao.saveUserAuthority(username, password, email, 1);
                    userDao.saveUser(userAuthority.getUserId(), "123");
                    return MessageUtil.createMessage(MessageUtil.STAT_OK, MessageUtil.MSG_REGISTER_SUCCEED);
                case 2: //验证码超时失效
                    LogUtil.print(MessageUtil.MSG_VCODE_OUTDATED);
                    return MessageUtil.createMessage(MessageUtil.STAT_INVALID, MessageUtil.MSG_VCODE_OUTDATED);
                default://验证码不匹配
                    LogUtil.print(MessageUtil.MSG_VCODE_WRONG);
                    return MessageUtil.createMessage(MessageUtil.STAT_INVALID, MessageUtil.MSG_VCODE_WRONG);
            }
        }
        LogUtil.print(MessageUtil.MSG_USERNAME_USED);
        return MessageUtil.createMessage(MessageUtil.STAT_INVALID, MessageUtil.MSG_USERNAME_USED);
    }
}
