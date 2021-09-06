package com.game.context;

import com.alibaba.fastjson.JSON;
import com.game.context.FrameSyncContext;
import com.game.entity.Frame;
import com.game.entity.Operation;
import net.sf.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description: Test Frame Synchronization Context
 * @date 2021/8/13 11:01
 */
@SpringBootTest
public class FrameSyncContextUnitTest {
    @Autowired
    private FrameSyncContext frameSyncContext;

    @Test
    @DisplayName("测试添加帧数据和查看帧数据")
    public void testAddFrame() {
        //user with gid 0 add a new frame to room 0.
        frameSyncContext.addFrame(0, 0, new Frame());
        JSONObject thisFrame = frameSyncContext.getFrameDataOfThisFrame(0);
        System.out.println(thisFrame.toString());
        Assertions.assertNotNull(thisFrame.get("0"));
        //test frame coverage
        Frame anotherFrame = new Frame();
        Operation operation = new Operation("lzh", 0.0, 0.0, true, true, 0.0);
        List<Operation> operations = new ArrayList<>();
        operations.add(operation);
        anotherFrame.getOperations().add(operation);
        frameSyncContext.addFrame(0, 0, anotherFrame);
        JSONObject frameData = JSONObject.fromObject(frameSyncContext.getFrameDataOfThisFrame(0).get("0"));
        System.out.println(frameData.get("operations"));
        Assertions.assertEquals(JSON.toJSONString(frameData.get("operations")), JSON.toJSONString(operations));

        frameSyncContext.addFrame(0, 1, new Frame());
        thisFrame = frameSyncContext.getFrameDataOfThisFrame(0);
        Assertions.assertNotNull(thisFrame.get("0"));
        Assertions.assertNotNull(thisFrame.get("1"));
    }

    @Test
    @DisplayName("测试清空帧数据")
    public void testClearFrame() {
        frameSyncContext.addFrame(0, 0, new Frame());
        frameSyncContext.addFrame(0, 1, new Frame());
        JSONObject thisFrame = frameSyncContext.getFrameDataOfThisFrame(0);

        Assertions.assertNotNull(thisFrame.get("0"));
        Assertions.assertNotNull(thisFrame.get("1"));

        frameSyncContext.clearFrame(0);
        thisFrame = frameSyncContext.getFrameDataOfThisFrame(0);
        Assertions.assertNull(thisFrame.get("0"));
        Assertions.assertNull(thisFrame.get("1"));

        //一些边界情况
        frameSyncContext.clearFrame(-1);
        frameSyncContext.clearFrame(101);
        frameSyncContext.clearFrame(99);
    }

    @Test
    @DisplayName("测试创建和删除帧同步定时器")
    public void testAddRemoveFrameSyncScheduler() {
        boolean result = frameSyncContext.addFrameSyncScheduler(0, () -> {
        });
        Assertions.assertTrue(result);

        result = frameSyncContext.addFrameSyncScheduler(0, () -> {
        });
        Assertions.assertFalse(result);

        result = frameSyncContext.removeFrameSyncScheduler(0);
        Assertions.assertTrue(result);

        result = frameSyncContext.removeFrameSyncScheduler(0);
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("测试到达下一帧")
    public void testToNextFrame() {
        //为房间0添加第零帧。
        frameSyncContext.getFrames().put(0, 0L);
        Assertions.assertEquals(frameSyncContext.getFrames().get(0), 0);

        for (long i = 0L; i < 10000; ++i) {
            frameSyncContext.toNextFrame(0);
            Assertions.assertEquals(frameSyncContext.getFrames().get(0), i + 1);
        }

        //一些边界情况
        frameSyncContext.toNextFrame(-1);
        frameSyncContext.toNextFrame(101);
        frameSyncContext.toNextFrame(2);

        long boundary = Long.MAX_VALUE - 1;
        frameSyncContext.getFrames().put(1, boundary);
        Assertions.assertEquals(frameSyncContext.getFrames().get(1), boundary);
        frameSyncContext.toNextFrame(1);
        Assertions.assertEquals(frameSyncContext.getFrames().get(1), 0);
        for (long i = 0L; i < 10000; ++i) {
            frameSyncContext.toNextFrame(1);
            Assertions.assertEquals(frameSyncContext.getFrames().get(1), i + 1);
        }
    }
}
