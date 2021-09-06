package com.game.controller;

import com.game.annotation.SkipToken;
import com.game.constants.NetworkConstants;
import com.game.dto.RoomDto;
import com.game.dto.RoomFeatureDto;

import com.game.entity.Frame;
import com.game.service.RoomCacheService;

import com.game.utils.ipUtils.IpUtil;
import com.game.utils.jwtUtils.JwtUtil;
import com.game.utils.logUtils.LogUtil;
import com.game.utils.messageUtils.Message;
import com.game.utils.testUtils.ClusterTestUtil;
import com.game.utils.triggerUtils.TriggerUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.web.bind.annotation.*;
import com.game.entity.User;
import com.game.utils.messageUtils.MessageUtil;
import com.game.utils.redisUtils.RedisMessageManager;
import com.game.utils.testUtils.StompSessionBuilder;
import net.sf.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;


import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeoutException;

import java.net.InetAddress;
import java.net.NetworkInterface;

@Api(tags = "测试模块")
@RestController
@RequestMapping("/test")
public class TestController {
    private static String getMACAddress(InetAddress ia) throws Exception {
        // 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        // 下面代码是把mac地址拼装成String
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                sb.append("-");
            }
            // mac[i] & 0xFF 是为了把byte转化为正整数
            String s = Integer.toHexString(mac[i] & 0xFF);
            // System.out.println("--------------");
            // System.err.println(s);
            sb.append(s.length() == 1 ? 0 + s : s);
        }
        // 把字符串所有小写字母改为大写成为正规的mac地址并返回
        return sb.toString().toUpperCase();
    }

    @RequestMapping(value = "/serverTest", method = RequestMethod.GET)
    public String getServerName() throws Exception {
        InetAddress ia = null;
        String a = "777";
        ia = InetAddress.getLocalHost();
        String localname = ia.getHostName();
        String localip = ia.getHostAddress();
        InetAddress ia1 = InetAddress.getLocalHost();// 获取本地IP对象
        a += "本机名称是：" + localname + '\n';
        a += "本机的ip是 ：" + localip + '\n';
//        a += "本机的MAC是 ：" + getMACAddress(ia1) + '\n';
        return a;
    }

    @RequestMapping(value = "/ip", method = RequestMethod.GET)
    public String getIntraNet() throws SocketException {
        return IpUtil.getIntranetIp();
    }

    @Autowired
    RedisMessageManager redisMessageManager;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    private StompSession testSession = null;

    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    @Cacheable(value = "userKey")
    public User getUser() {
        User user = new User(0, "123", "123");
        System.out.println("若下面没出现“无缓存的时候调用”字样且能打印出数据表示测试成功");
        return user;
    }

    @RequestMapping(value = "/uid", method = RequestMethod.GET)
    String uid(HttpSession session) {
        UUID uid = (UUID) session.getAttribute("uid");
        if (uid == null) {
            uid = UUID.randomUUID();
        }
        session.setAttribute("uid", uid);
        return session.getId();
    }

    @RequestMapping(value = "/sendRedis", method = RequestMethod.GET)
    void sendMessage() {
        redisMessageManager.sendObject("room", MessageUtil.createMessage(MessageUtil.STAT_OK, "Hello from Redis.", JSONObject.fromObject(new User())));
    }

    @Autowired
    String intraNetIp;

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

    @Autowired
    RoomCacheService roomCacheService;

    @Autowired
    ClusterTestUtil clusterTestUtil;

    @RequestMapping(value = "/getRoomFeatures", method = RequestMethod.GET)
    public Map<String, Map<String, RoomFeatureDto>> getRoomFeatures() {
        clusterTestUtil.logWhoImAndWhatIHaveCalled("getRoomFeatures");
        return roomCacheService.getRoomFeatures();
    }

    @RequestMapping(value = "/createRoom", method = RequestMethod.GET)
    public void createRoom(@RequestParam Integer userId) {
        clusterTestUtil.logWhoImAndWhatIHaveCalled("createRoom");
        roomCacheService.createRoom(userId);
    }

    @RequestMapping(value = "/joinRoom", method = RequestMethod.GET)
    public void joinRoom(@RequestParam Integer userId) {
        clusterTestUtil.logWhoImAndWhatIHaveCalled("joinRoom");
        roomCacheService.joinRoom(userId);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public Map<String, Integer> getUserStates() {
        clusterTestUtil.logWhoImAndWhatIHaveCalled("getUserStates");
        return roomCacheService.getUserStates();
    }

    @RequestMapping(value = "/removeRoom", method = RequestMethod.GET)
    public void removeRoom(@RequestParam Integer roomId) {
        clusterTestUtil.logWhoImAndWhatIHaveCalled("removeRoom");
        roomCacheService.removeRoom(roomId);
    }

    @RequestMapping(value = "/clear", method = RequestMethod.GET)
    public void clearRoomContext() {
        clusterTestUtil.logWhoImAndWhatIHaveCalled("clearRoomContext");
        roomCacheService.clearRoomContext();
    }

    @Autowired
    LoginController loginController;

    @Autowired
    RegisterController registerController;

    @Autowired
    UserController userController;

    private final List<StompSession> stompSessionList = new ArrayList<>();

    private int getIdentity(String sessionId) {
        if (sessionId == null) return -1;
        int sum = 0;
        for (int i = 0; i < sessionId.length(); ++i) {
            sum += sessionId.charAt(i);
        }
        return sum % 2;
    }

    @ApiOperation("房间压力测试")
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

    private List<StompSession> sessions = new ArrayList<>();

    @SkipToken
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

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    Map<String, Frame> data = new HashMap<>();

    @PostConstruct
    private void init() {
        data.put("0", new Frame());
        data.put("1", new Frame());
    }

    @Autowired
    ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    TriggerUtil triggerUtil;

    Map<Integer, ScheduledFuture<?>> scheduledFutures = new HashMap<>();

    private void addScheduledTask(int roomId, Runnable task, int microSecs) {
        scheduledFutures.put(roomId,
                threadPoolTaskScheduler.schedule(task, triggerUtil.createMicroSecLevelTrigger(microSecs)));
    }

    @SkipToken
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
    @RequestMapping(value = "/greet", method = RequestMethod.GET)
    public String greet(@RequestParam String name) {
        System.out.println("receive greet from " + name);
        return "Hello " + name + "!";
    }
}
