package com.game.utils.testUtils;

import com.game.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/9/4 13:55
 */
@Component
public class TokenTestUtil {
    @Autowired
    LoginService loginService;

    public String getTestToken(String username) {
        return loginService.login(username, "123").data.getString("token");
    }
}
