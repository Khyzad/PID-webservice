package com.hida.service;

import com.hida.dao.PurlDao;
import com.hida.model.Purl;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

/**
 * This class tests the functionality of MinterServiceImpl using Mockito.
 *
 * @author lruffin
 */
public class ResolverServiceImplTest {

    @Mock
    private PurlDao Dao;

    @InjectMocks
    private ResolverServiceImpl Service;
    
    /**
     * Sets up Mockito
     *
     * @throws Exception
     */
    @BeforeClass
    public void setUpClass() throws Exception {
        MockitoAnnotations.initMocks(this);        
    }

    /**
     * Tests to see if the URL of a given entity is properly retrieved
     */
    @Test
    public void testRetrieveURL() {
        Purl entity = new Purl();
        entity.setURL("");
        when(Dao.findByPurl(any(String.class))).thenReturn(entity);
        
        Service.retrieveURL("");
        
        verify(Dao, atLeastOnce()).findByPurl(any(String.class));
    }

    /**
     * Tests to see if an entity can be edited
     */
    @Test
    public void testEditURL() {        
        Purl entity = new Purl();
        when(Dao.findByPurl(any(String.class))).thenReturn(entity);
        
        Service.editURL("", "");
        verify(Dao, atLeastOnce()).findByPurl(any(String.class));
                
    }

    @Test
    public void testDeletePURL() {
        Assert.fail("unimplemented");
    }

    @Test
    public void testRetrieveModel() {
        Assert.fail("unimplemented");
    }

    @Test
    public void testInsertPURL() {
        Assert.fail("unimplemented");
    }

}
