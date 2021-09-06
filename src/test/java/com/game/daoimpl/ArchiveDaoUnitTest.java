package com.game.daoimpl;

import com.alibaba.fastjson.JSON;
import com.game.entity.Archive;
import com.game.repository.ArchiveRepository;
import net.sf.json.JSONObject;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description: Test Archive Dao
 * @date 2021/8/13 14:15
 */
public class ArchiveDaoUnitTest {
    @InjectMocks
    private ArchiveDaoImpl archiveDao;

    @Mock
    private ArchiveRepository archiveRepository;

    private Document testDoc;

    private JSONObject testJson;

    private Archive testArchive;

    private Archive testArchiveWithNullId;

    private final List<Archive> testArchives = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        testJson = new JSONObject();
        testJson.put("test", "123");

        testDoc = Document.parse(JSON.toJSONString(testJson));

        testArchiveWithNullId = new Archive(null, testDoc, new Date());

        testArchive = new Archive("123", testDoc, new Date());

        MockitoAnnotations.initMocks(this);

        Mockito.when(archiveRepository.findArchiveById("123"))
                .thenReturn(testArchive);

        for (int i = 0; i < 5; ++i)
            testArchives.add(testArchive);

        Mockito.when(archiveRepository.findAll()).thenReturn(testArchives);
    }

    @Test
    @DisplayName("测试findOne")
    public void testFindOne() {
        Archive archive = archiveDao.findOne("123");
        Assertions.assertEquals(archive, testArchive);

        archive = archiveDao.findOne(null);
        Assertions.assertNull(archive);

        archive = archiveDao.findOne("");
        Assertions.assertNull(archive);

        archive = archiveDao.findOne("111");
        Assertions.assertNull(archive);
    }

    @Test
    @DisplayName("测试findAll")
    public void testFindAll() {
        List<Archive> archives = archiveDao.findAll();
        Assertions.assertEquals(archives, testArchives);
    }

    @Test
    @DisplayName("测试保存存档")
    public void testSaveArchive() {
        Mockito.when(archiveRepository.save(Mockito.refEq(testArchiveWithNullId, "data", "time")))
                .thenReturn(new Archive("666", testArchiveWithNullId.getData(), testArchiveWithNullId.getTime()));
        String aid = archiveDao.saveArchive(null, testJson.toString());
        Assertions.assertEquals(aid, "666");
//        aid = archiveDao.saveArchive("123", testJson.toString());

        Mockito.when(archiveRepository.save(Mockito.any())).thenReturn(testArchive);
        aid = archiveDao.saveArchive("123", testJson.toString());
        Assertions.assertEquals(aid, "123");
    }
}
