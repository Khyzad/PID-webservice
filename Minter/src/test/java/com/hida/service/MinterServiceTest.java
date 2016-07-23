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
import com.hida.model.DefaultSetting;
import com.hida.model.NotEnoughPermutationsException;
import com.hida.model.Pid;
import com.hida.model.Token;
import com.hida.model.UsedSetting;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * This class tests the functionality of MinterService using Mockito.
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
public class MinterServiceTest extends AbstractTestNGSpringContextTests {

    /**
     * Default setting values stored in resources folder
     */
    private final String TEST_FILE = "testDefaultSetting.properties";

    @Mock
    private DefaultSettingRepository defaultSettingRepo_;

    @Mock
    private GeneratorService genService_;

    @Mock
    private UsedSettingRepository usedSettingRepo_;

    @InjectMocks
    private MinterService minterService_;

    /**
     * Sets up Mockito
     *
     * @throws Exception
     */
    @BeforeClass
    public void setUpClass() throws Exception {
        MockitoAnnotations.initMocks(this);
        minterService_.setDefaultSettingPath(TEST_FILE);
        minterService_.initializeStoredSetting();
    }

    /**
     * Test the various mint settings (auto/random and random/sequential)
     *
     * @return An array of values
     */
    @DataProvider(name = "mintSettings")
    public Object[][] mintSettings() {
        return new Object[][]{
            {true, true},
            {true, false},
            {false, true},
            {false, false}
        };
    }

    /**
     * Tests the MinterService by assuming that the settings aren't currently
     * stored in the database
     */
    @Test
    public void testMintWithNewUsedSetting() throws Exception {
        Set<Pid> sampleSet = getSampleSet();
        when(genService_.generatePids(any(DefaultSetting.class), anyLong(), anyLong()))
                .thenReturn(sampleSet);

        // don't do anything when a save attempt has been made
        setSaveBehavior();
        setFindUsedSettingBehavior(null);

        // start behavior
        minterService_.mint(sampleSet.size(), sampleDefaultSetting());

        // verify that save attempts have been made
        verify(genService_, atLeast(sampleSet.size())).savePid(any(Pid.class));
        verify(usedSettingRepo_, times(1)).save(any(UsedSetting.class));
        verify(usedSettingRepo_, times(1)).findUsedSetting(any(String.class),
                any(Token.class),
                any(String.class),
                anyInt(),
                anyBoolean());
    }

    /**
     * Tests the MinterService under the scenario where UsedSetting entity with
     * matching parameters already exist.     
     */
    @Test
    public void testMintWithOldUsedSetting() throws Exception {
        Set<Pid> sampleSet = getSampleSet();
        UsedSetting sampleUsedSetting = getSampleUsedSetting();
        when(genService_.generatePids(any(DefaultSetting.class), anyLong(), anyLong()))
                .thenReturn(sampleSet);
        long oldAmount = sampleUsedSetting.getAmount();

        // don't do anything when a save attempt has been made
        setSaveBehavior();
        setFindUsedSettingBehavior(sampleUsedSetting);

        // start behavior
        minterService_.mint(sampleSet.size(), sampleDefaultSetting());

        // verify that save attempts have been made
        verify(genService_, atLeast(sampleSet.size())).savePid(any(Pid.class));
        verify(usedSettingRepo_, times(1)).findUsedSetting(any(String.class),
                any(Token.class),
                any(String.class),
                anyInt(),
                anyBoolean());
        
        Assert.assertEquals(sampleUsedSetting.getAmount(), oldAmount + sampleSet.size());
    }

    /**
     * Tests to ensure that the whenever the stored default setting is used then
     * the requested amount is saved and used as a starting value.
     */
    @Test
    public void testMintWithStartingValue() throws Exception {
        // retrieve a sample DefaultSetting entity
        DefaultSetting testSetting = this.sampleDefaultSetting();

        // get a sample UsedSetting entity
        UsedSetting usedSetting = getSampleUsedSetting();

        // assume that any Pids created aren't already persisted and pretend to persist them
        when(genService_.findOne(any(String.class))).thenReturn(null);
        when(genService_.save(any(Pid.class))).thenReturn(null);

        // assume the UsedSetting isn't persisted and pretend to persist it        
        when(usedSettingRepo_.findUsedSetting(any(String.class),
                any(Token.class),
                any(String.class),
                anyInt(),
                anyBoolean())).thenReturn(usedSetting);
        when(usedSettingRepo_.save(any(UsedSetting.class))).thenReturn(null);

        // check to see if all the Pids were created
        long amount = 5;
        long lastAmount = minterService_.getLastSequentialAmount();
        Set<Pid> testSet = minterService_.mint(amount, testSetting);

        Assert.assertEquals(minterService_.getLastSequentialAmount(), (lastAmount + amount) % 10);
    }

