package com.hida.dao;

import org.dbunit.dataset.IDataSet;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author lruffin
 */
public class PurlDaoImplTest extends EntityDaoImplTest{

    @Override
    protected IDataSet getDataSet() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Test
    public void testFindByPurl(){
        Assert.fail("unimplemented");
    }

    @Test
    public void testSavePurl(){
        Assert.fail("unimplemented");
    }

    @Test
    public void testDeletePurl(){
        Assert.fail("unimplemented");
    }
    
}
