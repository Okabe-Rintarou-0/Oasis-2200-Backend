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
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

@Rollback
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegisterControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("测试验证码")
    public void testVCode() {
        //test for normal email
        ResponseEntity<Message> response = testRestTemplate.getForEntity("/getvcode?email=lvzhou2200@163.com", Message.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Message resBody = response.getBody();
        Assertions.assertNotNull(resBody);
        Assertions.assertEquals(resBody.status, MessageUtil.STAT_OK);

        //test for invalid email name
        response = testRestTemplate.getForEntity("/getvcode?email=@163.com", Message.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        resBody = response.getBody();
        Assertions.assertNotNull(resBody);
        Assertions.assertEquals(resBody.status, MessageUtil.STAT_INVALID);
    }

    @Test
    @DisplayName("测试注册")
    @Transactional
    public void testRegister(){
        //test for normal register
        JSONObject registerInfo = new JSONObject();
        registerInfo.put("username", "not-used-name");
        registerInfo.put("password", "123");
        registerInfo.put("email","lvzhou2200@163.com");
        registerInfo.put("vcode","");
        ResponseEntity<Message> response = testRestTemplate.postForEntity("/register", registerInfo, Message.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Message resBody = response.getBody();
        Assertions.assertNotNull(resBody);
        Assertions.assertEquals(resBody.status, MessageUtil.STAT_INVALID);

        //test for register of existed username
        registerInfo = new JSONObject();
        registerInfo.put("username", "lc");
        registerInfo.put("password", "123");
        registerInfo.put("email","lvzhou2200@163.com");
        registerInfo.put("vcode","");
        response = testRestTemplate.postForEntity("/register", registerInfo, Message.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        resBody = response.getBody();
        Assertions.assertNotNull(resBody);
        Assertions.assertEquals(resBody.status, MessageUtil.STAT_INVALID);
    }

    @Test
    @DisplayName("测试无验证码注册")
    @Transactional
    public void testRegisterWithoutVCode(){
        //test for normal register
        JSONObject registerInfo = new JSONObject();
        registerInfo.put("username", "132131yyy");
        registerInfo.put("password", "123");
        registerInfo.put("email","lvzhou2200@163.com");
        ResponseEntity<Message> response = testRestTemplate.postForEntity("/registerWithoutVCode", registerInfo, Message.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Message resBody = response.getBody();
        Assertions.assertNotNull(resBody);
//        Assertions.assertEquals(resBody.status, MessageUtil.STAT_OK);

        //test for register of existed username
        registerInfo = new JSONObject();
        registerInfo.put("username", "lc");
        registerInfo.put("password", "123");
        registerInfo.put("email","lvzhou2200@163.com");
        response = testRestTemplate.postForEntity("/registerWithoutVCode", registerInfo, Message.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        resBody = response.getBody();
        Assertions.assertNotNull(resBody);
        Assertions.assertEquals(resBody.status, MessageUtil.STAT_INVALID);
    }
}
