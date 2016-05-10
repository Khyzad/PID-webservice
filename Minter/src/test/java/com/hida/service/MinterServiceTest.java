package com.hida.service;

import com.hida.model.AutoIdGenerator;
import com.hida.repositories.DefaultSettingRepository;
import com.hida.repositories.PidRepository;
import com.hida.repositories.UsedSettingRepository;
import com.hida.model.DefaultSetting;
import com.hida.model.NotEnoughPermutationsException;
import com.hida.model.Pid;
import com.hida.model.TokenType;
import com.hida.model.UsedSetting;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * This class tests the functionality of MinterService using Mockito.
 *
 * @author lruffin
 */
public class MinterServiceTest {
    
    /**
     * Default setting values stored in resources folder
     */
    private final String TEST_READ_PATH = "src/test/resources/testReadDefaultSetting.properties";
    private final String TEST_WRITE_PATH = "src/test/resources/testWriteDefaultSetting.properties";

    @Mock
    private DefaultSettingRepository DefaultSettingRepo;

    @Mock
    private PidRepository PidRepo;

    @Mock
    private UsedSettingRepository UsedSettingRepo;

    @InjectMocks
    private MinterService MinterService;

    private final ArrayList<DefaultSetting> DefaultSettingList = new ArrayList<>();

    private Set<Pid> PidSet = new TreeSet<>();

