package com.hida.service;

import com.hida.dao.DefaultSettingDao;
import com.hida.dao.PidDao;
import com.hida.dao.UsedSettingDao;
import com.hida.model.BadParameterException;
import com.hida.model.DefaultSetting;
import com.hida.model.NotEnoughPermutationsException;
import com.hida.model.Pid;
import com.hida.model.TokenType;
import com.hida.model.UsedSetting;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * missing javadoc
 *
 * @author lruffin
 */
public class MinterServiceImplTest {

    @Mock
    DefaultSettingDao DefaultSettingDao;

    @Mock
    PidDao PidDao;

    @Mock
    UsedSettingDao UsedSettingDao;

    @InjectMocks
    MinterServiceImpl MinterServiceImpl;

    ArrayList<DefaultSetting> DefaultSettingList = new ArrayList<>();

    Set<Pid> PidSet = new TreeSet<>();

    /**
     * missing javadoc
     *
     * @throws Exception
     */
    @BeforeClass
    public void setUpClass() throws Exception {
        MockitoAnnotations.initMocks(this);
        initializeDefaultSetting();
        initializePidSet();
    }

    /**
     *
     * @return
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
     * missing javadoc
     *
     * @param isRandom
     * @param isAuto
     */
    @Test(dataProvider = "mintSettings")
    public void testMintWithNewUsedSetting(boolean isRandom, boolean isAuto) {
        DefaultSetting defaultSetting = DefaultSettingList.get(1);
        defaultSetting.setAuto(isAuto);
        defaultSetting.setRandom(isRandom);

        when(PidDao.findByName(any(String.class))).thenReturn(null);
        doNothing().when(PidDao).savePid(any(Pid.class));
        when(UsedSettingDao.findUsedSetting(any(UsedSetting.class))).thenReturn(null); 
        doNothing().when(UsedSettingDao).save(any(UsedSetting.class));

        Set<Pid> testSet = MinterServiceImpl.mint(10, defaultSetting);
        boolean containsAll = testSet.containsAll(PidSet);
        Assert.assertEquals(containsAll, true);
    }

    /**
     * missing javadoc
     *
     * @param isAuto
     * @param isRandom
     */
    @Test(dataProvider = "mintSettings")
    public void testMintWithOldUsedSetting(boolean isAuto, boolean isRandom) {
        DefaultSetting defaultSetting = DefaultSettingList.get(1);
        defaultSetting.setAuto(isAuto);
        defaultSetting.setRandom(isRandom);

        when(PidDao.findByName(any(String.class))).thenReturn(null);
        doNothing().when(PidDao).savePid(any(Pid.class));
        when(UsedSettingDao.findUsedSetting(any(UsedSetting.class))).thenReturn(null); 
        
        doNothing().when(UsedSettingDao).save(any(UsedSetting.class));
        doNothing().when(UsedSettingDao).save(any(UsedSetting.class));

        Set<Pid> testSet = MinterServiceImpl.mint(5, defaultSetting);
        boolean containsAll = PidSet.containsAll(testSet);
        Assert.assertEquals(containsAll, true);
    }

    /**
     * missing javadoc
     *
     * @param isAuto
     * @param isRandom
     */
    @Test(expectedExceptions = NotEnoughPermutationsException.class, dataProvider = "mintSettings")
    public void testMintNotEnoughPermutationsExceptionInFindUsedSetting(
            boolean isAuto, boolean isRandom) {
        DefaultSetting defaultSetting = DefaultSettingList.get(1);
        defaultSetting.setAuto(isAuto);
        defaultSetting.setRandom(isRandom);

        UsedSetting usedSetting = new UsedSetting("", // prefix
                TokenType.DIGIT, // tokentype
                "d", // charmap
                1, // rootlength
                true, //sans vowels
                5); // amount

        when(PidDao.findByName(any(String.class))).thenReturn(null);
        doNothing().when(PidDao).savePid(any(Pid.class));
        when(UsedSettingDao.findUsedSetting(any(UsedSetting.class))).thenReturn(usedSetting);
        when(UsedSettingDao.findUsedSettingById(anyInt())).thenReturn(usedSetting);
        doNothing().when(UsedSettingDao).save(any(UsedSetting.class));

        Set<Pid> testSet = MinterServiceImpl.mint(6, defaultSetting);
    }

