package com.hida.service;

import com.hida.model.BadParameterException;
import com.hida.model.DefaultSetting;
import com.hida.model.Pid;
import org.mockito.Mock;
import org.mockito.InjectMocks;
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
    MinterService MinterServiceDao;

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

    @Test
    public void testGetCurrentSetting() {
        Assert.fail("unimplemented");
    }

    @Test
    public void updateCurrentSetting() {
        Assert.fail("unimplemented");
    }

}
