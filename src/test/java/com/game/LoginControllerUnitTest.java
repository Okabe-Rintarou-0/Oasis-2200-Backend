//package com.game;
//
//import com.alibaba.fastjson.JSONObject;
//import com.game.controller.LoginController;
//import com.game.properties.ClusterProperties;
//import com.game.service.LoginService;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//
//import com.game.utils.messageUtils.MessageUtil;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.mockito.BDDMockito.given;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import java.nio.charset.StandardCharsets;
//
//@WebMvcTest(LoginController.class)
//public class LoginControllerUnitTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ClusterProperties clusterProperties;
//
//    @MockBean
//    private LoginService loginService;
//
//    @Test
//    @DisplayName("测试登录")
//    public void testLogin() throws Exception {
//
//        given(loginService.login("lzh", "000")).willReturn(MessageUtil.createMessage(
//                MessageUtil.STAT_INVALID, MessageUtil.MSG_LOGIN_INVALID
//        ));
//
//        JSONObject loginInfo = new JSONObject();
//        loginInfo.put("username", "lzh");
//        loginInfo.put("pwd", "000");
//        MvcResult result = mockMvc.perform(
//                post("/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(JSONObject.toJSONString(loginInfo))
//        )
//                .andExpect(status().isOk())
//                .andReturn();
//        MockHttpServletResponse response = result.getResponse();
//        System.out.println(response.getContentAsString(StandardCharsets.UTF_8));
//    }
//}
