package com.game.repository;

import com.game.entity.Archive;
import org.springframework.data.mongodb.repository.MongoRepository;

import javax.transaction.Transactional;

@Transactional
public interface ArchiveRepository extends MongoRepository<Archive, String> {
    Archive findArchiveById(String id);
}
