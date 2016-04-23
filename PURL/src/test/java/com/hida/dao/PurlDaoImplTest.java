package com.hida.dao;

import com.hida.model.Purl;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * Tests the functionality of PurlDaoTest and ensures that it properly interacts
 * with Hibernate.
 *
 * @author lruffin
 */
public class PurlDaoImplTest extends EntityDaoImplTest {
    
    @Autowired
    private PurlDao Dao;

    /**
     * Retrieves data from an xml file sheet to mock Pids.
     *
     * @return a data set
     * @throws Exception
     */
    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet dataSet = new FlatXmlDataSet(this.getClass().getClassLoader().
                getResourceAsStream("Purl.xml"));
        return dataSet;
    }

    /**
     * Tests to see if a purl entity is retrievable
     */
    @Test
    public void testFindByPurl() {
        Purl entity1 = Dao.findByPurl("abc123");
        Purl entity2 = Dao.findByPurl("xyz");
        Purl entity3 = Dao.findByPurl("null");
        
        Assert.assertNotNull(entity1);
        Assert.assertNotNull(entity2);
        Assert.assertNull(entity3);        
    }

    @Test
    public void testSavePurl() {
        Assert.fail("unimplemented");
    }

    @Test
    public void testDeletePurl() {
        Assert.fail("unimplemented");
    }

}
