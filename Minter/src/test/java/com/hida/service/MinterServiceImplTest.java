package com.hida.service;

import com.hida.dao.DefaultSettingDao;
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
import static org.testng.Assert.*;

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

    @BeforeClass
    public void setUpClass() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMint() throws BadParameterException {
        Assert.fail("unimplemented");
    }

    /**
     * missing javadoc
     */
    @Test
    public void testGetCurrentSetting() {        
        DefaultSetting defaultSetting = new DefaultSetting("prepend", // prepend
                "prefix", // prefix
                TokenType.DIGIT, // tokentype
                "dddd", // charmap
                3, // rootlength
                true, // isSansVowels
                true, // isAuto
                true);  // isRandom
        
        when(DefaultSettingDao.getDefaultSetting()).thenReturn(defaultSetting);        
        Assert.assertEquals(MinterServiceImpl.getCurrentSetting(), defaultSetting);        
    }

    /**
     * missing javadoc
     */
    @Test
    public void updateCurrentSetting() {   
        DefaultSetting defaultSetting = new DefaultSetting("prepend", // prepend
                "prefix", // prefix
                TokenType.DIGIT, // tokentype
                "dddd", // charmap
                3, // rootlength
                true, // isSansVowels
                true, // isAuto
                true);  // isRandom
        
        when(DefaultSettingDao.getDefaultSetting()).thenReturn(defaultSetting);  
        
        MinterServiceImpl.updateCurrentSetting(defaultSetting);
        verify(DefaultSettingDao, atLeastOnce()).getDefaultSetting();
    }

}
