package com.game.interceptor;

import com.game.context.WebsocketContext;

import com.game.pricipal.UserPrincipal;
import com.game.utils.jwtUtils.JwtUtil;
import com.game.utils.logUtils.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class UserInterceptor implements ChannelInterceptor {
    @Autowired
    private WebsocketContext websocketContext;

    @Autowired
    private String intraNetIp;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return null;
        }
        String wsSessionId = accessor.getSessionId();
        StompCommand thisCmd = accessor.getCommand();
        if (thisCmd != null) {
//            LogUtil.info("Cmd = " + thisCmd.getMessageType());
//            LogUtil.print(JSON.toJSONString(accessor.getNativeHeader("token")));
            switch (thisCmd) {
                case CONNECT: {
                    break;
                }
                case DISCONNECT: {
                    List<String> httpTokens = accessor.getNativeHeader("token");
                    if (httpTokens != null && httpTokens.size() == 1) {
                        String token = httpTokens.get(0);
                        Integer userId = JwtUtil.getUserIdFromToken(token);
                        if (userId != null) {
                            LogUtil.info("unbind " + wsSessionId + " with " + userId);
                            websocketContext.unbind(wsSessionId);
                        }
                    }
                    break;
                }
                case SUBSCRIBE: {
                    List<String> httpTokens = accessor.getNativeHeader("token");
                    if (httpTokens != null && httpTokens.size() == 1) {
                        String token = httpTokens.get(0);
                        if (token != null) {
                            LogUtil.info("token = " + token);
                            Integer userId = JwtUtil.getUserIdFromToken(token);
                            LogUtil.info("userId = " + userId);
                            String destination = accessor.getDestination();
                            LogUtil.print("Ws(%s) has subscribe to: %s, ip = %s",
                                    wsSessionId, destination, intraNetIp);
                            if (userId >= 0 && destination != null && destination.contains("room")) {
                                websocketContext.bind(wsSessionId, userId);
                            }
                            break;
                        }
                    }
                    break;
                }
                case UNSUBSCRIBE:
                    LogUtil.print("Ws(%s) has unsubscribe to: %s, ip = %s",
                            wsSessionId, accessor.getDestination(), intraNetIp);
                    break;
                case SEND:
                    accessor.setUser(new UserPrincipal(wsSessionId));
//                    LogUtil.info("hb = " + Arrays.toString(accessor.getHeartbeat()));
//                    LogUtil.print("Ws(%s) has send to: %s, ip = %s",
//                            wsSessionId, accessor.getDestination(), intraNetIp);
                    break;
                default:
                    break;
            }
        }
        return message;
    }
}
