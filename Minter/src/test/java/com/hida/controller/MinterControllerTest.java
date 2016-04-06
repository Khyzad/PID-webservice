package com.hida.controller;

import com.hida.service.MinterServiceImpl;
import junit.framework.Assert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.ui.ModelMap;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author lruffin
 */
public class MinterControllerTest {
    
    @Mock
    MinterServiceImpl MinterServiceDao;
    
    @InjectMocks
    MinterController Controller;
    
    @Spy
    ModelMap ModelMap;
    
    @BeforeClass
    public void setUpClass() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testMintPrepend(){
        Assert.fail("unimplemented");
    }
    
    @Test
    public void testBadParameterExceptionTokenType(){
        Assert.fail("unimplemented");
    }
    
    @Test
    public void testBadParameterExceptionBoolean(){
        Assert.fail("unimplemented");
    }
    
}
