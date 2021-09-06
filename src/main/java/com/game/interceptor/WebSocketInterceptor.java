package com.game.interceptor;

import com.game.utils.httpUtils.HttpUtil;
import com.game.utils.jwtUtils.JwtUtil;
import com.game.utils.testUtils.ClusterTestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketInterceptor implements HandshakeInterceptor {

    @Autowired
    ClusterTestUtil clusterTestUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
//        HashMap<String, String> paramMap = HttpUtil.decodeParamMap(serverHttpRequest.getURI().getQuery());
//        String token;
//        if ((token = paramMap.get("token")) == null || JwtUtil.getUserIdFromToken(token) == -1) {
//            System.out.println("没有token或token解析失败，应该被拦截！");
//        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        clusterTestUtil.logForWsConnection();
    }
}
