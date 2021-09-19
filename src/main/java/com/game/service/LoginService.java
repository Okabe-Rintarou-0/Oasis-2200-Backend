package com.game.service;

import com.game.entity.UserAuthority;
import com.game.utils.messageUtils.Message;

public interface LoginService {
    Message login(String username, String password);
}
