package com.hida.dao;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author lruffin
 */
public class PurlDaoImplTest extends EntityDaoImplTest{

    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet dataSet = new FlatXmlDataSet(this.getClass().getClassLoader().
                getResourceAsStream("Purl.xml"));
        return dataSet;        
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
