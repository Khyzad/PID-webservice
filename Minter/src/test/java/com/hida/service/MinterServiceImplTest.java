package com.hida.service;

import com.hida.dao.DefaultSettingDao;
import com.hida.dao.PidDao;
import com.hida.dao.UsedSettingDao;
import com.hida.model.BadParameterException;
import com.hida.model.DefaultSetting;
import com.hida.model.Pid;
import com.hida.model.TokenType;
import com.hida.model.UsedSetting;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;

import org.testng.annotations.BeforeClass;
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
     * missing javadoc
     *
     * @throws BadParameterException
     */
    @Test
    public void testMintWithNewUsedSetting() throws BadParameterException {
        DefaultSetting defaultSetting = DefaultSettingList.get(1);

        when(PidDao.findByName(any(String.class))).thenReturn(null);
        doNothing().when(PidDao).savePid(any(Pid.class));
        when(UsedSettingDao.findUsedSettingById(anyInt())).thenReturn(null);
        doNothing().when(UsedSettingDao).save(any(UsedSetting.class));

        Set<Pid> testSet = MinterServiceImpl.mint(10, defaultSetting);
        boolean containsAll = testSet.containsAll(PidSet);
        Assert.assertEquals(containsAll, true);

    }

    /**
     * missing javadoc
     *
     * @throws BadParameterException
     */
    @Test
    public void testMintWithOldUsedSetting() throws BadParameterException {
        Assert.fail("unimplemented");
    }

    /**
     * missing javadoc
     *
     * @throws BadParameterException
     */
    @Test
    public void testMintBadParameterException() throws BadParameterException {
        Assert.fail("unimplemented");
    }

    /**
     * missing javadoc
     *
     * @throws BadParameterException
     */
    @Test
    public void testMintNotEnoughPermutationException() throws BadParameterException {
        Assert.fail("unimplemented");
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

        @Override
        public String getRootName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
}
