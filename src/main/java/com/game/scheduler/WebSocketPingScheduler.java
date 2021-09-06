package com.game.scheduler;


import com.game.entity.Frame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


/**
 * Created by xschen on 16/9/2017.
 * <p>
 * A simple scheduler that tries to ping the web client every 10 seconds via web-socket
 */

@Service
public class WebSocketPingScheduler {

//    @Autowired
//    private AuditEventService service;
    @Autowired
    private SimpMessagingTemplate brokerMessagingTemplate;

    public Frame frame = new Frame();

    public void addFrame(Frame f) {
        frame.updateFrame(f);
    }

//    @Scheduled(fixedDelay = 30L)
//    public void webSocketPing() {
//
//        brokerMessagingTemplate.convertAndSend("/topics/combat", JSON.toJSONString(frame, SerializerFeature.BrowserCompatible));
//        frame = new Frame();
//    }
}
