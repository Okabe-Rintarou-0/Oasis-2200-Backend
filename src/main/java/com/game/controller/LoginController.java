package com.game.controller;

import com.game.annotation.SkipToken;
import com.game.service.LoginService;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import com.game.utils.sessionUtils.SessionUtil;
import io.swagger.annotations.Api;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Api(tags = "登录模块")
@RestController
public class LoginController {

    @Autowired
    LoginService loginService;

    @SkipToken
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Message login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("pwd");
        return loginService.login(username, password);
    }

}
