package com.game.controller;

import com.game.entity.Archive;
import com.game.service.ArchiveService;
import com.game.utils.jwtUtils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "存档模块")
@RequestMapping("/archive")
@RestController
public class ArchiveController {

    @Autowired
    private ArchiveService archiveService;

    @ApiOperation(value = "获取所有存档", notes = "获取所有存档信息")
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public List<Archive> getAllArchives() {
        return archiveService.findAll();
    }

    @ApiOperation(value = "保存存档", notes = "保存请求用户的存档")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void saveArchive(@RequestBody String data) {
        archiveService.saveArchive(data);
    }

    @ApiOperation(value = "获取存档", notes = "获取请求用户的存档")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Archive getArchive() {
        return archiveService.getArchive(JwtUtil.getUserId());
    }
}
