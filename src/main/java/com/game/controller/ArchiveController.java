package com.game.controller;

import com.game.entity.Archive;
import com.game.service.ArchiveService;
import com.game.utils.jwtUtils.JwtUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "存档模块")
@RestController
public class ArchiveController {

    @Autowired
    private ArchiveService archiveService;

    @RequestMapping(value = "/allArchives", method = RequestMethod.GET)
    public List<Archive> getAllArchives() {
        return archiveService.findAll();
    }

    @RequestMapping(value = "/saveArchive", method = RequestMethod.POST)
    public void saveArchive(@RequestBody String data) {
        archiveService.saveArchive(data);
    }

    @RequestMapping(value = "/archive", method = RequestMethod.GET)
    public Archive getArchive() {
        return archiveService.getArchive(JwtUtil.getUserId());
    }
}
