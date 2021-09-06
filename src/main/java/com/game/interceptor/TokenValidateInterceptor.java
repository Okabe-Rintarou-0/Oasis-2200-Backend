package com.game.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.game.annotation.SkipToken;
import com.game.entity.UserAuthority;
import com.game.service.LoginService;
import com.game.utils.logUtils.LogUtil;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import lombok.NoArgsConstructor;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

@NoArgsConstructor
public class TokenValidateInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) {
        String token = request.getHeader("X-Authorization");
        System.out.println("token = " + token);
        if (!(obj instanceof HandlerMethod)) return true;
        HandlerMethod handlerMethod = (HandlerMethod) obj;
        if (handlerMethod.getBean().getClass().getName().equals("springfox.documentation.swagger.web.ApiResourceController")) {
            return true;
        }
        Method method = handlerMethod.getMethod();
//        System.out.println("当前函数为: " + method.toString());

        //检查是否有SkipToken注释，有则跳过认证
        if (method.isAnnotationPresent(SkipToken.class)) {
            System.out.println("SkipToken");
        } else {
            // 执行认证
            if (token == null) {
                System.out.println("直接拦截，无token");
                Message message = MessageUtil.createMessage(MessageUtil.STAT_INVALID, "无Token，请携带Token登录");
                this.sendJsonBack(response, message);
                return false;
            }
            // 获取 token 中的 user id
            String username;
            try {
                username = JWT.decode(token).getClaim("username").asString();
            } catch (JWTDecodeException j) {
                Message message = MessageUtil.createMessage(MessageUtil.STAT_INVALID, "Token错误");
                this.sendJsonBack(response, message);
                return false;
            }
            System.out.println("username = " + username);
            UserAuthority user = loginService.findUserAuthorityByUsername(username);
            if (user == null) {
                Message message = MessageUtil.createMessage(MessageUtil.STAT_INVALID, "用户不存在，请重新登录");
                this.sendJsonBack(response, message);
                return false;
            }
            // 验证 token
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
            try {
                jwtVerifier.verify(token);
            } catch (JWTVerificationException e) {
                Message message = MessageUtil.createMessage(MessageUtil.STAT_INVALID, "JWT验证失败");
                this.sendJsonBack(response, message);
                return false;
            }
        }
        return true;
    }

    private void sendJsonBack(HttpServletResponse response, Message message) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(JSONObject.fromObject(message));
        } catch (IOException e) {
            LogUtil.print("send json back error");
        }
    }
}
