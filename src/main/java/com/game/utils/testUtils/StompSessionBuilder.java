package com.game.utils.testUtils;

import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

//This class is to create stomp session for test.
public class StompSessionBuilder {

    public static String websocketPathFormat = "ws://localhost:%d/my-ws";

    private WebSocketStompClient webSocketStompClient;

    private String websocketPath;

    public StompSessionBuilder buildWebSocketStompClient() {
        webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))));
        return this;
    }

    public StompSessionBuilder setMessageConverter(MessageConverter messageConverter) {
        webSocketStompClient.setMessageConverter(messageConverter);
        return this;
    }

    public StompSessionBuilder allocatePort(int port) {
        websocketPath = String.format(websocketPathFormat, port);
        return this;
    }

    public StompSession buildWebsocketSession() throws InterruptedException, ExecutionException, TimeoutException {
        return webSocketStompClient
                .connect(websocketPath, new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);
    }
}
