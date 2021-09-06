package com.game.controller;

import com.game.entity.UserAuthority;
import com.game.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/28 15:59
 */
@RequestMapping("/User")
@RestController
public class UserController {
    @Autowired
    UserService userService;

//    @RequestMapping("/authority")
//    UserAuthority getUserAuthority(@RequestParam int userId) {
//        return userService.getUserAuthority(userId);
//    }
}
