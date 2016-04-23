package com.hida.service;

import com.hida.dao.PurlDao;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

/**
 * This class tests the functionality of MinterServiceImpl using Mockito.
 *
 * @author lruffin
 */
public class ResolverServiceImplTest {

    @Autowired
    private PurlDao Dao;

    @InjectMocks
    private ResolverService Service;
    
    /**
     * Sets up Mockito
     *
     * @throws Exception
     */
    @BeforeClass
    public void setUpClass() throws Exception {
        MockitoAnnotations.initMocks(this);        
    }

    @Test
    public void testRetrieveURL() {
        Assert.fail("unimplemented");
    }

    @Test
    public void editURL() {
        Assert.fail("unimplemented");
    }

    @Test
    public void deletePURL() {
        Assert.fail("unimplemented");
    }

    @Test
    public void retrieveModel() {
        Assert.fail("unimplemented");
    }

    @Test
    public void insertPURL() {
        Assert.fail("unimplemented");
    }

}
