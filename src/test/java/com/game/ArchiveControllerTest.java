package com.game;

import com.game.entity.Archive;
import com.game.utils.testUtils.TokenTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.LinkedMultiValueMap;

import javax.transaction.Transactional;
import java.util.Date;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/9/4 16:20
 */
@Rollback
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ArchiveControllerTest {
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    TokenTestUtil tokenTestUtil;

    @Test
    @DisplayName("测试获取所有存档")
    public void testGetAllArchive() {
        String hostToken = tokenTestUtil.getTestToken("lzh");
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Authorization", hostToken);
        System.out.println("token = " + hostToken);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<Archive[]> response = testRestTemplate.exchange("/archive/getAll", HttpMethod.GET, httpEntity, Archive[].class);
        Archive[] archives = response.getBody();
        Assertions.assertNotNull(archives);
        Assertions.assertNotEquals(archives.length, 0);
    }

    @Test
    @DisplayName("测试获取我的存档")
    public void testGetArchive() {
        String token = tokenTestUtil.getTestToken("lzh");
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Authorization", token);
        System.out.println("token = " + token);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<Archive> response = testRestTemplate.exchange("/archive/get", HttpMethod.GET, httpEntity, Archive.class);
        Archive archives = response.getBody();
        Assertions.assertNotNull(archives);
    }

    @Test
    @Transactional
    @DisplayName("测试保存存档")
    public void testSaveArchive() {
        String token = tokenTestUtil.getTestToken("bc");
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Authorization", token);
        System.out.println("token = " + token);
        Date time = new Date();
        Archive archive = new Archive();
        archive.setTime(time);
        HttpEntity<String> httpEntity = new HttpEntity<>(archive.toString(), headers);


        testRestTemplate.exchange("/archive/save", HttpMethod.POST, httpEntity, Object.class);

        ResponseEntity<Archive> response = testRestTemplate.exchange("/archive/get", HttpMethod.GET, httpEntity, Archive.class);
        Archive archives = response.getBody();

        Assertions.assertNotNull(archives);
        Assertions.assertEquals(archive.getTime(), time);
    }
}
