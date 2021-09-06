package com.game;

import com.alibaba.fastjson.JSON;
import com.game.entity.CharacterInfo;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import com.game.utils.testUtils.TokenTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description:
 * @date 2021/9/4 19:30
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CombatControllerTest {
    @Autowired
    TokenTestUtil tokenTestUtil;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("测试接受和拒绝比赛")
    public void testAccept() {
        String hostToken = tokenTestUtil.getTestToken("bc");
        HttpHeaders hostHeaders = new HttpHeaders();
        hostHeaders.add("X-Authorization", hostToken);
        System.out.println("token = " + hostToken);
        HttpEntity<String> hostHttpEntity = new HttpEntity<>(hostHeaders);
        ResponseEntity<Message> response = testRestTemplate.exchange("/accept", HttpMethod.GET, hostHttpEntity, Message.class);
        Message message = response.getBody();
        Assertions.assertNotNull(message);
        Assertions.assertEquals(message.status, MessageUtil.STAT_INVALID);
        response = testRestTemplate.exchange("/deny", HttpMethod.GET, hostHttpEntity, Message.class);
        message = response.getBody();
        Assertions.assertNotNull(message);
        Assertions.assertEquals(message.status, MessageUtil.STAT_INVALID);

        String clientToken = tokenTestUtil.getTestToken("lc");
        HttpHeaders clientHeaders = new HttpHeaders();
        clientHeaders.add("X-Authorization", clientToken);
        System.out.println("token = " + clientToken);
        HttpEntity<String> clientHttpEntity = new HttpEntity<>(clientHeaders);

        response = testRestTemplate.exchange("/createRoom", HttpMethod.GET, hostHttpEntity, Message.class);
        message = response.getBody();
        Assertions.assertNotNull(message);
        Assertions.assertEquals(message.status, MessageUtil.STAT_OK);

        response = testRestTemplate.exchange("/joinRoom", HttpMethod.GET, clientHttpEntity, Message.class);
        message = response.getBody();
        Assertions.assertNotNull(message);
        Assertions.assertEquals(message.status, MessageUtil.STAT_OK);

        response = testRestTemplate.exchange("/deny", HttpMethod.GET, hostHttpEntity, Message.class);
        message = response.getBody();
        Assertions.assertNotNull(message);
        Assertions.assertEquals(message.status, MessageUtil.STAT_OK);

        testRestTemplate.exchange("/end?winner=0", HttpMethod.GET, hostHttpEntity, Object.class);

        List<CharacterInfo> infos = new ArrayList<>();
        infos.add(new CharacterInfo());
        hostHeaders = new HttpHeaders();
        hostHeaders.add("X-Authorization", hostToken);
        hostHeaders.setContentType(MediaType.APPLICATION_JSON);
        hostHttpEntity = new HttpEntity<>(JSON.toJSONString(infos), hostHeaders);
        response = testRestTemplate.exchange("/uploadPlayerInfo", HttpMethod.POST, hostHttpEntity, Message.class);
        message = response.getBody();
        Assertions.assertNotNull(message);
        Assertions.assertEquals(message.status, MessageUtil.STAT_INVALID);

        response = testRestTemplate.exchange("/createRoom", HttpMethod.GET, hostHttpEntity, Message.class);
        message = response.getBody();
        Assertions.assertNotNull(message);
        Assertions.assertEquals(message.status, MessageUtil.STAT_OK);

        response = testRestTemplate.exchange("/joinRoom", HttpMethod.GET, clientHttpEntity, Message.class);
        message = response.getBody();
        Assertions.assertNotNull(message);
        Assertions.assertEquals(message.status, MessageUtil.STAT_OK);

        response = testRestTemplate.exchange("/accept", HttpMethod.GET, hostHttpEntity, Message.class);
        message = response.getBody();
        Assertions.assertNotNull(message);
        Assertions.assertEquals(message.status, MessageUtil.STAT_OK);

        hostHeaders = new HttpHeaders();
        hostHeaders.add("X-Authorization", hostToken);
        hostHeaders.setContentType(MediaType.APPLICATION_JSON);
        hostHttpEntity = new HttpEntity<>(JSON.toJSONString(infos), hostHeaders);
        response = testRestTemplate.exchange("/uploadPlayerInfo", HttpMethod.POST, hostHttpEntity, Message.class);
        message = response.getBody();
        Assertions.assertNotNull(message);
        Assertions.assertEquals(message.status, MessageUtil.STAT_OK);

        clientHeaders = new HttpHeaders();
        clientHeaders.add("X-Authorization", hostToken);
        clientHeaders.setContentType(MediaType.APPLICATION_JSON);
        clientHttpEntity = new HttpEntity<>(JSON.toJSONString(infos), clientHeaders);
        response = testRestTemplate.exchange("/uploadPlayerInfo", HttpMethod.POST, clientHttpEntity, Message.class);
        message = response.getBody();
        Assertions.assertNotNull(message);
        Assertions.assertEquals(message.status, MessageUtil.STAT_OK);
    }
}
