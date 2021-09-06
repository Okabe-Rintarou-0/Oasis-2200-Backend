package com.game.repository;

import com.game.entity.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Integer> {

    @Query(value = "from UserAuthority where username = ?1 and password = ?2")
    UserAuthority findOne(String username, String password);

    @Query(value = "from UserAuthority where username = ?1")
    UserAuthority findByUsername(String username);
}
