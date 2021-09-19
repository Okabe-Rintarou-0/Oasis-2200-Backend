package com.game.context;

import com.game.annotation.Context;
import com.game.entity.Frame;
import com.game.utils.triggerUtils.TriggerUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Context
public class FrameSyncContext {
    //帧同步内容
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private TriggerUtil triggerUtil;

    private int currentPoolSize = 20;

    private final static Long MAX_FRAME = Long.MAX_VALUE;

    //超过这个阈值则认定一方输掉比赛。
    private final static Long DISCONNECT_FRAME_DIFF_THRESH = 150L;

    //< 房间号，对应的定时任务 >
    private final ConcurrentHashMap<Integer, ScheduledFuture<?>> frameSyncSchedulers;
    //< 房间号，对应帧数据 >
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Frame>> frameData;
    //< 房间号，对应的当前帧数 >
    private final ConcurrentHashMap<Integer, Long> frames;

    //自己写的心跳包检测
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Long>> pingPong;

    public Long getCurrentFrame(int roomId) {
        return frames.get(roomId);
    }

    @Value("${combat.frameSyncInterval}")
    private Integer frameSyncInterval;

    //创建帧同步定时器
    public boolean addFrameSyncScheduler(int roomId, Runnable frameSyncTask) {
        if (frameSyncSchedulers.containsKey(roomId)) {
            return false; //若已存在，则无需添加
        }
        ScheduledFuture<?> future =
                threadPoolTaskScheduler.schedule(frameSyncTask, triggerUtil.createMicroSecLevelTrigger(frameSyncInterval));
        if (future == null) return false;
        frameSyncSchedulers.put(roomId, future);     //添加一个新的定时器
        if (frameSyncSchedulers.size() >= currentPoolSize) {
            currentPoolSize = currentPoolSize * 2;
            threadPoolTaskScheduler.setPoolSize(currentPoolSize);
        }
        //动态扩大线程池
        frames.put(roomId, 0L);     //设置为第0帧
        frameData.put(roomId, new ConcurrentHashMap<>());
        ConcurrentHashMap<Integer, Long> pingPongOfNewRoom = new ConcurrentHashMap<>();
        pingPongOfNewRoom.put(0, -1L);
        pingPongOfNewRoom.put(1, -1L);
        pingPong.put(roomId, pingPongOfNewRoom);

        return true;
    }

    //移除帧同步定时器
    public boolean removeFrameSyncScheduler(int roomId) {
        ScheduledFuture<?> future = frameSyncSchedulers.get(roomId);
        if (future == null) {
            return false;
        }
        future.cancel(true);
        while (!future.isCancelled()) {
            future.cancel(true);
        }
        frameSyncSchedulers.remove(roomId);
        return true;
    }

    public void removePingPong(int roomId) {
        pingPong.remove(roomId);
        frames.remove(roomId);
    }

    public void toNextFrame(int roomId) {
        if (frames.containsKey(roomId)) {
            Long currentFrame = frames.get(roomId);
            frames.put(roomId, (currentFrame + 1) % MAX_FRAME);
        }
    }

    //房间号为roomId的gameId玩家，上传了一个新网络包的情况
    public void addFrame(int roomId, int gameId, Frame frame) {
        ConcurrentHashMap<String, Frame> thisFrameData = frameData.get(roomId);
        String gameIdStr = "" + gameId;
        if (thisFrameData.containsKey(gameIdStr)) {
            thisFrameData.get(gameIdStr).updateFrame(frame);
        } else {
            thisFrameData.put(gameIdStr, frame);
        }
        ConcurrentHashMap<Integer, Long> pingPongOfThisRoom = pingPong.get(roomId);
        if (pingPongOfThisRoom != null)
            pingPongOfThisRoom.put(gameId, getCurrentFrame(roomId));
    }

    public void clearFrame(int roomId) {
        if (frameData.containsKey(roomId)) {
            frameData.get(roomId).clear();
        }
    }

    public JSONObject getFrameDataOfThisFrame(Integer roomId) {
        return JSONObject.fromObject(frameData.get(roomId));
    }

    public FrameSyncContext() {
        frameSyncSchedulers = new ConcurrentHashMap<>();
        //< roomId, FrameSyncScheduler >
        frameData = new ConcurrentHashMap<>();
        frames = new ConcurrentHashMap<>();
        pingPong = new ConcurrentHashMap<>();
    }

    public int checkDisconnection(int roomId) {
        if (!pingPong.containsKey(roomId)) return -1;
        Long currentFrame = getCurrentFrame(roomId);

        for (int i = 0; i < 2; ++i) {
            Long thisUserFrame = pingPong.get(roomId).get(i);
            if (currentFrame - thisUserFrame >= DISCONNECT_FRAME_DIFF_THRESH)
                return i;
        }
        return -1;
    }

    //this interface is only for test
    //please ignore and don't use it if you are not testing!
    public ConcurrentHashMap<Integer, Long> getFrames() {
        return frames;
    }

    public boolean combatStarted(int roomId) {
        return frameSyncSchedulers.containsKey(roomId);
    }
}
