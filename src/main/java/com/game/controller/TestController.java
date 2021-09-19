package com.game.controller;

import com.game.annotation.SkipToken;
import com.game.constants.NetworkConstants;
import com.game.dao.CombatCacheDao;
import com.game.dto.RoomDto;
import com.game.dto.RoomFeatureDto;
import com.game.entity.CharacterInfo;
import com.game.entity.Frame;
import com.game.entity.PlayerStatus;
import com.game.service.CombatCacheService;
import com.game.service.RoomCacheService;
import com.game.utils.ipUtils.IpUtil;
import com.game.utils.jwtUtils.JwtUtil;
import com.game.utils.logUtils.LogUtil;
import com.game.utils.messageUtils.Message;
import com.game.utils.redisUtils.RedisMessageManager;
import com.game.utils.testUtils.ClusterTestUtil;
import com.game.utils.testUtils.StompSessionBuilder;
import com.game.utils.triggerUtils.TriggerUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeoutException;

@Api(tags = "测试模块")
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    RedisMessageManager redisMessageManager;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    String intraNetIp;

    @Autowired
    CombatCacheService combatCacheService;

    @Autowired
    RoomCacheService roomCacheService;

    @Autowired
    ClusterTestUtil clusterTestUtil;

    @Autowired
    LoginController loginController;

    @Autowired
    RegisterController registerController;

    @Autowired
    UserController userController;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    TriggerUtil triggerUtil;

    private final Map<Integer, ScheduledFuture<?>> scheduledFutures = new HashMap<>();

    private final List<StompSession> stompSessionList = new ArrayList<>();

    private final List<StompSession> sessions = new ArrayList<>();

    private final Map<String, Frame> data = new HashMap<>();

    private StompSession testSession = null;

    @PostConstruct
    private void init() {
        data.put("0", new Frame());
        data.put("1", new Frame());
    }

    @ApiOperation(value = "获取访问的服务器的内网ip", notes = "获取访问的服务器的内网ip")
    @RequestMapping(value = "/ip", method = RequestMethod.GET)
    public String getIntraNet() throws SocketException {
        return IpUtil.getIntranetIp();
    }

    @ApiOperation(value = "测试聊天", notes = "测试聊天")
    @ApiImplicitParam(name = "testMessage", value = "聊天信息", required = true, paramType = "query")
    @RequestMapping(value = "/testChat", method = RequestMethod.GET)
    public String testChat(@RequestParam(value = "testMessage") String message) throws InterruptedException, ExecutionException, TimeoutException {
        LogUtil.print(String.format("chat with message: %s%n", message));
        if (testSession == null) {
            testSession = new StompSessionBuilder()
                    .buildWebSocketStompClient()
                    .setMessageConverter(new StringMessageConverter()) //for string
                    .allocatePort(8080)
                    .buildWebsocketSession();
            testSession.subscribe("/topics/chat", new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders stompHeaders) {
                    return String.class;
                }

                @Override
                public void handleFrame(StompHeaders stompHeaders, Object o) {
                    LogUtil.print(String.format("Receive broadcast: %s%n, I'm %s", o.toString(), intraNetIp));
                }
            });
        }
        testSession.send("/app/chatroom", message);
        return "send testMessage";
    }

    @ApiOperation(value = "获取所有房间信息", notes = "获取所有房间信息(基于redis)")
    @RequestMapping(value = "/getRoomFeatures", method = RequestMethod.GET)
    public Map<String, Map<String, RoomFeatureDto>> getRoomFeatures() {
        clusterTestUtil.logWhoImAndWhatIHaveCalled("getRoomFeatures");
        return roomCacheService.getRoomFeatures();
    }

    @ApiOperation(value = "获取所有用户状态", notes = "获取所有房间状态(用户在哪个房间？)")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public Map<String, Integer> getUserStates() {
        clusterTestUtil.logWhoImAndWhatIHaveCalled("getUserStates");
        return roomCacheService.getUserStates();
    }

    @ApiOperation(value = "删除指定房间", notes = "删除指定房间(基于redis)")
    @ApiImplicitParam(name = "roomId", value = "聊天信息", required = true, paramType = "query", dataType = "Integer")
    @RequestMapping(value = "/removeRoom", method = RequestMethod.GET)
    public void removeRoom(@RequestParam Integer roomId) {
        clusterTestUtil.logWhoImAndWhatIHaveCalled("removeRoom");
        roomCacheService.removeRoom(roomId);
    }

    @ApiOperation(value = "清空房间", notes = "清空房间(基于redis)")
    @RequestMapping(value = "/clearRoomContext", method = RequestMethod.GET)
    public void clearRoomContext() {
        clusterTestUtil.logWhoImAndWhatIHaveCalled("clearRoomContext");
        roomCacheService.clearRoomContext();
    }

    @ApiOperation(value = "房间压力测试", notes = "随机加入或创建房间，加入失败会再尝试一次")
    @RequestMapping(value = "/room", method = RequestMethod.GET)
    public String testRoom(@RequestParam(value = "testId") Integer testId) {
        int identity = testId % 2;
        Integer userId = JwtUtil.getUserId();
        System.out.println("userId = " + userId);
        int roomId = -1;
        if (identity == 1) { //is host
            System.out.println("im host");
            Message message = roomCacheService.createRoom(userId);
            if (message.status > 0) {
                roomId = (Integer) message.data.get("roomId");
            }
        } else {
            System.out.println("im client");
            RoomDto roomDto = roomCacheService.joinRoom(userId);
            if (roomDto == null) { //再加入一次
                System.out.println("Join again");
                roomDto = roomCacheService.joinRoom(userId);
            }
            if (roomDto != null) {
                roomId = roomDto.getRoomId();
                System.out.println("加入成功！");
            }
        }
        if (roomId < 0) return "No room";
        return identity == 1 ? "I am host" : "I am client";
    }

    @SkipToken
    @ApiOperation(value = "帧同步压力测试", notes = "创建若干ws连接并订阅房间topics，开启帧同步")
    @RequestMapping(value = "/connectAndSub", method = RequestMethod.GET)
    public void testWsConnectAndSub(@RequestParam Integer id) throws InterruptedException, ExecutionException, TimeoutException {
        testSession = new StompSessionBuilder()
                .buildWebSocketStompClient()
                .setMessageConverter(new MappingJackson2MessageConverter()) //for string
                .allocatePort(8080)
                .buildWebsocketSession();
        String subUrl = String.format(NetworkConstants.TOPIC_ROOM_FORMAT, (id / 2) + "");
        testSession.subscribe(subUrl, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return Object.class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, Object o) {
                LogUtil.print(String.format("Receive broadcast: %s, I'm %s", o.toString(), id));
            }
        });
        sessions.add(testSession);
        int roomId = id / 2;
        if (scheduledFutures.containsKey(roomId)) return;
        addScheduledTask(roomId, () -> {
            simpMessagingTemplate.convertAndSend(subUrl, data);
        }, 66);
    }

    private void addScheduledTask(int roomId, Runnable task, int microSecs) {
        scheduledFutures.put(roomId,
                threadPoolTaskScheduler.schedule(task, triggerUtil.createMicroSecLevelTrigger(microSecs)));
    }

    @SkipToken
    @ApiOperation(value = "清除所有帧同步定时任务", notes = "清除所有帧同步定时任务")
    @RequestMapping(value = "/clearAll", method = RequestMethod.GET)
    public void clearAll() {
        for (Map.Entry<Integer, ScheduledFuture<?>> scheduledFutureEntry : scheduledFutures.entrySet()) {
            ScheduledFuture<?> scheduledFuture = scheduledFutureEntry.getValue();
            scheduledFuture.cancel(true);
            while (!scheduledFuture.isCancelled()) {
                scheduledFuture.cancel(true);
            }
        }
        sessions.clear();
    }

    @SkipToken
    @ApiOperation(value = "打个招呼", notes = "打个招呼吧")
    @RequestMapping(value = "/greet", method = RequestMethod.GET)
    public String greet(@RequestParam String name) {
        System.out.println("receive greet from " + name);
        return "Hello " + name + "!";
    }

    @SkipToken
    @ApiOperation(value = "获取用户上传的信息", notes = "即playerInfo")
    @GetMapping(value = "/playerInfo")
    public Map<String, List<CharacterInfo>> getPlayerInfo(@RequestParam Integer roomId) {
        return combatCacheService.getPlayerInfo(roomId);
    }
}