    /**
     * Sets up Mockito
     *
     * @throws Exception
     */
    @BeforeClass
    public void setUpClass() throws Exception {
        MockitoAnnotations.initMocks(this);
        initializeDefaultSettingList();
        initializePidSet();
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
     *
     * @param isRandom Determines if the PIDs are created randomly or
     * sequentially
     * @param isAuto Determines which generator, either Auto or Custom, will be
     * used
     */
    @Test(dataProvider = "mintSettings")
    public void testMintWithNewUsedSetting(boolean isRandom, boolean isAuto) {
        // retrieve a sample DefaultSetting entity
        DefaultSetting defaultSetting = DefaultSettingList.get(1);
        defaultSetting.setAuto(isAuto);
        defaultSetting.setRandom(isRandom);

        // assume that any Pids created aren't already persisted and pretend to persist them
        when(PidRepo.findOne(any(String.class))).thenReturn(null);
        when(PidRepo.save(any(Pid.class))).thenReturn(null);

        // assume the UsedSetting isn't persisted and pretend to persist it
        when(UsedSettingRepo.findUsedSetting(any(String.class),
                any(TokenType.class),
                any(String.class),
                anyInt(),
                anyBoolean())).thenReturn(null);

        when(UsedSettingRepo.save(any(UsedSetting.class))).thenReturn(null);

        // check to see if all the Pids were created
        Set<Pid> testSet = MinterService.mint(10, defaultSetting);
        boolean containsAll = testSet.containsAll(PidSet);
        Assert.assertEquals(containsAll, true);
    }

    /**
     * Tests the MinterService under the scenario where UsedSetting entity with
     * matching parameters already exist.
     *
     * @param isRandom Determines if the PIDs are created randomly or
     * sequentially
     * @param isAuto Determines which generator, either Auto or Custom, will be
     * used
     */
    @Test(dataProvider = "mintSettings")
    public void testMintWithOldUsedSetting(boolean isAuto, boolean isRandom) {
        // retrieve a sample DefaultSetting entity
        DefaultSetting defaultSetting = DefaultSettingList.get(1);
        defaultSetting.setAuto(isAuto);
        defaultSetting.setRandom(isRandom);

        // get a sample UsedSetting entity
        UsedSetting usedSetting = getSampleUsedSetting();

        // assume that any Pids created aren't already persisted and pretend to persist them
        when(PidRepo.findOne(any(String.class))).thenReturn(null);
        when(PidRepo.save(any(Pid.class))).thenReturn(null);

        // assume the UsedSetting isn't persisted and pretend to persist it        
        when(UsedSettingRepo.findUsedSetting(any(String.class),
                any(TokenType.class),
                any(String.class),
                anyInt(),
                anyBoolean())).thenReturn(usedSetting);
        when(UsedSettingRepo.save(any(UsedSetting.class))).thenReturn(null);

        // check to see if all the Pids were created
        Set<Pid> testSet = MinterService.mint(5, defaultSetting);
        boolean containsAll = PidSet.containsAll(testSet);
        Assert.assertEquals(containsAll, true);
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
            boolean isAuto, boolean isRandom) {
        // retrieve a sample DefaultSetting entity
        DefaultSetting defaultSetting = DefaultSettingList.get(1);
        defaultSetting.setAuto(isAuto);
        defaultSetting.setRandom(isRandom);

        // get a sample UsedSetting entity
        UsedSetting usedSetting = getSampleUsedSetting();

        // assume that any Pids created aren't already persisted and pretend to persist them
        when(PidRepo.findOne(any(String.class))).thenReturn(null);
        when(PidRepo.save(any(Pid.class))).thenReturn(null);

        // pretend to find and retrieve variable usedSetting
        when(UsedSettingRepo.findUsedSetting(any(String.class),
                any(TokenType.class),
                any(String.class),
                anyInt(),
                anyBoolean())).thenReturn(usedSetting);
        when(UsedSettingRepo.findOne(anyInt())).thenReturn(usedSetting);

        // try to mint an amount greater than what is available
        Set<Pid> testSet = MinterService.mint(6, defaultSetting);
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
            boolean isAuto, boolean isRandom) {
        // retrieve a sample DefaultSetting entity
        DefaultSetting defaultSetting = DefaultSettingList.get(1);
        defaultSetting.setAuto(isAuto);
        defaultSetting.setRandom(isRandom);

        // assume that any Pids created aren't already persisted and pretend to persist them
        when(PidRepo.findOne(any(String.class))).thenReturn(null);
        when(PidRepo.save(any(Pid.class))).thenReturn(null);

        // assume that UsedSetting entity with the relevant parameters does not exist
        when(UsedSettingRepo.findUsedSetting(any(String.class),
                any(TokenType.class),
                any(String.class),
                anyInt(),
                anyBoolean())).thenReturn(null);
        when(PidRepo.save(any(Pid.class))).thenReturn(null);

        // try to mint an amount greater than what is possible
        Set<Pid> testSet = MinterService.mint(11, defaultSetting);
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
    public void testMintNotEnoughPermutationExceptionInRollId(boolean isAuto, boolean isRandom) {
        // retrieve a sample DefaultSetting entity
        DefaultSetting defaultSetting = DefaultSettingList.get(1);
        defaultSetting.setAuto(isAuto);
        defaultSetting.setRandom(isRandom);

        // pretend any Pid with the name "0" is the only Pid that exists
        Iterator<Pid> iter = PidSet.iterator();
        Pid id = iter.next();
        when(PidRepo.findOne(any(String.class))).thenReturn(null);
        when(PidRepo.findOne("0")).thenReturn(id);
        when(PidRepo.save(any(Pid.class))).thenReturn(null);

        // assume that UsedSetting entity with the relevant parameters does not exist
        when(UsedSettingRepo.findUsedSetting(any(String.class),
                any(TokenType.class),
                any(String.class),
                anyInt(),
                anyBoolean())).thenReturn(null);
        when(UsedSettingRepo.save(any(UsedSetting.class))).thenReturn(null);

        // try to mint an amount greater than what is possible
        Set<Pid> testSet = MinterService.mint(10, defaultSetting);
    }

    /**
     * Test in MinterService that ensures that the CurrentSetting is sought
     * after.
     */
    @Test
    public void testGetCurrentSettingWithExistingDefaultSetting() throws Exception {
        DefaultSetting defaultSetting = DefaultSettingList.get(0);
        when(DefaultSettingRepo.findCurrentDefaultSetting()).thenReturn(defaultSetting);

        MinterService.getCurrentSetting(this.TEST_READ_PATH);
        verify(DefaultSettingRepo, atLeastOnce()).findCurrentDefaultSetting();
    }

    /**
     * Test in MinterService that ensures that the CurrentSetting is sought
     * after and if it does not exist, a new DefaultSetting is created and
     * saved.
     */
    @Test
    public void testGetCurrentSettingWithoutExistingDefaultSetting() throws Exception {
        DefaultSetting defaultSetting = DefaultSettingList.get(0);
        when(DefaultSettingRepo.findCurrentDefaultSetting()).thenReturn(null);
        DefaultSetting actualSetting = MinterService.getCurrentSetting(this.TEST_READ_PATH);

        Assert.assertEquals(actualSetting.getCharMap(), defaultSetting.getCharMap());
        Assert.assertEquals(actualSetting.getPrefix(), defaultSetting.getPrefix());
        Assert.assertEquals(actualSetting.getPrepend(), defaultSetting.getPrepend());
        Assert.assertEquals(actualSetting.getRootLength(), defaultSetting.getRootLength());
        Assert.assertEquals(actualSetting.getTokenType(), defaultSetting.getTokenType());
        Assert.assertEquals(actualSetting.isAuto(), defaultSetting.isAuto());
        Assert.assertEquals(actualSetting.isRandom(), defaultSetting.isRandom());
        Assert.assertEquals(actualSetting.isSansVowels(), defaultSetting.isSansVowels());
    }

    /**
     * Test in MinterService that checks if CurrentSetting in MinterService is
     * being properly updated.
     */
    @Test
    public void testUpdateCurrentSetting() throws Exception {
        DefaultSetting defaultSetting = DefaultSettingList.get(0);
        when(DefaultSettingRepo.findCurrentDefaultSetting()).thenReturn(defaultSetting);

        MinterService.updateCurrentSetting(this.TEST_WRITE_PATH, defaultSetting);
        verify(DefaultSettingRepo, atLeastOnce()).findCurrentDefaultSetting();
    }

    /**
     * Create a list of sample DefaultSettingList
     */
    private void initializeDefaultSettingList() {
        DefaultSetting defaultSetting1 = new DefaultSetting("", // prepend
                "", // prefix
                TokenType.DIGIT, // token type
                "ddddd", // charmap
                5, // rootlength
                true, // sans vowel
                true, // is auto
                true); // is random

        DefaultSettingList.add(defaultSetting1);

        DefaultSetting defaultSetting2 = new DefaultSetting("", // prepend
                "", // prefix
                TokenType.DIGIT, // token type
                "d", // charmap
                1, // rootlength
                true, // sans vowel
                true, // is auto
                false); // is random

        DefaultSettingList.add(defaultSetting2);
    }

    /**
     * Create a sample set of Pid
     */
    private void initializePidSet() {
        AutoIdGenerator gen = new AutoIdGenerator("", true, TokenType.DIGIT, 1);
        PidSet = gen.sequentialMint(10);        
    }

    /**
     * Return a sample UsedSetting
     *
     * @return
     */
    private UsedSetting getSampleUsedSetting() {
        return new UsedSetting("", // prefix
                TokenType.DIGIT, // tokentype
                "d", // charmap
                1, // rootlength
                true, //sans vowels
                5); // amount
    }

    
}
