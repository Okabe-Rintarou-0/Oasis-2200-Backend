package com.game.pricipal;

import java.security.Principal;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/9/15 10:24
 */
public class UserPrincipal implements Principal {
    String id;

    @Override
    public String getName() {
        return id;
    }

    public UserPrincipal(String wsSessionId) {
        id = wsSessionId;
    }
}
