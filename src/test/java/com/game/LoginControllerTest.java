package com.game;

import com.game.utils.messageUtils.Message;
import com.game.utils.messageUtils.MessageUtil;
import net.sf.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("测试登录")
    public void testLogin() {
        //test for normal login
        JSONObject loginInfo = new JSONObject();
        loginInfo.put("username", "lzh");
        loginInfo.put("pwd", "123");
        ResponseEntity<Message> response = testRestTemplate.postForEntity("/login", loginInfo, Message.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Message resBody = response.getBody();
        Assertions.assertNotNull(resBody);
        Assertions.assertEquals(resBody.status, MessageUtil.STAT_OK);

        //test for login with wrong password
        loginInfo.put("username", "lzh");
        loginInfo.put("pwd", "10086");
        response = testRestTemplate.postForEntity("/login", loginInfo, Message.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        resBody = response.getBody();
        Assertions.assertNotNull(resBody);
        Assertions.assertEquals(resBody.status, MessageUtil.STAT_INVALID);

        //test for login without password
        loginInfo.put("username", "lzh");
        loginInfo.put("pwd", "");
        response = testRestTemplate.postForEntity("/login", loginInfo, Message.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        resBody = response.getBody();
        Assertions.assertNotNull(resBody);
        Assertions.assertEquals(resBody.status, MessageUtil.STAT_INVALID);

        //test for login without username
        loginInfo.put("username", "");
        loginInfo.put("pwd", "123");
        response = testRestTemplate.postForEntity("/login", loginInfo, Message.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        resBody = response.getBody();
        Assertions.assertNotNull(resBody);
        Assertions.assertEquals(resBody.status, MessageUtil.STAT_INVALID);

        //test for login without username and password
        loginInfo.put("username", "");
        loginInfo.put("pwd", "");
        response = testRestTemplate.postForEntity("/login", loginInfo, Message.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        resBody = response.getBody();
        Assertions.assertNotNull(resBody);
        Assertions.assertEquals(resBody.status, MessageUtil.STAT_INVALID);

        //test for login without request body.
        loginInfo.clear();
        response = testRestTemplate.postForEntity("/login", loginInfo, Message.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        resBody = response.getBody();
        Assertions.assertNotNull(resBody);
        Assertions.assertEquals(resBody.status, MessageUtil.STAT_INVALID);
    }
}
