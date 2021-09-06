//package com.game;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.game.controller.CombatController;
//import com.game.entity.CharacterInfo;
//import com.game.service.CombatCacheService;
//import com.game.service.CombatService;
//import com.game.utils.messageUtils.MessageUtil;
//import org.junit.Ignore;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
///**
// * @author lzh
// * @Title:
// * @Package
// * @Description: Test CombatController
// * @date 2021/8/13 10:23
// */
//@WebMvcTest(CombatController.class)
//public class CombatControllerUnitTest {
//    @MockBean
//    private CombatService combatService;
//
//    @MockBean
//    private CombatCacheService combatCacheService;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    @DisplayName("测试接受战斗")
//    public void testAcceptCombat() throws Exception {
//        //if not accepted;
//        given(combatService.acceptCombat(-1)).willReturn(false);
//        MvcResult result = mockMvc.perform(
//                get("/accept")
//                        .sessionAttr("userId", 0)
//                        .sessionAttr("userIdentity", 0)
//                        .sessionAttr("username", "lzh")
//        )
//                .andReturn();
//        String response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        System.out.println(response);
//        JSONObject jsonObject = JSON.parseObject(response);
//        Assertions.assertEquals(jsonObject.get("status"), MessageUtil.STAT_INVALID);
//
//        given(combatService.acceptCombat(-1)).willReturn(true);
//        result = mockMvc.perform(
//                get("/accept")
//                        .sessionAttr("userId", 0)
//                        .sessionAttr("userIdentity", 0)
//                        .sessionAttr("username", "lzh")
//        )
//                .andReturn();
//        response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        System.out.println(response);
//        jsonObject = JSON.parseObject(response);
//        Assertions.assertEquals(jsonObject.get("status"), MessageUtil.STAT_OK);
//    }
//
//    @Test
//    @DisplayName("测试拒绝战斗")
//    public void testDenyCombat() throws Exception {
//        given(combatService.denyCombat(1)).willReturn(true);
//        MvcResult result = mockMvc.perform(
//                get("/deny")
//                        .sessionAttr("userId", 0)
//                        .sessionAttr("userIdentity", 0)
//                        .sessionAttr("username", "lzh")
//        )
//                .andReturn();
//        String response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        System.out.println(response);
//        JSONObject jsonObject = JSON.parseObject(response);
//        Assertions.assertEquals(jsonObject.get("status"), MessageUtil.STAT_OK);
//
//        given(combatService.denyCombat(-1)).willReturn(false);
//        result = mockMvc.perform(
//                get("/deny")
//                        .sessionAttr("userId", 0)
//                        .sessionAttr("userIdentity", 0)
//                        .sessionAttr("username", "lzh")
//        )
//                .andReturn();
//        response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        System.out.println(response);
//        jsonObject = JSON.parseObject(response);
//        Assertions.assertEquals(jsonObject.get("status"), MessageUtil.STAT_INVALID);
//    }
//
//    @Test
//    @DisplayName("测试上传用户信息")
//    public void testUploadPlayerInfo() throws Exception {
//        List<CharacterInfo> testInfo = new ArrayList<>();
//        given(combatService.uploadPlayerInfo(1, testInfo)).willReturn(true);
//        MvcResult result = mockMvc.perform(post("/uploadPlayerInfo")
//                .sessionAttr("userId", 0)
//                .sessionAttr("userIdentity", 0)
//                .sessionAttr("username", "lzh")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(JSON.toJSONString(testInfo))
//        )
//                .andExpect(status().isOk())
//                .andReturn();
//        String response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        System.out.println(response);
//        JSONObject jsonObject = JSON.parseObject(response);
//        Assertions.assertEquals(jsonObject.get("status"), MessageUtil.STAT_OK);
//
//        given(combatService.uploadPlayerInfo(1, testInfo)).willReturn(false);
//        result = mockMvc.perform(post("/uploadPlayerInfo")
//                .sessionAttr("userId", 0)
//                .sessionAttr("userIdentity", 0)
//                .sessionAttr("username", "lzh")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(JSON.toJSONString(testInfo))
//        )
//                .andExpect(status().isOk())
//                .andReturn();
//        response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        System.out.println(response);
//        jsonObject = JSON.parseObject(response);
//        Assertions.assertEquals(jsonObject.get("status"), MessageUtil.STAT_INVALID);
//    }
//
//    @Test
//    @Ignore
//    @DisplayName("测试结束战斗")
//    public void testCombat() {
//        //test without params
//
//    }
//}
