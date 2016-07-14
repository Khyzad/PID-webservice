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
package com.hida.service;

import com.hida.configuration.RepositoryConfiguration;
import com.hida.repositories.DefaultSettingRepository;
import com.hida.repositories.PidRepository;
import com.hida.repositories.UsedSettingRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * A test class designed to test the functionality of RepositoryService
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
public class RepositoryServiceTest extends AbstractTestNGSpringContextTests {
    
    @Mock
    private PidRepository pidRepo_;

    @Mock
    private UsedSettingRepository usedSettingRepo_;

    @Mock
    private DefaultSettingRepository defaultSettingRepo_;
    
    @InjectMocks
    private RepositoryService service_;        
    
    @Test
    public void testGeneratePids(){
        Assert.fail("unimplemented");
    }
    
    @Test
    public void testGetRemainingPermutations(){
        Assert.fail("unimplemented");
    }
    
    @Test
    public void testPersistPids(){
        Assert.fail("unimplemented");
    }
    
    @Test
    public void testUpdateCurrentSetting(){
        Assert.fail("unimplemented");
    }
    
    @Test
    public void testInitializeStoredSetting(){
        Assert.fail("unimplemented");
    }
    

}
