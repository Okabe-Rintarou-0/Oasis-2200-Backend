//package com.game;
//
//import com.alibaba.fastjson.JSON;
//import com.game.controller.ArchiveController;
//import com.game.entity.Archive;
//import com.game.interceptor.TokenValidateInterceptor;
//import com.game.properties.ClusterProperties;
//import com.game.service.ArchiveService;
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//import org.bson.Document;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import static org.mockito.BDDMockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * @author lzh
// * @Title: 测试存档controller
// * @Package
// * @Description: Test Archive Controller
// * @date 2021/8/13 15:23
// */
//@WebMvcTest(ArchiveController.class)
//public class ArchiveControllerUnitTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    ClusterProperties clusterProperties;
//
//    @MockBean
//    TokenValidateInterceptor tokenValidateInterceptor;
//
//    @MockBean
//    SimpMessagingTemplate simpMessagingTemplate;
//
//    @MockBean
//    ArchiveService archiveService;
//
//    private final List<Archive> testArchives = new ArrayList<>();
//
//    @Test
//    @DisplayName("测试findAll")
//    public void testFindAll() throws Exception {
//        for (int i = 0; i < 5; ++i) {
//            testArchives.add(new Archive());
//        }
//        given(archiveService.findAll()).willReturn(testArchives);
//        MvcResult result = mockMvc.perform(get("/allArchives")
//                .sessionAttr("userId", 0)
//                .sessionAttr("userIdentity", 0)
//                .sessionAttr("username", "lzh")
//        )
//                .andExpect(status().isOk())
//                .andReturn();
//        String response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        System.out.println(response);
//    }
//
//    @Test
//    @DisplayName("测试保存存档")
//    public void testSaveArchive() throws Exception {
//        JSONObject data = new JSONObject();
//        data.put("buildings", new JSONArray());
//        MvcResult result = mockMvc.perform(post("/saveArchive")
//                .sessionAttr("userId", 0)
//                .sessionAttr("userIdentity", 0)
//                .sessionAttr("username", "lzh")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(JSON.toJSONString(data))
//        )
//                .andExpect(status().isOk())
//                .andReturn();
//    }
//
//    @Test
//    @DisplayName("测试获取用户存档")
//    public void testGetArchive() throws Exception {
//        Archive testArchive = new Archive("123", new Document(), new Date());
//        given(archiveService.getArchive(0)).willReturn(testArchive);
//        given(archiveService.getArchive(-1)).willReturn(null);
//
//        MvcResult result = mockMvc.perform(get("/archive"))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
//    }
//}
