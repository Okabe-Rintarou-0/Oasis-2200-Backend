package com.game.utils.jwtUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.game.entity.UserAuthority;
import com.game.service.LoginService;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component
public class JwtUtil {
    /**
     * 过期时间改为从配置文件获取
     */
    private static String accessTokenExpireTime;

    /**
     * JWT认证加密私钥(Base64加密)
     */
    private static String encryptJWTKey;

    // @Value("${jwt.accessTokenExpireTime}")
    public void setAccessTokenExpireTime(String accessTokenExpireTime) {
        JwtUtil.accessTokenExpireTime = accessTokenExpireTime;
    }

    //@Value("${jwt.encryptJWTKey}")
    public void setEncryptJWTKey(String encryptJWTKey) {
        JwtUtil.encryptJWTKey = encryptJWTKey;
    }

    //when called, jwt has been passed, so we can get the decoded value without verify the jwt again.
    public static String getUsername() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String token = request.getHeader("X-Authorization");
            try {
                Claim usernameClaim = JWT.decode(token).getClaim("username");
                if (usernameClaim != null)
                    return usernameClaim.asString();
            } catch (JWTDecodeException j) {
                return null;
            }
        }
        return null;
    }

    public static Integer getUserId() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String token = request.getHeader("X-Authorization");
            try {
                Claim userIdClaim = JWT.decode(token).getClaim("userId");
                if (userIdClaim != null)
                    return Integer.valueOf(userIdClaim.asString());
            } catch (JWTDecodeException j) {
                return -1;
            }
        }
        return -1;
    }

    public static Integer getUserIdFromToken(String token) {
        try {
            Claim userIdClaim = JWT.decode(token).getClaim("userId");
            if (userIdClaim != null)
                return Integer.valueOf(userIdClaim.asString());
        } catch (JWTDecodeException j) {
            return -1;
        }
        return -1;
    }

    public static String getUsernameFromToken(String token) {
        try {
            Claim usernameClaim = JWT.decode(token).getClaim("username");
            if (usernameClaim != null)
                return usernameClaim.asString();
        } catch (JWTDecodeException j) {
            return null;
        }
        return null;
    }
}
