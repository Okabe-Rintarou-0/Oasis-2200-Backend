package com.game.controller;

import com.game.annotation.SkipToken;
import com.game.entity.UserAuthority;
import com.game.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/28 15:59
 */
@Api(tags = "用户模块")
@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @ApiOperation(value = "移除学生信息", notes = "移除学生信息（调试用）")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query")
    @SkipToken
    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    void removeUser(@RequestParam String username) {
        userService.removeUser(username);
    }

    @ApiOperation(value = "获取用户权限", notes = "根据用户名获取用户权限(即UserAuthority)")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query")
    @SkipToken
    @RequestMapping(value = "/authority", method = RequestMethod.GET)
    UserAuthority getUserAuthority(@RequestParam String username) {
        return userService.findUserAuthorityByUsername(username);
    }
}
