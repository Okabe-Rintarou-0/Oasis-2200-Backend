package com.game.controller;

import com.game.annotation.SkipToken;
import com.game.service.RegisterService;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "注册模块")
@RestController
public class RegisterController {
    @Autowired
    RegisterService registerService;

    @SkipToken
    @RequestMapping(value = "/getvcode", method = RequestMethod.GET)
    public Message getVCode(@RequestParam String email) {
        boolean flag = registerService.getVCode(email);
        if (flag) {
            return MessageUtil.createMessage(MessageUtil.STAT_OK, "发送验证码成功");                 //生成了验证码并发送给了用户
        } else {
            return MessageUtil.createMessage(MessageUtil.STAT_INVALID, "发送验证码失败");                //未获取到
        }
    }

//    @SkipToken
//    @RequestMapping(value = "/register", method = RequestMethod.POST)
//    public Message register(@RequestBody Map<String, String> params) {
//        String username = params.get("username");
//        String password = params.get("password");
//        String email = params.get("email");
//        String vcode = params.get("vcode");
//
//        UserAuthority userAuthority = loginService.findUserAuthorityByUsername(username);
//        if (userAuthority != null) {
//            LogUtil.print(MessageUtil.MSG_USERNAME_USED);
//            return MessageUtil.createMessage(MessageUtil.STAT_INVALID, MessageUtil.MSG_USERNAME_USED);
//        }
//        //判断验证码是否正确以及是否失效
//        int flag = loginService.cmpVCode(vcode);
//        switch (flag) {
//            case 1:
//                LogUtil.print(MessageUtil.MSG_REGISTER_SUCCEED);
//                loginService.saveUserAuthority(username, password, email, 1);
//                return MessageUtil.createMessage(MessageUtil.STAT_OK, MessageUtil.MSG_REGISTER_SUCCEED);
//            case 2:
//                LogUtil.print(MessageUtil.MSG_VCODE_OUTDATED);
//                return MessageUtil.createMessage(MessageUtil.STAT_INVALID, MessageUtil.MSG_VCODE_OUTDATED);
//            default:
//                LogUtil.print(MessageUtil.MSG_VCODE_WRONG);
//                return MessageUtil.createMessage(MessageUtil.STAT_INVALID, MessageUtil.MSG_VCODE_WRONG);
//        }
//    }

//    @SkipToken
//    @RequestMapping(value = "/noVcodeRegister", method = RequestMethod.POST)
//    public Message registerWithNoVcode(@RequestBody Map<String, String> params) {
//        String username = params.get("username");
//        String password = params.get("password");
//        String email = params.get("email");
//
//        UserAuthority userAuthority = loginService.findUserAuthorityByUsername(username);
//        if (userAuthority != null) {
//            LogUtil.print(MessageUtil.MSG_USERNAME_USED);
//            return MessageUtil.createMessage(MessageUtil.STAT_INVALID, MessageUtil.MSG_USERNAME_USED);
//        }
//        loginService.saveUserAuthority(username, password, email, 1);
//        return MessageUtil.createMessage(MessageUtil.STAT_OK, MessageUtil.MSG_REGISTER_SUCCEED);
//    }

    @SkipToken
    @RequestMapping(value = "/registerWithoutVCode", method = RequestMethod.POST)
    public Message registerWithoutVCode(@RequestBody Map<String, String> params) {
        return registerService.registerWithoutVCode(params);
    }

    @SkipToken
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Message register(@RequestBody Map<String, String> params) {
        return registerService.register(params);
    }
}
