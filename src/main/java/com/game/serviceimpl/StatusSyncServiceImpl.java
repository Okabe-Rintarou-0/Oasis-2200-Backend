package com.game.serviceimpl;

import com.game.constants.NetworkConstants;
import com.game.context.StatusSyncContext;
import com.game.entity.PlayerStatus;
import com.game.service.StatusSyncService;
import com.game.utils.messageUtils.MessageUtil;
import com.game.utils.messageUtils.StompMessage;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StatusSyncServiceImpl implements StatusSyncService {

    @Autowired
    private StatusSyncContext statusSyncContext;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void statusSync(Integer roomId, Map<String, List<PlayerStatus>> status) {
        if (roomId == null || roomId < 0) {
            return;
        }
        StompMessage stompMessage = MessageUtil.createStompMessage("stat", "stat", JSONObject.fromObject(status));
        String dest = String.format(NetworkConstants.TOPIC_ROOM_FORMAT, roomId.toString()); //"/topics/rooms/%s"
        simpMessagingTemplate.convertAndSend(dest, stompMessage);
    }
}
