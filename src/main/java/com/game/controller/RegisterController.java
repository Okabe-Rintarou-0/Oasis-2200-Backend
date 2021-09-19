package com.game.controller;

import com.game.annotation.SkipToken;
import com.game.service.RegisterService;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "注册模块")
@RestController
public class RegisterController {
    @Autowired
    RegisterService registerService;

    @SkipToken
    @ApiOperation(value = "获取vcode", notes = "获取vcode")
    @RequestMapping(value = "/getvcode", method = RequestMethod.GET)
    public Message getVCode(@RequestParam String email) {
        boolean flag = registerService.getVCode(email);
        if (flag) {
            return MessageUtil.createMessage(MessageUtil.STAT_OK, "发送验证码成功");                 //生成了验证码并发送给了用户
        } else {
            return MessageUtil.createMessage(MessageUtil.STAT_INVALID, "发送验证码失败");                //未获取到
        }
    }

    @SkipToken
    @ApiOperation(value = "用户注册（无需vcode）", notes = "发送请求的用户注册（无需vcode）")
    @RequestMapping(value = "/registerWithoutVCode", method = RequestMethod.POST)
    public Message registerWithoutVCode(@RequestBody Map<String, String> params) {
        return registerService.registerWithoutVCode(params);
    }

    @SkipToken
    @ApiOperation(value = "用户注册", notes = "发送请求的用户进行注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Message register(@RequestBody Map<String, String> params) {
        return registerService.register(params);
    }
}
