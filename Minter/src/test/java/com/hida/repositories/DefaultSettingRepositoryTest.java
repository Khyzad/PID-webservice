package com.hida.repositories;

import com.hida.configuration.RepositoryConfiguration;
import com.hida.model.DefaultSetting;
import com.hida.model.TokenType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.Assert;

/**
 * Tests the functionality of DefaultSettingDaoImpl.
 *
 * @author lruffin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {RepositoryConfiguration.class})
@TestPropertySource(locations = "classpath:testConfig.properties")
public class DefaultSettingRepositoryTest {
       
    private DefaultSettingRepository DefaultSettingRepo;
    
    @Autowired
    public void setDefaultSettingRepository(DefaultSettingRepository DefaultSettingRepo) {
        this.DefaultSettingRepo = DefaultSettingRepo;
    }   

    /**
     * Tests to see if DefaultSettingDao can find an entity with an id of 1.
     */
    @Test
    public void testSaveAndFind() {
        DefaultSetting sample = getSampleDefaultSetting();                        
        DefaultSettingRepo.save(sample);
                
        DefaultSetting entity = DefaultSettingRepo.findCurrentDefaultSetting();                
        Assert.assertNotNull(entity);
    }

    /**
     * Returns a sample DefaultSetting entity.
     *
     * @return
     */
    private DefaultSetting getSampleDefaultSetting() {
        DefaultSetting setting = new DefaultSetting("",
                "",
                TokenType.DIGIT,
                "d",
                1,
                true,
                true,
                true);

        return setting;
    }        
}