    /**
     * missing javadoc
     *
     * @param isAuto
     * @param isRandom
     */
    @Test(expectedExceptions = NotEnoughPermutationsException.class, dataProvider = "mintSettings")
    public void testMintNotEnoughPermutationsExceptionInCalculatePermutations(
            boolean isAuto, boolean isRandom) {
        DefaultSetting defaultSetting = DefaultSettingList.get(1);
        defaultSetting.setAuto(isAuto);
        defaultSetting.setRandom(isRandom);
        
        UsedSetting usedSetting = new UsedSetting("", // prefix
                TokenType.DIGIT, // tokentype
                "d", // charmap
                1, // rootlength
                true, //sans vowels
                0); // amount

        when(PidDao.findByName(any(String.class))).thenReturn(null);
        doNothing().when(PidDao).savePid(any(Pid.class));
        when(UsedSettingDao.findUsedSetting(usedSetting)).thenReturn(usedSetting);
        doNothing().when(UsedSettingDao).save(any(UsedSetting.class));

        Set<Pid> testSet = MinterServiceImpl.mint(11, defaultSetting);
    }

    /**
     * missing javadoc
     *
     * @param isAuto
     * @param isRandom
     */
    @Test(expectedExceptions = NotEnoughPermutationsException.class, dataProvider = "mintSettings")
    public void testMintNotEnoughPermutationExceptionInRollId(boolean isAuto, boolean isRandom) {
        DefaultSetting defaultSetting = DefaultSettingList.get(1);
        defaultSetting.setAuto(isAuto);
        defaultSetting.setRandom(isRandom);
        
        UsedSetting usedSetting = new UsedSetting("", // prefix
                TokenType.DIGIT, // tokentype
                "d", // charmap
                1, // rootlength
                true, //sans vowels
                0); // amount

        when(PidDao.findByName("0")).thenReturn(new TestPid(0));
        doNothing().when(PidDao).savePid(any(Pid.class));
        when(UsedSettingDao.findUsedSetting(usedSetting)).thenReturn(usedSetting);
        doNothing().when(UsedSettingDao).save(any(UsedSetting.class));

        Set<Pid> testSet = MinterServiceImpl.mint(10, defaultSetting);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testGetCurrentSettingWithExistingDefaultSetting() {
        DefaultSetting defaultSetting = DefaultSettingList.get(0);
        when(DefaultSettingDao.getDefaultSetting()).thenReturn(defaultSetting);
        Assert.assertEquals(MinterServiceImpl.getCurrentSetting(), defaultSetting);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testGetCurrentSettingWithoutExistingDefaultSetting() {
        DefaultSetting defaultSetting = DefaultSettingList.get(0);
        when(DefaultSettingDao.getDefaultSetting()).thenReturn(null);
        DefaultSetting actualSetting = MinterServiceImpl.getCurrentSetting();

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
     * missing javadoc
     */
    @Test
    public void testUpdateCurrentSetting() {
        DefaultSetting defaultSetting = DefaultSettingList.get(0);
        when(DefaultSettingDao.getDefaultSetting()).thenReturn(defaultSetting);

        MinterServiceImpl.updateCurrentSetting(defaultSetting);
        verify(DefaultSettingDao, atLeastOnce()).getDefaultSetting();
    }

    /**
     * missing javadoc
     */
    private void initializeDefaultSetting() {
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
     * missing javadoc
     */
    private void initializePidSet() {
        for (int i = 0; i < 10; i++) {
            PidSet.add(new TestPid(i));
        }
    }

    /**
     * missing javadoc
     */
    private class TestPid extends Pid {

        public TestPid(int n) {
            BaseMap = new int[1];
            BaseMap[0] = n;
        }

        @Override
        public boolean incrementId() {
            throw new UnsupportedOperationException("Not supported yet.");
        }       
    }
}
