package com.game;

import com.alibaba.fastjson.JSON;
import com.game.utils.messageUtils.ArrayMessage;
import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
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
import org.springframework.util.LinkedMultiValueMap;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoomControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    TokenTestUtil tokenTestUtil;

    @Test
    @DisplayName("测试删除所有房间和查看所有房间")
    public void testClearAllRoomsAndViewAllRooms() {
        String result = testRestTemplate.getForObject("/room/clearAll", String.class);
        Assertions.assertEquals(result, "清除成功!");
        ArrayMessage msg = testRestTemplate.getForObject("/room/getAll", ArrayMessage.class);
        System.out.println(JSON.toJSONString(msg));
        Assertions.assertEquals(msg.data.size(), 0); // no rooms now
    }

    @Test
    @DisplayName("测试创建和加入房间")
    public void testCreateAndJoinRoom() {
        String hostToken = tokenTestUtil.getTestToken("lzh");
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Authorization", hostToken);
        System.out.println("token = " + hostToken);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<Message> response = testRestTemplate.exchange("/room/join", HttpMethod.GET, httpEntity, Message.class);
        Message resMsg = response.getBody();
        Assertions.assertNotNull(resMsg);
        Assertions.assertEquals(resMsg.status, MessageUtil.STAT_INVALID);

        response = testRestTemplate.exchange("/room/create", HttpMethod.GET, httpEntity, Message.class);
        resMsg = response.getBody();
        Assertions.assertNotNull(resMsg);
        Assertions.assertEquals(resMsg.status, MessageUtil.STAT_OK);

        response = testRestTemplate.exchange("/room/create", HttpMethod.GET, httpEntity, Message.class);
        resMsg = response.getBody();
        Assertions.assertNotNull(resMsg);
        Assertions.assertEquals(resMsg.status, MessageUtil.STAT_INVALID);

        String clientToken = tokenTestUtil.getTestToken("lc");
        headers = new LinkedMultiValueMap<>();
        headers.add("X-Authorization", clientToken);
        System.out.println("token = " + clientToken);

        httpEntity = new HttpEntity<>(headers);
        response = testRestTemplate.exchange("/room/join", HttpMethod.GET, httpEntity, Message.class);
        resMsg = response.getBody();
        Assertions.assertNotNull(resMsg);
//        Assertions.assertEquals(resMsg.status, MessageUtil.STAT_OK);
    }

    @Test
    @DisplayName("测试获取房间信息和清除房间")
    public void testGetAndClear() {
        String token = tokenTestUtil.getTestToken("lzh");
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Authorization", token);
        System.out.println("token = " + token);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        testRestTemplate.exchange("/test/clear", HttpMethod.GET, httpEntity, Object.class);
        ResponseEntity<Map> response = testRestTemplate.exchange("/test/getRoomFeatures", HttpMethod.GET, httpEntity, Map.class);
        System.out.println(JSON.toJSONString(response.getBody()));

        response = testRestTemplate.exchange("/test/users", HttpMethod.GET, httpEntity, Map.class);
        System.out.println(JSON.toJSONString(response.getBody()));

        ResponseEntity<Message> res = testRestTemplate.exchange("/room/create", HttpMethod.GET, httpEntity, Message.class);
        Assertions.assertNotNull(res.getBody());

        response = testRestTemplate.exchange("/test/removeRoom?roomId=0", HttpMethod.GET, httpEntity, Map.class);
        System.out.println(JSON.toJSONString(response.getBody()));
    }

    @Test
    @DisplayName("测试创建或加入房间")
    public void testJoinOrCreate() {
        String hostToken = tokenTestUtil.getTestToken("bc");
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Authorization", hostToken);
        System.out.println("token = " + hostToken);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<Message> response = testRestTemplate.exchange("/room/joinOrCreate", HttpMethod.GET, httpEntity, Message.class);
        Message resMsg = response.getBody();
        Assertions.assertNotNull(resMsg);
        Assertions.assertEquals(resMsg.status, MessageUtil.STAT_OK);
        Assertions.assertEquals(resMsg.message, "创建成功！");

        String clientToken = tokenTestUtil.getTestToken("zby");
        headers = new LinkedMultiValueMap<>();
        headers.add("X-Authorization", clientToken);
        System.out.println("token = " + clientToken);
        httpEntity = new HttpEntity<>(headers);
        response = testRestTemplate.exchange("/room/joinOrCreate", HttpMethod.GET, httpEntity, Message.class);
        resMsg = response.getBody();
        Assertions.assertNotNull(resMsg);
        Assertions.assertEquals(resMsg.status, MessageUtil.STAT_OK);
        Assertions.assertEquals(resMsg.message, "加入成功！");
    }
}
