package com.game.controller;

import com.game.entity.Frame;
import com.game.service.FrameSyncService;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "帧同步模块")
@RequestMapping("/frameSync")
@RestController
public class FrameSyncController {

    @Autowired
    FrameSyncService frameSyncService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @ApiOperation(value = "开启帧同步", notes = "开启帧同步")
    @RequestMapping(value = "/start", method = RequestMethod.GET) //just for test
    public Message startSync(@RequestParam Integer roomId) {
        if (frameSyncService.startSync(roomId)) {
            return MessageUtil.createMessage(MessageUtil.STAT_OK, "开启成功!");
        } else {
            return MessageUtil.createMessage(MessageUtil.STAT_INVALID, "开启失败!");
        }
    }

    @ApiOperation(value = "停止帧同步", notes = "停止帧同步")
    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    public Message stopSync(@RequestParam Integer roomId) {
        if (frameSyncService.stopSync(roomId)) {
            return MessageUtil.createMessage(MessageUtil.STAT_OK, "关闭成功!");
        } else {
            return MessageUtil.createMessage(MessageUtil.STAT_INVALID, "关闭失败！");
        }
    }

    @MessageMapping("/uploadFrame/{roomId}/{gameId}")
    public void uploadFrameData(Frame frame, @DestinationVariable(value = "roomId") String roomId,
                                @DestinationVariable(value = "gameId") String gameId) {
        //gameId是每个玩家在后端的唯一标识
        frameSyncService.addFrame(Integer.parseInt(roomId), Integer.parseInt(gameId), frame);
    }

}
