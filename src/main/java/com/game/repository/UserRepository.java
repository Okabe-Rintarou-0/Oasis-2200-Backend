package com.game.repository;

import com.game.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "select archiveId from User where userId = ?1")
    String getArchiveId(Integer userId);

    @Modifying
    @Query(value = "update User set archiveId = ?2 where userId = ?1")
    void setArchiveId(Integer userId, String newId);

    @Query(value = "from User where userId = ?1")
    User findOne(Integer userId);
}
