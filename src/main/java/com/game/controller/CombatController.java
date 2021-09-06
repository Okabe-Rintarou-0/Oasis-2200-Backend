package com.game.controller;

import com.game.entity.CharacterInfo;
import com.game.service.CombatCacheService;
import com.game.service.CombatService;
import com.game.service.FrameSyncService;
import com.game.utils.jwtUtils.JwtUtil;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "战斗模块")
@RestController
public class CombatController {

    @Autowired
    private CombatService combatService;

    @Autowired
    private CombatCacheService combatCacheService;

    @Value("${cluster.strategy}")
    String strategy;

    @RequestMapping(value = "/accept", method = RequestMethod.GET) //房主同意当前房间对战开始
    public Message acceptCombat() {
        boolean result;
        result = combatCacheService.acceptCombat(JwtUtil.getUserId());
        if (result) {
            return MessageUtil.createMessage(MessageUtil.STAT_OK, "接收成功");
        }
        return MessageUtil.createMessage(MessageUtil.STAT_INVALID, "接受失败");
    }

    @RequestMapping(value = "/deny", method = RequestMethod.GET)
    public Message denyCombat() {
        boolean result;
        result = combatCacheService.denyCombat(JwtUtil.getUserId());
//        result = combatService.denyCombat();
        if (result) {
            return MessageUtil.createMessage(MessageUtil.STAT_OK, "拒绝成功");
        }
        return MessageUtil.createMessage(MessageUtil.STAT_INVALID, "拒绝失败");
    }

    @RequestMapping(value = "/uploadPlayerInfo", method = RequestMethod.POST) //进行多人匹配时，upload自己的信息
    public Message uploadPlayerInfo(@RequestBody List<CharacterInfo> infos) {
        boolean result;
        result = combatCacheService.uploadPlayerInfo(JwtUtil.getUserId(), infos);
        if (result) {
            return MessageUtil.createMessage(MessageUtil.STAT_OK, "上传成功");
        }
        return MessageUtil.createMessage(MessageUtil.STAT_INVALID, "上传失败");
    }

    @RequestMapping(value = "/end", method = RequestMethod.GET)
    public void endCombat(@RequestParam(value = "winner") Integer winner) {
        combatCacheService.endCombat(JwtUtil.getUserId(), winner);
    }
}
