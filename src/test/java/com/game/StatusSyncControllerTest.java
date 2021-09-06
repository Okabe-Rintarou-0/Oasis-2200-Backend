package com.game;

import com.alibaba.fastjson.JSON;
import com.game.entity.PlayerStatus;
import com.game.utils.testUtils.StompSessionBuilder;
import com.game.utils.triggerUtils.TriggerUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author lzh
 * @Title: StatusSyncControllerTest
 * @Package
 * @Description: Test Status Synchronization Controller
 * @date 2021/8/12 18:47
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatusSyncControllerTest {
    @LocalServerPort
    private Integer port;

    //Time unit: second.
    private static final int sendInterval = 1;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    TriggerUtil triggerUtil;

    private ConcurrentMap<Integer, BlockingQueue<String>> stompMessages;

    @BeforeEach
    public void setUp() {
        stompMessages = new ConcurrentHashMap<>(5);
        for (int i = 0; i < 5; ++i)
            stompMessages.put(i, new ArrayBlockingQueue<>(1000));
    }

    @Test
    @DisplayName("测试状态同步")
    public void testStatusSynchronizationForSingleRoom() throws InterruptedException, ExecutionException, TimeoutException {

        List<Integer> messageCounts = new ArrayList<>(2);
        for (int i = 0; i < 2; ++i)
            messageCounts.add(0);

        //host
        StompSession hostStompSession = new StompSessionBuilder()
                .buildWebSocketStompClient()
                .setMessageConverter(new MappingJackson2MessageConverter())
                .allocatePort(port)
                .buildWebsocketSession();

        //client
        StompSession clientSession = new StompSessionBuilder()
                .buildWebSocketStompClient()
                .setMessageConverter(new MappingJackson2MessageConverter())
                .allocatePort(port)
                .buildWebsocketSession();

        List<String> hostMessages = new ArrayList<>();
        List<String> clientMessages = new ArrayList<>();

        //host and client will both subscribe this
        String subscribeUrl = "/topics/room/0";
        hostStompSession.subscribe(subscribeUrl, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return Object.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.printf("Host received %s%n", JSON.toJSONString(payload));
                messageCounts.set(0, messageCounts.get(0) + 1);
                hostMessages.add(JSON.toJSONString(payload));
            }
        });

        clientSession.subscribe(subscribeUrl, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return Object.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.printf("Client received %s%n", JSON.toJSONString(payload));
                messageCounts.set(1, messageCounts.get(1) + 1);
                clientMessages.add(JSON.toJSONString(payload));
            }
        });

        //假设每个阵营有五个人
        Map<String, List<PlayerStatus>> statuses = new HashMap<>();
        for (int i = 0; i < 2; ++i) {
            List<PlayerStatus> thisStatus = new ArrayList<>();
            for (int j = 0; j < 5; ++j)
                thisStatus.add(new PlayerStatus());
            statuses.put(i + "", thisStatus);
        }

        Runnable sendStatusTask = () -> {
            //host sends the status message
            System.out.println("send");
            hostStompSession.send("/app/statSync/0", statuses);
        };

        //一秒一次
        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(sendStatusTask, triggerUtil.createSecondLevelCronTrigger(0, sendInterval));

        while (true) {
            System.out.println(messageCounts.get(0) + " " + messageCounts.get(1));
            if (messageCounts.get(0) > 5 || messageCounts.get(1) > 5)
                break;
        }
        Assertions.assertNotNull(future);
        future.cancel(true);
    }
}