    /**
     * Tests the MinterService to ensure that a NotEnoughPermutationsException
     * is thrown whenever the amount retrieved from FindUsedSetting is less than
     * the requested amount.
     *
     * @param isRandom Determines if the PIDs are created randomly or
     * sequentially
     * @param isAuto Determines which generator, either Auto or Custom, will be
     * used
     */
    @Test(expectedExceptions = NotEnoughPermutationsException.class, dataProvider = "mintSettings")
    public void testMintNotEnoughPermutationsExceptionInFindUsedSetting(
            boolean isAuto, boolean isRandom) throws Exception {
        // retrieve a sample DefaultSetting entity
        DefaultSetting testSetting = this.sampleDefaultSetting();
        testSetting.setAuto(isAuto);
        testSetting.setRandom(isRandom);

        // get a sample UsedSetting entity
        UsedSetting usedSetting = getSampleUsedSetting();

        // assume that any Pids created aren't already persisted and pretend to persist them
        when(genService_.findOne(any(String.class))).thenReturn(null);
        when(genService_.save(any(Pid.class))).thenReturn(null);

        // pretend to find and retrieve variable usedSetting
        when(usedSettingRepo_.findUsedSetting(any(String.class),
                any(Token.class),
                any(String.class),
                anyInt(),
                anyBoolean())).thenReturn(usedSetting);
        when(usedSettingRepo_.findOne(anyInt())).thenReturn(usedSetting);

        // try to mint an amount greater than what is available
        Set<Pid> testSet = minterService_.mint(6, testSetting);
    }

    /**
     * Tests the MinterService to ensure that a NotEnoughPermutationsException
     * is thrown whenever the requested amount of Pids to mint exceeds the
     * possible number of permutations.
     *
     * @param isRandom Determines if the PIDs are created randomly or
     * sequentially
     * @param isAuto Determines which generator, either Auto or Custom, will be
     * used
     */
    @Test(expectedExceptions = NotEnoughPermutationsException.class, dataProvider = "mintSettings")
    public void testMintNotEnoughPermutationsExceptionInCalculatePermutations(
            boolean isAuto, boolean isRandom) throws Exception {
        // retrieve a sample DefaultSetting entity
        DefaultSetting testSetting = this.sampleDefaultSetting();
        testSetting.setAuto(isAuto);
        testSetting.setRandom(isRandom);

        // assume that any Pids created aren't already persisted and pretend to persist them
        when(genService_.findOne(any(String.class))).thenReturn(null);
        when(genService_.save(any(Pid.class))).thenReturn(null);

        // assume that UsedSetting entity with the relevant parameters does not exist
        when(usedSettingRepo_.findUsedSetting(any(String.class),
                any(Token.class),
                any(String.class),
                anyInt(),
                anyBoolean())).thenReturn(null);
        when(genService_.save(any(Pid.class))).thenReturn(null);

        // try to mint an amount greater than what is possible
        Set<Pid> testSet = minterService_.mint(11, testSetting);
    }

    /**
     * Tests the MinterService to ensure that a NotEnoughPermutationsException
     * is thrown whenever it is no longer possible to 'roll' Pids. This is
     * important because there may be different settings that may have created
     * Pids that could match the fields of the currently used setting.
     *
     * @param isRandom Determines if the PIDs are created randomly or
     * sequentially
     * @param isAuto Determines which generator, either Auto or Custom, will be
     * used
     */
    @Test(expectedExceptions = NotEnoughPermutationsException.class, dataProvider = "mintSettings")
    public void testMintNotEnoughPermutationExceptionInRollId(boolean isAuto, boolean isRandom)
            throws Exception {
        // retrieve a sample DefaultSetting entity
        DefaultSetting testSetting = this.sampleDefaultSetting();
        testSetting.setAuto(isAuto);
        testSetting.setRandom(isRandom);

        // pretend any Pid with the name "0" is the only Pid that exists
        Pid sentinelPid = new Pid("1");
        when(genService_.findOne(any(String.class))).thenReturn(sentinelPid);
        when(genService_.findOne("0")).thenReturn(null);

        // assume that UsedSetting entity with the relevant parameters does not exist
        when(usedSettingRepo_.findUsedSetting(any(String.class),
                any(Token.class),
                any(String.class),
                anyInt(),
                anyBoolean())).thenReturn(null);
        when(usedSettingRepo_.save(any(UsedSetting.class))).thenReturn(null);

        // try to mint an amount greater than what is possible
        Set<Pid> testSet = minterService_.mint(10, testSetting);
    }

