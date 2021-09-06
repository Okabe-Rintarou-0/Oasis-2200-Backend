package com.game.utils.testUtils;

import com.game.utils.logUtils.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lzh
 * @Title: 测试用
 * @Package
 * @Description: For test for clusters
 * @date 2021/8/24 15:56
 */
@Component
public class ClusterTestUtil {
    @Autowired
    String intraNetIp;

    public void logWhoImAndWhatIHaveCalled(String funcName) {
        String msg = String.format("My ip is %s, I've just called %s", intraNetIp, funcName);
        LogUtil.info(msg);
    }

    //打印ws信息
    public void logForWsConnection() {
        String msg = String.format("A ws has just connected to ip: %s", intraNetIp);
        LogUtil.info(msg);
    }
}
