package com.hida.repositories;

import com.hida.configuration.RepositoryConfiguration;
import com.hida.model.TokenType;
import com.hida.model.UsedSetting;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.Assert;

/**
 * Tests the functionality of UsedSettingDaoImpl.
 *
 * @author lruffin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {RepositoryConfiguration.class})
@TestPropertySource(locations = "classpath:testConfig.properties")
public class UsedSettingRepositoryTest {
        
    private UsedSettingRepository UsedSettingRepo;
    
    @Autowired
    public void setUsedSettingRepository(UsedSettingRepository UsedSettingRepository) {
        this.UsedSettingRepo = UsedSettingRepository;
    }   

    /**
     * Tests the functionality of UsedSettingDao save.
     */
    @Test
    public void testSaveAndFindByIdTest() {
        final UsedSetting setting = getSampleUsedSetting();
        
        UsedSettingRepo.save(setting);
        UsedSetting entity = UsedSettingRepo.findOne(setting.getId());
        Assert.assertNotNull(entity);
    }   
    
    /**
     * Attempts to find a UsedSetting by the fields. 
     */
    @Test
    public void testSaveAndfindByUsedSetting() {
        UsedSetting sampleSetting = getSampleUsedSetting();
        UsedSettingRepo.save(sampleSetting);
        
        UsedSetting entity = UsedSettingRepo.findUsedSetting(sampleSetting.getPrefix(),
                sampleSetting.getTokenType(),
                sampleSetting.getCharMap(),
                sampleSetting.getRootLength(),
                sampleSetting.isSansVowels());
        Assert.assertNotNull(entity);
    }

    /**
     * Returns a sample UsedSetting entity.
     *
     * @return
     */
    private UsedSetting getSampleUsedSetting() {
        UsedSetting setting = new UsedSetting("",
                TokenType.DIGIT,
                "d",
                1,
                true,
                1);

        return setting;
    }
    
    /**
     * Deletes all entries in the in-memory database after each test
     */
    @After
    public void tearDown() {
        UsedSettingRepo.deleteAll();
    }
}