    /**
     * Test in MinterService that ensures that the CurrentSetting is sought
     * after.
     */
    @Test
    public void testInitializeStoredSetting() throws Exception {
        DefaultSetting testSetting = sampleDefaultSetting();
        when(defaultSettingRepo_.findCurrentDefaultSetting()).thenReturn(testSetting);

        minterService_.initializeStoredSetting();
        verify(defaultSettingRepo_, atLeastOnce()).findCurrentDefaultSetting();
    }

    /**
     * Test in MinterService that ensures that the CurrentSetting is sought
     * after and if it does not exist, a new DefaultSetting is created and
     * saved.
     */
    @Test
    public void testGetCurrentSettingWithoutExistingDefaultSetting() throws Exception {
        DefaultSetting testSetting = sampleDefaultSetting();
        when(defaultSettingRepo_.findCurrentDefaultSetting()).thenReturn(null);
        DefaultSetting actualSetting = minterService_.getStoredSetting();

        Assert.assertEquals(actualSetting.getCharMap(), testSetting.getCharMap());
        Assert.assertEquals(actualSetting.getPrefix(), testSetting.getPrefix());
        Assert.assertEquals(actualSetting.getPrepend(), testSetting.getPrepend());
        Assert.assertEquals(actualSetting.getRootLength(), testSetting.getRootLength());
        Assert.assertEquals(actualSetting.getTokenType(), testSetting.getTokenType());
        Assert.assertEquals(actualSetting.isAuto(), testSetting.isAuto());
        Assert.assertEquals(actualSetting.isRandom(), testSetting.isRandom());
        Assert.assertEquals(actualSetting.isSansVowels(), testSetting.isSansVowels());
    }

    /**
     * Test in MinterService that checks if CurrentSetting in MinterService is
     * being properly updated.
     */
    @Test
    public void testUpdateCurrentSetting() throws Exception {
        DefaultSetting testSetting = sampleDefaultSetting();
        when(defaultSettingRepo_.findCurrentDefaultSetting()).thenReturn(testSetting);

        minterService_.updateCurrentSetting(testSetting);
        verify(defaultSettingRepo_, atLeastOnce()).findCurrentDefaultSetting();
    }

    /**
     * Create a test Default Setting object
     */
    private DefaultSetting sampleDefaultSetting() {
        return new DefaultSetting("", // prepend
                "", // prefix
                500, // cacheSize
                Token.DIGIT, // token type
                "d", // charmap
                1, // rootlength
                true, // sans vowel
                true, // is auto
                false); // is random
    }

    /**
     * Return a sample UsedSetting
     *
     * @return
     */
    private UsedSetting getSampleUsedSetting() {
        return new UsedSetting("", // prefix
                Token.DIGIT, // tokentype
                "d", // charmap
                1, // rootlength
                true, //sans vowels
                5); // amount
    }

    private Set<Pid> getSampleSet() {
        Set<Pid> set = new LinkedHashSet<>();

        for (int i = 0; i < 10; i++) {
            set.add(new Pid(i + ""));
        }

        return set;
    }
        
    private void setSaveBehavior(){
        doNothing().when(genService_).savePid(any(Pid.class));        
        when(usedSettingRepo_.save(any(UsedSetting.class))).thenReturn(null);
        when(defaultSettingRepo_.save(any(DefaultSetting.class))).thenReturn(null);
    }
    
    private void setFindUsedSettingBehavior(UsedSetting setting){
        when(usedSettingRepo_.findUsedSetting(any(String.class),
                any(Token.class),
                any(String.class),
                anyInt(),
                anyBoolean())).thenReturn(setting);
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
