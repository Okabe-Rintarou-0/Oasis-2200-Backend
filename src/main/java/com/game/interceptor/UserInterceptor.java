package com.game.interceptor;

import com.game.context.WebsocketContext;

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
                    List<String> httpTokens = accessor.getNativeHeader("token");
                    if (httpTokens != null && httpTokens.size() == 1) {
                        String token = httpTokens.get(0);
                        if (token != null) {
                            LogUtil.info("token = " + token);
                            Integer userId = JwtUtil.getUserIdFromToken(token);
                            LogUtil.info("userId = " + userId);
                            if (userId != null) {
                                LogUtil.info("bind " + wsSessionId + " to " + userId);
                                websocketContext.bind(wsSessionId, userId);
                            }
                        }
                    }
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
                    LogUtil.print("Ws(%s) has subscribe to: %s, ip = %s",
                            wsSessionId, accessor.getDestination(), intraNetIp);
                    break;
                }
                case UNSUBSCRIBE:
                    LogUtil.print("Ws(%s) has unsubscribe to: %s, ip = %s",
                            wsSessionId, accessor.getDestination(), intraNetIp);
                    break;
                case SEND:
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
