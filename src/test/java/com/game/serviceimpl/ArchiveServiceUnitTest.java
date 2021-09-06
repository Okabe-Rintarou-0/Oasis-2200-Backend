package com.game.serviceimpl;

import com.game.dao.ArchiveDao;
import com.game.dao.UserDao;
import com.game.entity.Archive;
import com.game.serviceimpl.ArchiveServiceImpl;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lzh
 * @Title:
 * @Package
 * @Description: Test Archive Service
 * @date 2021/8/13 15:02
 */
public class ArchiveServiceUnitTest {
    @InjectMocks
    private ArchiveServiceImpl archiveService;

    @Mock
    private UserDao userDao;

    @Mock
    private ArchiveDao archiveDao;

    private final Archive testArchive = new Archive("123", new Document(), new Date());

    private final List<Archive> testArchives = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        for (int i = 0; i < 5; ++i) {
            Archive testArchive = new Archive(i + "", new Document(), new Date());
            testArchives.add(testArchive);
        }

        Mockito.when(archiveDao.findAll()).thenReturn(testArchives);

        Mockito.when(archiveDao.findOne(null)).thenReturn(null);
        Mockito.when(archiveDao.findOne("123")).thenReturn(testArchive);

        Mockito.when(userDao.getArchiveId(0)).thenReturn("123");
        Mockito.when(userDao.getArchiveId(Mockito.intThat(value -> value < 0)))
                .thenReturn(null);
    }

    @Test
    @DisplayName("测试findAll")
    public void testFindAll() {
        Assertions.assertEquals(archiveService.findAll(), testArchives);
    }

    @Test
    @DisplayName("测试获取存档")
    public void testGetArchive() {
        Assertions.assertNull(archiveService.getArchive(-1));
        Assertions.assertEquals(archiveService.getArchive(0), testArchive);
    }

    @Test
    @DisplayName("测试保存存档")
    public void testSaveArchive() {
        String data = "123";
        Mockito.when(userDao.getArchiveId(1))
                .thenReturn("123");
        Mockito.when(archiveDao.saveArchive("123", data))
                .thenReturn("123");
        archiveService.saveArchive(data);
    }
}
