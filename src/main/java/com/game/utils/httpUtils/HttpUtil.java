package com.game.utils.httpUtils;

import java.util.HashMap;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/30 10:48
 */
public class HttpUtil {
    public static HashMap<String, String> decodeParamMap(String query) {
        HashMap<String, String> paramMap = new HashMap<>();
        if (query != null) {
            String[] paramList = query.split("&");
            for (String param : paramList) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    paramMap.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return paramMap;
    }
}
