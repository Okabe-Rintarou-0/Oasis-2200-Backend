package com.game;

import com.alibaba.fastjson.JSON;
import com.game.entity.Frame;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
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
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FrameSyncControllerTest {
    @LocalServerPort
    private Integer port;

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
    @DisplayName("测试帧同步（单个房间）")
    public void testFrameSynchronizationForSingleRoom() throws InterruptedException, ExecutionException, TimeoutException {
        //startSync
        //单房间并发测试
        //测试用的会话
        List<StompSession> stompSessions = new ArrayList<>();
        for (int i = 0; i < 2; ++i) {
            StompSession stompSession = new StompSessionBuilder()
                    .buildWebSocketStompClient()
                    .setMessageConverter(new MappingJackson2MessageConverter()) //for json
                    .allocatePort(port)
                    .buildWebsocketSession();

            stompSession.subscribe("/topics/room/0", new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return Object.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    System.out.println(JSON.toJSONString(payload));
                    stompMessages.get(0).add(JSON.toJSONString(payload));
                }
            });
            stompSessions.add(stompSession);
        }

        Message message = testRestTemplate.getForObject("/startSync?roomId=0", Message.class);
        Assertions.assertEquals(message.status, MessageUtil.STAT_OK);
        message = testRestTemplate.getForObject("/startSync?roomId=0", Message.class);
        Assertions.assertEquals(message.status, MessageUtil.STAT_INVALID);
        List<Runnable> sendFrameTasks = new ArrayList<>();
        for (int i = 0; i < 2; ++i) {
            int finalI = i;
            sendFrameTasks.add(() -> {
                stompSessions.get(finalI).send(String.format("/app/uploadFrame/0/%d", finalI), new Frame());
            });
        }
        List<ScheduledFuture<?>> scheduledFutures = new ArrayList<>();
        for (Runnable sendFrameTask : sendFrameTasks) {
            scheduledFutures.add(threadPoolTaskScheduler.schedule(sendFrameTask, triggerUtil.createMicroSecLevelTrigger(66)));
        }

        while (stompMessages.get(0).size() < 5) {

        }

        for (ScheduledFuture<?> future : scheduledFutures) {
            future.cancel(true);
        }

        message = testRestTemplate.getForObject("/stopSync?roomId=0", Message.class);
        Assertions.assertEquals(message.status, MessageUtil.STAT_OK);
        message = testRestTemplate.getForObject("/stopSync?roomId=0", Message.class);
        Assertions.assertEquals(message.status, MessageUtil.STAT_INVALID);
    }

    @Test
    @DisplayName("测试打开关闭帧同步接口(startSync)")
    public void testStartStopSync() {
        Message message = testRestTemplate.getForObject("/startSync?roomId=0", Message.class);
        Assertions.assertEquals(message.status, MessageUtil.STAT_OK);
        message = testRestTemplate.getForObject("/startSync?roomId=1", Message.class);
        Assertions.assertEquals(message.status, MessageUtil.STAT_OK);
        message = testRestTemplate.getForObject("/startSync?roomId=0", Message.class);
        Assertions.assertEquals(message.status, MessageUtil.STAT_INVALID);
        //can't stop sync for non-existent room
        message = testRestTemplate.getForObject("/stopSync?roomId=2", Message.class);
        Assertions.assertEquals(message.status, MessageUtil.STAT_INVALID);
        message = testRestTemplate.getForObject("/stopSync?roomId=0", Message.class);
        Assertions.assertEquals(message.status, MessageUtil.STAT_OK);
        message = testRestTemplate.getForObject("/stopSync?roomId=0", Message.class);
        Assertions.assertEquals(message.status, MessageUtil.STAT_INVALID);
        message = testRestTemplate.getForObject("/startSync?roomId=0", Message.class);
        Assertions.assertEquals(message.status, MessageUtil.STAT_OK);
    }
}