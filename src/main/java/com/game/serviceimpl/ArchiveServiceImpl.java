package com.game.serviceimpl;

import com.game.dao.ArchiveDao;
import com.game.dao.UserDao;
import com.game.entity.Archive;
import com.game.service.ArchiveService;
import com.game.utils.jwtUtils.JwtUtil;
import com.game.utils.sessionUtils.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ArchiveServiceImpl implements ArchiveService {

    @Autowired
    private ArchiveDao archiveDao;

    @Autowired
    private UserDao userDao;

    @Override
    public List<Archive> findAll() {
        return archiveDao.findAll();
    }

    @Override
    @Transactional
    public void saveArchive(String data) {
        Integer userId = JwtUtil.getUserId();
        String archiveId = userDao.getArchiveId(userId);
        archiveId = archiveDao.saveArchive(archiveId, data);
        userDao.updateArchiveId(userId, archiveId);
    }

    @Override
    public Archive getArchive(int userId) {
        String archiveId = userDao.getArchiveId(userId);
        System.out.println("archiveId = " + archiveId);
        return archiveDao.findOne(archiveId);
    }
}
