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
import com.hida.model.NotEnoughPermutationsException;
import com.hida.model.Pid;
import com.hida.model.PidTest;
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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
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
import org.testng.annotations.BeforeMethod;
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

    private PidTest pidTest_ = new PidTest();

    /**
     * Get default setting values from test properties file
     *
     * @throws Exception
     */
    @BeforeClass
    public void setUpClass() throws Exception {
        defaultSetting_ = this.readPropertiesFile(TEST_FILE);
    }

    /**
     * Refreshes the mocks after each test
     *
     * @throws Exception
     */
    @BeforeMethod
    public void setUpMethod() throws Exception {
        // set up Mockito
        MockitoAnnotations.initMocks(this);

        // return null when any repository attempts to save
        when(pidRepo_.save(any(Pid.class))).thenReturn(null);
        when(usedSettingRepo_.save(any(UsedSetting.class))).thenReturn(null);
        when(defaultSettingRepo_.save(any(DefaultSetting.class))).thenReturn(null);
    }

    @Test
    public void testGeneratePids() {
        // pretend that no newly generated Pids were persisted
        when(pidRepo_.findOne(any(String.class))).thenReturn(null);

        // generate the set of Pids
        int actualAmount = 5;
        Set<Pid> testSet = service_.generatePids(defaultSetting_, actualAmount);

        // test behavior
        Assert.assertEquals(actualAmount, testSet.size());
        verify(pidRepo_, atLeast(actualAmount)).findOne(any(String.class));
    }

    @Test
    public void testGeneratePidsWithStartingValue() {
        // pretend that no newly generated Pids were persisted
        when(pidRepo_.findOne(any(String.class))).thenReturn(null);

        // generate the set of Pids
        int actualAmount = 4;
        Set<Pid> testSet = service_.generatePids(defaultSetting_, actualAmount, 5);

        // test behavior                
        Assert.assertEquals(actualAmount, testSet.size());
        verify(pidRepo_, atLeast(actualAmount)).findOne(any(String.class));

        Iterator<Pid> iter = testSet.iterator();
        while (iter.hasNext()) {
            Pid pid1 = iter.next();
            Pid pid2 = iter.next();
            pidTest_.testOrder(pid1, pid2);
        }
    }

    @Test
    public void testGeneratePidsWithRollOver() {
        // pretend to persist all odd Pids
        int amount = 5;
        for (int i = 0; i < amount; i++) {
            when(pidRepo_.findOne((2 * i + 1) + "")).thenReturn(new Pid());
        }

        // generate the set of Pids
        Set<Pid> testSet = service_.generatePids(defaultSetting_, amount);

        // test behavior
        Assert.assertEquals(amount, testSet.size());
        verify(pidRepo_, atLeast(amount)).findOne(any(String.class));

        // check to see that only Pids with even names are contained
        Iterator<Pid> iter = testSet.iterator();
        while (iter.hasNext()) {
            Pid pid = iter.next();

            boolean isEven = Integer.parseInt(pid.getName()) % 2 == 0;
            Assert.assertEquals(isEven, true, String.format("%s did not rollover", pid));

        }
    }

    @Test
    public void testGeneratePidsWithWrapAround() {
        // pretend that no newly generated Pids were persisted
        int amount = 10;
        when(pidRepo_.findOne(any(String.class))).thenReturn(null);

        // generate the set of Pids
        Set<Pid> testSet = service_.generatePids(defaultSetting_, amount, 5);

        // test behavior and ensure that the set contains the desired amonut
        Assert.assertEquals(amount, testSet.size());
        verify(pidRepo_, atLeast(amount)).findOne(any(String.class));
    }

    @Test
    public void testGetCurrentAmount() {
        UsedSetting usedSetting = this.getSampleUsedSetting();
        
        // find the matching usedSetting
        when(usedSettingRepo_.findUsedSetting(any(String.class),
                any(Token.class),
                any(String.class),
                anyInt(),
                anyBoolean())).thenReturn(usedSetting);
        
        // call the method to test
        service_.getCurrentAmount(defaultSetting_);
        
        // ensure that usedSettingRepo_.find was called once
        verify(usedSettingRepo_, times(1)).findUsedSetting(any(String.class),
                any(Token.class),
                any(String.class),
                anyInt(),
                anyBoolean());
    }

    @Test
    public void testPersistPidsWithNewUsedSettings() {
        // create two identical sets to represent before and after persistPids is called
        Set<Pid> oldSet = getSamplePidSet();
        Set<Pid> set = getSamplePidSet();

        // assume the UsedSetting isn't persisted and pretend to persist it
        when(usedSettingRepo_.findUsedSetting(any(String.class),
                any(Token.class),
                any(String.class),
                anyInt(),
                anyBoolean())).thenReturn(null);

        // copy defaultSetting_ and change the prepend to something other than empty string
        String prepend = "http://";
        DefaultSetting setting = new DefaultSetting(defaultSetting_);
        setting.setPrepend(prepend);
        service_.persistPids(setting, set, set.size());

        // ensure that the size is still the same
        Assert.assertEquals(set.size(), oldSet.size());

        // ensure that save was called exactly size of set times
        verify(pidRepo_, times(oldSet.size())).save(any(Pid.class));

        // ensure that usedSettingRepo_.find was called once
        verify(usedSettingRepo_, times(1)).findUsedSetting(any(String.class),
                any(Token.class),
                any(String.class),
                anyInt(),
                anyBoolean());

        verify(usedSettingRepo_, times(1)).save(any(UsedSetting.class));

        // ensure that prepend was added to the front of the name
        Iterator<Pid> newIter = set.iterator();
        Iterator<Pid> oldIter = oldSet.iterator();
        while (newIter.hasNext()) {
            Pid newPid = newIter.next();
            Pid oldPid = oldIter.next();
            Assert.assertEquals(newPid.getName().startsWith(prepend), true);
            Assert.assertEquals(newPid.getName().substring(prepend.length()), oldPid.getName());
        }
    }

    @Test
    public void testPersistPidsWithOldUsedSettings() {
        // create two identical sets to represent before and after persistPids is called
        Set<Pid> oldSet = getSamplePidSet();
        Set<Pid> set = getSamplePidSet();

        UsedSetting usedSetting = this.getSampleUsedSetting();

        // assume the UsedSetting isn't persisted and pretend to persist it
        when(usedSettingRepo_.findUsedSetting(any(String.class),
                any(Token.class),
                any(String.class),
                anyInt(),
                anyBoolean())).thenReturn(usedSetting);

        // copy defaultSetting_ and change the prepend to something other than empty string
        String prepend = "http://";
        DefaultSetting defaultSetting = new DefaultSetting(defaultSetting_);
        defaultSetting.setPrepend(prepend);
        service_.persistPids(defaultSetting, set, set.size());

        // ensure that the size is still the same
        Assert.assertEquals(set.size(), oldSet.size());

        // ensure that save was called exactly size of set times
        verify(pidRepo_, times(oldSet.size())).save(any(Pid.class));

        // ensure that the amount stored in usedSetting is increased
        Assert.assertEquals(usedSetting.getAmount(), oldSet.size());

        // ensure that prepend was added to the front of the name
        Iterator<Pid> newIter = set.iterator();
        Iterator<Pid> oldIter = oldSet.iterator();
        while (newIter.hasNext()) {
            Pid newPid = newIter.next();
            Pid oldPid = oldIter.next();
            Assert.assertEquals(newPid.getName().startsWith(prepend), true);
            Assert.assertEquals(newPid.getName().substring(prepend.length()), oldPid.getName());
        }
    }

    @Test
    public void testUpdateCurrentSetting() throws Exception {
        DefaultSetting testSetting = new DefaultSetting(defaultSetting_);
        when(defaultSettingRepo_.findCurrentDefaultSetting()).thenReturn(testSetting);

        service_.updateCurrentSetting(testSetting);
        verify(defaultSettingRepo_, times(1)).findCurrentDefaultSetting();
    }   
    
    @Test
    public void testGenerateCache() {
        service_.generateCache(defaultSetting_);
        Assert.assertEquals(service_.getCache().size(), defaultSetting_.getCacheSize());
    }

    @Test
    public void testInitializeStoredSetting() {
        Assert.fail("unimplemented");
    }

    private Set<Pid> getSamplePidSet() {
        Set<Pid> set = new LinkedHashSet<>();
        for (int i = 0; i < 10; i++) {
            set.add(new Pid(i + ""));
        }
        return set;
    }

    private UsedSetting getSampleUsedSetting() {
        UsedSetting setting = new UsedSetting();

        setting.setPrefix(defaultSetting_.getPrefix());
        setting.setTokenType(defaultSetting_.getTokenType());
        setting.setCharMap(defaultSetting_.getCharMap());
        setting.setRootLength(defaultSetting_.getRootLength());
        setting.setSansVowels(defaultSetting_.isSansVowels());
        setting.setAmount(0);

        return setting;
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
