package com.hida.service;

import com.hida.dao.DefaultSettingDao;
import com.hida.dao.PidDao;
import com.hida.dao.UsedSettingDao;
import com.hida.model.BadParameterException;
import com.hida.model.DefaultSetting;
import com.hida.model.Pid;
import com.hida.model.TokenType;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author lruffin
 */
public class MinterServiceImplTest {

    @Mock
    DefaultSettingDao DefaultSettingDao;

    @InjectMocks
    MinterServiceImpl MinterServiceImpl;

    DefaultSetting DefaultSetting;

    @BeforeClass
    public void setUpClass() throws Exception {
        MockitoAnnotations.initMocks(this);
        initializeDefaultSetting();
    }

    @Test
    public void testMint() throws BadParameterException {
        Assert.fail("unimplemented");
    }

    /**
     * missing javadoc
     */
    @Test
    public void testGetCurrentSettingWithExistingDefaultSetting() {
        when(DefaultSettingDao.getDefaultSetting()).thenReturn(DefaultSetting);
        Assert.assertEquals(MinterServiceImpl.getCurrentSetting(), DefaultSetting);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testGetCurrentSettingWithoutExistingDefaultSetting() {
        when(DefaultSettingDao.getDefaultSetting()).thenReturn(null);
        DefaultSetting actualSetting = MinterServiceImpl.getCurrentSetting();
        
        Assert.assertEquals(actualSetting.getCharMap(), DefaultSetting.getCharMap());
        Assert.assertEquals(actualSetting.getPrefix(), DefaultSetting.getPrefix());
        Assert.assertEquals(actualSetting.getPrepend(), DefaultSetting.getPrepend());
        Assert.assertEquals(actualSetting.getRootLength(), DefaultSetting.getRootLength());
        Assert.assertEquals(actualSetting.getTokenType(), DefaultSetting.getTokenType());
        Assert.assertEquals(actualSetting.isAuto(), DefaultSetting.isAuto());
        Assert.assertEquals(actualSetting.isRandom(), DefaultSetting.isRandom());
        Assert.assertEquals(actualSetting.isSansVowels(), DefaultSetting.isSansVowels());        
    }

    /**
     * missing javadoc
     */
    @Test
    public void testUpdateCurrentSetting() {
        when(DefaultSettingDao.getDefaultSetting()).thenReturn(DefaultSetting);

        MinterServiceImpl.updateCurrentSetting(DefaultSetting);
        verify(DefaultSettingDao, atLeastOnce()).getDefaultSetting();
    }

    /**
     * missing javadoc
     */
    private void initializeDefaultSetting() {
        DefaultSetting = new DefaultSetting("", // prepend
                "", // prefix
                TokenType.DIGIT, // token type
                "ddddd", // charmap
                5, // rootlength
                true, // sans vowel
                true, // is auto
                true); // is random
    }

}
