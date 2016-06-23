package com.hida.repositories;

import com.hida.configuration.RepositoryConfiguration;
import com.hida.model.Token;
import com.hida.model.UsedSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * Tests the functionality of UsedSettingRepository.
 *
 * @author lruffin
 */
@WebAppConfiguration
@IntegrationTest
@SpringApplicationConfiguration(classes = {RepositoryConfiguration.class})
@TestPropertySource(locations = "classpath:testConfig.properties")
@TestExecutionListeners(inheritListeners = false, listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class})
public class UsedSettingRepositoryTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private UsedSettingRepository usedSettingRepo_;

    /**
     * Tests the functionality of UsedSettingDao save.
     */
    @Test
    public void testSaveAndDelete() {
        final UsedSetting setting = getSampleUsedSetting();

        usedSettingRepo_.save(setting);
        UsedSetting entity = usedSettingRepo_.findOne(setting.getId());
        Assert.assertNotNull(entity);

        usedSettingRepo_.delete(entity);
        long size = usedSettingRepo_.count();

        Assert.assertEquals(0, size);
    }

    @Test
    public void testFindById() {
        UsedSetting setting = getSampleUsedSetting();
        setting.setId(1);
        usedSettingRepo_.save(setting);

        UsedSetting entity = usedSettingRepo_.findOne(setting.getId());
        Assert.assertNotNull(entity);
    }

    /**
     * Attempts to find a UsedSetting by the fields.
     */
    @Test
    public void testSaveAndFindByUsedSetting() {
        UsedSetting sampleSetting = getSampleUsedSetting();
        usedSettingRepo_.save(sampleSetting);

        UsedSetting entity = usedSettingRepo_.findUsedSetting(sampleSetting.getPrefix(),
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
                Token.DIGIT,
                "d",
                1,
                true,
                1);

        return setting;
    }

    /**
     * Deletes all entries in the in-memory database after each test
     */
    @AfterMethod
    public void tearDown() {
        usedSettingRepo_.deleteAll();
    }
}
