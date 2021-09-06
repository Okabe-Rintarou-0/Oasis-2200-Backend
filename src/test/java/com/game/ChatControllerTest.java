package com.game;

import com.alibaba.fastjson.JSON;
import com.game.models.ChatMessage;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import com.game.utils.testUtils.TokenTestUtil;
import io.swagger.annotations.Api;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.Collections;;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.StompHeaders;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatControllerTest {
    @LocalServerPort
    private Integer port;

    private WebSocketStompClient webSocketStompClient;

    @BeforeEach
    public void setup() {
        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))));
    }

    private String getWsPath() {
        return String.format("ws://localhost:%d/my-ws", port);
    }

    @Autowired
    TokenTestUtil tokenTestUtil;

    @Test
    @DisplayName("测试用户聊天")
    public void testChat() throws Exception {

        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);

        webSocketStompClient.setMessageConverter(new StringMessageConverter());//发送string到websocket

        // pick one or use the default SimpleMessageConverter (plain Strings,bytes,JSON)
        //webSocketStompClient.setMessageConverter(new StringMessageConverter());
        //webSocketStompClient.setMessageConverter(new ByteArrayMessageConverter());
        //webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(getWsPath(), new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe("/topics/chat", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("Received message: " + payload);
                blockingQueue.add((String) payload);
            }
        });

        session.send("/app/chatroom/" + tokenTestUtil.getTestToken("lzh"), "Mike");
        session.send("/app/chatroom/123", "Mike");
    }

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("测试获取和删除聊天记录")
    public void TestGetAndClear() {
        String hostToken = tokenTestUtil.getTestToken("bc");
        HttpHeaders hostHeaders = new HttpHeaders();
        hostHeaders.add("X-Authorization", hostToken);
        HttpEntity<String> hostHttpEntity = new HttpEntity<>(hostHeaders);
        ResponseEntity<ChatMessage[]> response = testRestTemplate.exchange("/getChatMessages", HttpMethod.GET, hostHttpEntity, ChatMessage[].class);
        ChatMessage[] chatMessages = response.getBody();
        Assertions.assertNotNull(chatMessages);
        System.out.println("Get Chat msg " + JSON.toJSONString(chatMessages));

        testRestTemplate.exchange("/clearChatMessages", HttpMethod.GET, hostHttpEntity, ChatMessage[].class);
        response = testRestTemplate.exchange("/getChatMessages", HttpMethod.GET, hostHttpEntity, ChatMessage[].class);
        chatMessages = response.getBody();
        Assertions.assertNotNull(chatMessages);
        Assertions.assertEquals(chatMessages.length, 0);
        System.out.println("Get Chat msg " + JSON.toJSONString(chatMessages));
    }


//    @Test
//    public void verifyWelcomeMessageIsSent() throws Exception {
//        CountDownLatch latch = new CountDownLatch(1);
//
//        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
//
//        StompSession session = webSocketStompClient
//                .connect(getWsPath(), new StompSessionHandlerAdapter() {
//                })
//                .get(1, SECONDS);
//
//        session.subscribe("/app/chat", new StompFrameHandler() {
//
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return Message.class;
//            }
//
//            @Override
//            public void handleFrame(StompHeaders headers, Object payload) {
//                latch.countDown();
//            }
//        });
//
//        if (!latch.await(1, TimeUnit.SECONDS)) {
//            fail("Message not received");
//        }
//    }

}
