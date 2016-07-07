/*
 * Copyright 2016 Lawrence Ruffin, Leland Lopez, Brittany Cruz, Stephen Anspach
 *
 * Developed in collaboration with the Hawaii State Digital Archives.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
       DirtiesContextTestExecutionListener.class })
public class UsedSettingRepositoryTest extends AbstractTestNGSpringContextTests {
    
    @Autowired
    private UsedSettingRepository UsedSettingRepo;           

    /**
     * Tests the functionality of UsedSettingDao save.
     */
    @Test
    public void testSaveAndDelete() {
        final UsedSetting setting = getSampleUsedSetting();
        
        UsedSettingRepo.save(setting);
        UsedSetting entity = UsedSettingRepo.findOne(setting.getId());
        Assert.assertNotNull(entity);
        
        UsedSettingRepo.delete(entity);
        long size = UsedSettingRepo.count();
        
        Assert.assertEquals(0, size);
    }   
    
    @Test
    public void testFindById(){
        UsedSetting setting = getSampleUsedSetting();
        setting.setId(1);
        UsedSettingRepo.save(setting);
        
        UsedSetting entity = UsedSettingRepo.findOne(setting.getId());
        Assert.assertNotNull(entity);
    }
    
    /**
     * Attempts to find a UsedSetting by the fields. 
     */
    @Test
    public void testSaveAndFindByUsedSetting() {
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
        UsedSettingRepo.deleteAll();
    }
}
