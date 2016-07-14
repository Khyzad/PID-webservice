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
import com.hida.model.DefaultSetting;
import com.hida.model.Pid;
import com.hida.model.Token;
import com.hida.model.UsedSetting;
import com.hida.repositories.DefaultSettingRepository;
import com.hida.repositories.PidRepository;
import com.hida.repositories.UsedSettingRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
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

    private final String TEST_FILE = "testDefaultSetting.properties";

    @Mock
    private PidRepository pidRepo_;

    @Mock
    private UsedSettingRepository usedSettingRepo_;

    @Mock
    private DefaultSettingRepository defaultSettingRepo_;

    @InjectMocks
    private RepositoryService service_;
    
    private DefaultSetting defaultSetting_;

    /**
     * Sets up Mockito
     *
     * @throws Exception
     */
    @BeforeClass
    public void setUpClass() throws Exception {
        // set up Mockito
        MockitoAnnotations.initMocks(this);
        
        // get default setting values from test properties file
        defaultSetting_ = this.readPropertiesFile(TEST_FILE);
        
        // return null when any repository attempts to save
        when(pidRepo_.save(any(Pid.class))).thenReturn(null);
        when(usedSettingRepo_.save(any(UsedSetting.class))).thenReturn(null);
        when(defaultSettingRepo_.save(any(DefaultSetting.class))).thenReturn(null);
    }

    @Test
    public void testGeneratePids() {
        // assume that any Pids created aren't already persisted 
        when(pidRepo_.findOne(any(String.class))).thenReturn(null);

        // retrieve a sample DefaultSetting entity
        int actualAmount = 5;
        Set<Pid> testSet = service_.generatePids(defaultSetting_, actualAmount);

        // test behavior
        Assert.assertEquals(actualAmount, testSet.size());
        verify(pidRepo_, atLeast(actualAmount)).findOne(any(String.class));
    }

    @Test
    public void testGetRemainingPermutations() {
        Assert.fail("unimplemented");
    }

    @Test
    public void testPersistPids() {
        Assert.fail("unimplemented");
    }

    @Test
    public void testUpdateCurrentSetting() {
        Assert.fail("unimplemented");
    }

    @Test
    public void testInitializeStoredSetting() {
        Assert.fail("unimplemented");
    }

    /**
     * Set all the keys' values in testDefaultSettings to default values to
     * ensure that the values are being changed during the updatedChangedSetting
     * tests.
     *
     * @throws Exception
     */
    @AfterTest
    private void resetTestReadDefaultProperties() throws Exception {
        DefaultSetting setting = readPropertiesFile(TEST_FILE);

        Properties prop = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(TEST_FILE);
        File file = new File(url.toURI());
        OutputStream output = new FileOutputStream(file);

        // set the properties value
        prop.setProperty("prepend", setting.getPrepend());
        prop.setProperty("prefix", setting.getPrefix());
        prop.setProperty("cacheSize", setting.getCacheSize() + "");
        prop.setProperty("charMap", setting.getCharMap());
        prop.setProperty("rootLength", setting.getRootLength() + "");
        prop.setProperty("tokenType", setting.getTokenType() + "");
        prop.setProperty("sansVowel", setting.isSansVowels() + "");
        prop.setProperty("auto", setting.isAuto() + "");
        prop.setProperty("random", setting.isRandom() + "");

        // save and close
        prop.store(output, "");
        output.close();

    }

    /**
     * Read a given properties file and return its values in the form of a
     * DefaultSetting object
     *
     * @return DefaultSetting object with read values
     * @throws IOException Thrown when the file cannot be found
     */
    private DefaultSetting readPropertiesFile(String filename) throws IOException {
        Properties prop = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream input = loader.getResourceAsStream(filename);

        DefaultSetting setting = new DefaultSetting();

        // load a properties file
        prop.load(input);

        // get the property value, store it, and return it
        setting.setPrepend(prop.getProperty("prepend"));
        setting.setPrefix(prop.getProperty("prefix"));
        setting.setCacheSize(Long.parseLong(prop.getProperty("cacheSize")));
        setting.setCharMap(prop.getProperty("charMap"));
        setting.setTokenType(Token.valueOf(prop.getProperty("tokenType")));
        setting.setRootLength(Integer.parseInt(prop.getProperty("rootLength")));
        setting.setSansVowels(Boolean.parseBoolean(prop.getProperty("sansVowel")));
        setting.setAuto(Boolean.parseBoolean(prop.getProperty("auto")));
        setting.setRandom(Boolean.parseBoolean(prop.getProperty("random")));

        // close and return
        input.close();
        return setting;
    }
   

}
