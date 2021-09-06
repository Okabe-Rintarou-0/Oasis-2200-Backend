package com.game.daoimpl;

import com.game.dao.ArchiveDao;
import com.game.entity.Archive;
import com.game.repository.ArchiveRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class ArchiveDaoImpl implements ArchiveDao {

    @Autowired
    private ArchiveRepository archiveRepository;

    @Override
    public List<Archive> findAll() {
        return archiveRepository.findAll();
    }

    @Override
    public Archive findOne(String id) {
        return archiveRepository.findArchiveById(id);
    }

    @Override
    public String saveArchive(String id, String data) {
        Archive archive = new Archive(id, Document.parse(data), new Date());
        archive = archiveRepository.save(archive);
        return archive.getId();
    }
}
