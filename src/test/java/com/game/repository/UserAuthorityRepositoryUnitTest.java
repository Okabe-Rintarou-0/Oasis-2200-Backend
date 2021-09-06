package com.game.repository;

import com.game.entity.UserAuthority;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.HashMap;
import java.util.Map;

@DataJpaTest //使用JPA，还有类似的@MybatisTest
@Rollback // 是否回滚，默认是true
// 下面这句annotation的意思是使用真正的数据库源而不是使用虚拟数据库。
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserAuthorityRepositoryUnitTest {
    @Autowired
    private UserAuthorityRepository userAuthorityRepository;

    @Test
    @DisplayName("测试存储账号登录信息并判断是否登录信息正确")
    public void testSaveAndCheck() {
        Map<Integer, String> randomUsernames = new HashMap<>();
        Map<Integer, String> randomPasswords = new HashMap<>();

        //test save
        for (int i = 0; i < 500; ++i) {
            String randomUsername = RandomStringUtils.randomAlphanumeric(8);
            String randomPwd = RandomStringUtils.randomAlphanumeric(10);
            randomUsernames.put(i, randomUsername);
            randomPasswords.put(i, randomPwd);
            Assertions.assertEquals(
                    (userAuthorityRepository.save(new UserAuthority(randomUsername, randomPwd, "123@sjtu.edu.cn",0)).getUsername()),
                    randomUsername);
        }
        //test check
        for (int i = 0; i < 500; ++i) {
            Assertions.assertNotNull(userAuthorityRepository.findOne(
                    randomUsernames.get(i), randomPasswords.get(i)
            ));
            Assertions.assertNull(userAuthorityRepository.findOne(
                    randomUsernames.get(i), null));
            Assertions.assertNull(userAuthorityRepository.findOne(
                    randomUsernames.get(i), ""));
            Assertions.assertNull(userAuthorityRepository.findOne(
                    null, randomPasswords.get(i)));
            Assertions.assertNull(userAuthorityRepository.findOne(
                    "", randomPasswords.get(i)));
        }
        Assertions.assertNull(userAuthorityRepository.findOne(
                null, null));
        Assertions.assertNull(userAuthorityRepository.findOne(
                "", ""));
    }
}
