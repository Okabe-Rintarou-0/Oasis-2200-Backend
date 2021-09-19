package com.game.controller;

import com.game.entity.CharacterInfo;
import com.game.service.CombatCacheService;
import com.game.service.CombatService;
import com.game.service.FrameSyncService;
import com.game.utils.jwtUtils.JwtUtil;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "战斗模块")
@RequestMapping("/combat")
@RestController
public class CombatController {

    @Autowired
    private CombatService combatService;

    @Autowired
    private CombatCacheService combatCacheService;

    @Value("${cluster.strategy}")
    String strategy;

    @ApiOperation(value = "接受战斗", notes = "房主接受战斗")
    @RequestMapping(value = "/accept", method = RequestMethod.GET) //房主同意当前房间对战开始
    public Message acceptCombat() {
        boolean result;
        result = combatCacheService.acceptCombat(JwtUtil.getUserId());
        if (result) {
            return MessageUtil.createMessage(MessageUtil.STAT_OK, "接收成功");
        }
        return MessageUtil.createMessage(MessageUtil.STAT_INVALID, "接受失败");
    }

    @ApiOperation(value = "拒绝战斗", notes = "房主拒绝战斗")
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

    @ApiOperation(value = "上传玩家信息", notes = "战斗前上传玩家信息")
    @RequestMapping(value = "/uploadPlayerInfo", method = RequestMethod.POST) //进行多人匹配时，upload自己的信息
    public Message uploadPlayerInfo(@RequestBody List<CharacterInfo> infos) {
        boolean result;
        result = combatCacheService.uploadPlayerInfo(JwtUtil.getUserId(), infos);
        if (result) {
            return MessageUtil.createMessage(MessageUtil.STAT_OK, "上传成功");
        }
        return MessageUtil.createMessage(MessageUtil.STAT_INVALID, "上传失败");
    }

    @ApiOperation(value = "结束战斗", notes = "房主发起请求结束战斗")
    @ApiImplicitParam(name = "winner", value = "战斗获胜者", paramType = "query", required = true, dataType = "Integer")
    @RequestMapping(value = "/end", method = RequestMethod.GET)
    public void endCombat(@RequestParam(value = "winner") Integer winner) {
        combatCacheService.endCombat(JwtUtil.getUserId(), winner);
    }
}
