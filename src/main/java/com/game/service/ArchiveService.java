package com.game.service;

import com.game.entity.Archive;

import java.util.List;

public interface ArchiveService {
    List<Archive> findAll();

    void saveArchive(String data);

    Archive getArchive(int userId);
}
