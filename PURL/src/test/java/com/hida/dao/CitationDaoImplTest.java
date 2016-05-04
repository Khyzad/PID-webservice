package com.hida.dao;

import com.hida.repositories.CitationDao;
import com.hida.model.Citation;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * Tests the functionality of CitationDaoTest and ensures that it properly interacts
 * with Hibernate.
 *
 * @author lruffin
 */
public class CitationDaoImplTest extends EntityDaoImplTest {
    
    @Autowired
    private CitationDao Dao;

    /**
     * Retrieves data from an xml file sheet to mock Pids.
     *
     * @return a data set
     * @throws Exception
     */
    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet dataSet = new FlatXmlDataSet(this.getClass().getClassLoader().
                getResourceAsStream("Citation.xml"));
        return dataSet;
    }

    /**
     * Tests to see if a Citation entity is retrievable
     */
    @Test
    public void testFindByPurl() {
        Citation entity1 = Dao.findByPurl("abc123");
        Citation entity2 = Dao.findByPurl("xyz");
        Citation entity3 = Dao.findByPurl("null");
        
        Assert.assertNotNull(entity1);
        Assert.assertNotNull(entity2);
        Assert.assertNull(entity3);        
    }

    /**
     * Tests to see if a Citation object can be saved
     */
    @Test
    public void testSavePurl() {
        Citation purl = new Citation("pid","url","erc","who","what","date");
        Dao.savePurl(purl);
        
        Citation entity = Dao.findByPurl("pid");
        Assert.assertNotNull(entity);
    }

    /**
     * Tests to see if a Citation entity can be removed
     */
    @Test
    public void testDeletePurl() {
        Citation entity = Dao.findByPurl("abc123");
        
        Dao.deletePurl(entity);
        
        Citation nullEntity = Dao.findByPurl("abc123");
        Assert.assertNull(nullEntity);       
    }

}
