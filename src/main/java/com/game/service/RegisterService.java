package com.game.service;

import com.game.utils.messageUtils.Message;

import java.util.Map;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/29 11:12
 */
public interface RegisterService {
    Message registerWithoutVCode(Map<String, String> params);

    boolean getVCode(String email);

    Message register(Map<String, String> params);
}
