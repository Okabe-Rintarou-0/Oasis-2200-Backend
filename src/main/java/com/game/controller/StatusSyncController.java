package com.game.controller;

import com.game.entity.PlayerStatus;
import com.game.service.StatusSyncService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(tags = "状态同步模块")
@RestController
public class StatusSyncController {

    @Autowired
    private StatusSyncService statusSyncService;

    @MessageMapping("/statSync/{roomId}")
    public void statusSync(Map<String, List<PlayerStatus>> status, @DestinationVariable(value = "roomId") String roomId) {
        //LogUtil.print("receive status: " + JSON.toJSONString(status));
        statusSyncService.statusSync(Integer.parseInt(roomId), status);
    }
}

