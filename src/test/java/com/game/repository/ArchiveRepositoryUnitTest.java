package com.game.repository;

import com.game.entity.Archive;
import com.game.properties.ClusterProperties;
import com.game.properties.JwtProperties;
import com.game.properties.RoomProperties;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

/**
 * @author lzh
 * @Title: 测试存档Repository
 * @Package
 * @Description: Test Archive Repository
 * @date 2021/8/13 13:18
 */
@DataMongoTest //使用mongo，还有类似的@MybatisTest
@Rollback // 是否回滚，默认是true
// 下面这句annotation的意思是使用真正的数据库源而不是使用虚拟数据库。
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ArchiveRepositoryUnitTest {
    @Autowired
    private ArchiveRepository archiveRepository;

    @Test
    @DisplayName("测试保存和查询存档")
    public void testSaveAndQueryArchive() {
        //construct test data:
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("building", new JSONArray());
        jsonObject.put("character", new JSONArray());
        jsonObject.put("inventory", new JSONArray());
        jsonObject.put("store", new JSONArray());
        Document document = Document.parse(jsonObject.toString());
        Date justNow = new Date();
        Archive archive = new Archive(document, justNow);
        String saveId = archiveRepository.save(archive).getId();
        Assertions.assertEquals(archiveRepository.findArchiveById(saveId), archive);

        Assertions.assertNull(archiveRepository.findArchiveById(null));
        Assertions.assertNull(archiveRepository.findArchiveById(""));
        Assertions.assertNull(archiveRepository.findArchiveById("123"));

        Date now = new Date();
        Archive newArchive = new Archive(saveId, document, now);
        archiveRepository.save(newArchive);
        Archive tgtArchive = archiveRepository.findArchiveById(saveId);
        Assertions.assertNotEquals(tgtArchive.getTime(), justNow);
        Assertions.assertEquals(tgtArchive.getTime(), now);
    }
}
