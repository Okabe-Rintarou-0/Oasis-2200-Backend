package com.game.utils.messageUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MessageUtil {

    public static final int STAT_OK = 1;
    public static final int STAT_INVALID = -50;

    public static final String MSG_LOGIN_SUCCEED = "登陆成功";
    public static final String MSG_ALREADY_LOGIN = "用户已登录";
    public static final String MSG_NOT_LOGIN = "未登录";
    public static final String MSG_NO_ACCESS_RIGHT = "您没有访问权限";
    public static final String MSG_LOGIN_INVALID = "用户名或密码错误";
    public static final String MSG_USERNAME_USED = "用户名已被使用";
    public static final String MSG_VCODE_WRONG = "验证码错误";
    public static final String MSG_VCODE_OUTDATED = "验证码已过期";
    public static final String MSG_REGISTER_SUCCEED = "注册成功";

    public static Message createMessage(int status, String message, JSONObject data) {
        return new Message(status, message, data);
    }

    public static Message createMessage(int status, String message) {
        return new Message(status, message, null);
    }

    public static Message createMessage(int status, JSONObject data) {
        return new Message(status, null, data);
    }

    public static StompMessage createStompMessage(String topic, String msg, JSONObject data) {
        return new StompMessage(topic, msg, data);
    }

    public static StompMessage createStompMessage(String topic, String msg) {
        return new StompMessage(topic, msg, null);
    }

    public static ArrayMessage createArrayMessage(int status, String message, JSONArray data) {
        return new ArrayMessage(status, message, data);
    }

    public static RedisMessage createRedisMessage(String intraNetIp, String message, Object data) {
        return new RedisMessage(intraNetIp, message, data);
    }

    public static RedisMessage createRedisMessage(String intraNetIp, String message) {
        return new RedisMessage(intraNetIp, message, null);
    }
}
