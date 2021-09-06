package com.game.dao;

import com.game.entity.Archive;

import java.util.List;
import java.util.Optional;

public interface ArchiveDao {
    List<Archive> findAll();

    Archive findOne(String id);

    String saveArchive(String id, String data);
}
