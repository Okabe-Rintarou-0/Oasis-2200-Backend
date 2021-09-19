//package com.game;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.game.controller.RoomController;
//import com.game.dto.RoomDto;
//import com.game.interceptor.TokenValidateInterceptor;
//import com.game.service.RoomCacheService;
//import com.game.service.RoomService;
//import com.game.utils.ipUtils.IpUtil;
//import com.game.utils.messageUtils.MessageUtil;
//import com.game.utils.testUtils.ClusterTestUtil;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import java.nio.charset.StandardCharsets;
//
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//
///*
// * 测试基本上可以分为两种，一种是SpringBootTest，一种是WebMvbTest
// * 前者会真的代入spring boot环境跑一遍测试，也就是说会去做依赖注入，所以可以用autowired实现自动注入，而且会
// * 真的去跑一遍http请求，因而比较耗时。
// * 后者基于mockito，并不会进行依赖注入，而是把之前所需要的bean都用mock的方式进行模拟，用户可以指定
// * 对应的请求下的返回值，比如我要测controller，那我就指定好service在不同参数的情况下的返回值，然后对controller
// * 的接口进行测试。这边注意到使用了sessionAttr，因为如果没有登录信息则会被拦截器所拦截。
// *
// * 不难看出两者的不同，前者真的会去跑一次http，测试的是所有的接口（包括最深层的，嵌套的接口）
// * 而后者只会测试最浅层的接口，比如我下面写的这个，因为用mockito模拟了service层的行为，因而实际上
// * 只测试了controller层而已。
// *
// * 模拟的方法可以用我使用的give...willReturn语句
// * 也可以在@Before注解的方法下面使用Mockito.when(...).thenReturn(...);
// * 2021/8/10 林子宏标注
// */
//
//@WebMvcTest(RoomController.class)
//public class RoomControllerUnitTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private RoomCacheService roomCacheService;
//
//    @MockBean
//    private IpUtil ipUtil;
//
//    @MockBean
//    private TokenValidateInterceptor tokenValidateInterceptor;
//
//    @MockBean
//    private ClusterTestUtil clusterTestUtil;
//
//    @MockBean
//    SimpMessagingTemplate simpMessagingTemplate;
//
//    @MockBean
//    private RoomService roomService;
//
//    @Test
//    @DisplayName("测试创建房间接口（合法创建）")
//    public void testCreateRoomValid() throws Exception {
//        given(roomCacheService.createRoom(0)).willReturn(MessageUtil.createMessage(
//                MessageUtil.STAT_OK, "创建成功")
//        );
//        MvcResult result = mockMvc.perform(get("/createRoom")
//                .sessionAttr("userId", 0)
//                .sessionAttr("userIdentity", 0)
//                .sessionAttr("username", "lzh")
//        )
//                .andReturn();
//        String response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        System.out.println(response);
//        JSONObject jsonObject = JSON.parseObject(response);
//        Assertions.assertEquals(jsonObject.get("status"), MessageUtil.STAT_OK);
//    }
//
//    @Test
//    @DisplayName("测试创建房间接口（非法创建）")
//    public void testCreateRoomInvalid() throws Exception {
//        given(roomCacheService.createRoom(-1)).willReturn(MessageUtil.createMessage(
//                MessageUtil.STAT_INVALID, "创建失败")
//        );
//        MvcResult result = mockMvc.perform(get("/createRoom"))
//                .andReturn();
//        int responseLen = result.getResponse().getContentLength();
//        Assertions.assertEquals(responseLen, 0); //it should be has res, for it's intercepted
//    }
//
//
//    @Test
//    @DisplayName("测试加入房间")
//    public void testJoinRoom() throws Exception {
//        given(roomService.joinRandomValidRoom(0)).willReturn(new RoomDto()); //这边作假设
//
//        MvcResult result = mockMvc.perform(get("/joinRoom")
//                .sessionAttr("userId", 0)
//                .sessionAttr("userIdentity", 0)
//                .sessionAttr("username", "lzh")
//        )
//                .andReturn();
//        String response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        System.out.println(response);
//        JSONObject jsonObject = JSON.parseObject(response);
//        Assertions.assertEquals(jsonObject.get("status"), MessageUtil.STAT_OK);
//
//        given(roomService.joinRandomValidRoom(-1)).willReturn(null); //这边作假设
//
//        result = mockMvc.perform(get("/joinRoom")
//                .sessionAttr("userId", 0)
//                .sessionAttr("userIdentity", 0)
//                .sessionAttr("username", "lzh")
//        )
//                .andReturn();
//        response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        System.out.println(response);
//        jsonObject = JSON.parseObject(response);
//        Assertions.assertEquals(jsonObject.get("status"), MessageUtil.STAT_INVALID);
//    }
//}
