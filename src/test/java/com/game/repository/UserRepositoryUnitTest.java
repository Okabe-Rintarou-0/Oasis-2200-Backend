package com.game.repository;

import com.game.entity.User;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@DataJpaTest //使用JPA，还有类似的@MybatisTest
@Rollback // 是否回滚，默认是true
// 下面这句annotation的意思是使用真正的数据库源而不是使用虚拟数据库。
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryUnitTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("测试存储用户并获取其昵称")
    public void testSaveAndGetNickname() {
        ConcurrentHashMap<Integer, String> testMap = new ConcurrentHashMap<>();

        //多线程测试
        new Thread(() -> {
            for (int i = 0; i < 500; ++i) {
                String randomNickname = RandomStringUtils.randomAlphanumeric(15);
                System.out.println("randomNickname = " + randomNickname);
                userRepository.save(new User(i, null, randomNickname));
                testMap.put(i, randomNickname);
            }
        }).start();
        for (int i = 501; i < 1000; ++i) {
            String randomNickname = RandomStringUtils.randomAlphanumeric(15);
            System.out.println("randomNickname = " + randomNickname);
            userRepository.save(new User(i, null, randomNickname));
            testMap.put(i, randomNickname);
        }

        //Assertion
        for (Map.Entry<Integer, String> entry : testMap.entrySet()) {
            System.out.println("get from db: " + userRepository.findOne(entry.getKey()).getNickname());
            System.out.println("while the map says: " + entry.getValue());
            Assertions.assertEquals(userRepository.findOne(entry.getKey()).getNickname(), entry.getValue());
        }
    }

    @Test
    @DisplayName("测试修改用户archiveId")
    public void testUpdateArchiveId() {
        //测试的准备工作，一组是null的archiveId，一组是不为null的archiveId;
        ConcurrentHashMap<Integer, String> testMap = new ConcurrentHashMap<>(); //用于存储archiveId的。

        for (int tid = 1; tid <= 5; ++tid) {
            for (int i = tid * 100; i < (tid + 1) * 100; ++i) {
                userRepository.saveAndFlush(new User(i, null, "123"));
            }
        }
        for (int tid = 6; tid <= 10; ++tid) {
            for (int i = tid * 100; i < (tid + 1) * 100; ++i) {
                String randomArchiveId = RandomStringUtils.randomAlphanumeric(15);
                userRepository.saveAndFlush(new User(i, randomArchiveId, "123"));
                testMap.put(i, randomArchiveId);
            }
        }

        //Assertions
        for (int i = 100; i < 600; ++i) { //前500个的archiveId都是空。
            Assertions.assertNull(userRepository.getArchiveId(i)); //判断插入是否正确

            String newRandomArchiveId = RandomStringUtils.randomAlphanumeric(15); //设置新的archiveId
            userRepository.setArchiveId(i, newRandomArchiveId);

            Assertions.assertEquals(userRepository.getArchiveId(i), newRandomArchiveId); //判断修改是否成功
        }

        for (int i = 600; i < 1000; ++i) {
            Assertions.assertEquals(userRepository.getArchiveId(i), testMap.get(i)); //判断插入是否正确

            String newRandomArchiveId = RandomStringUtils.randomAlphanumeric(15); //设置新的archiveId
            userRepository.setArchiveId(i, newRandomArchiveId);

            Assertions.assertEquals(userRepository.getArchiveId(i), newRandomArchiveId); //判断修改是否成功
        }
    }
}
