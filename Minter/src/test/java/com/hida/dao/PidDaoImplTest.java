package com.hida.dao;

import com.hida.model.AutoId;
import com.hida.model.CustomId;
import com.hida.model.Pid;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.hibernate.NonUniqueObjectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author lruffin
 */
public class PidDaoImplTest extends EntityDaoImplTest {

    @Autowired
    PidDao PidDao;

    /**
     *
     * @return @throws Exception
     */
    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet dataSet = new FlatXmlDataSet(this.getClass().getClassLoader().
                getResourceAsStream("Pid.xml"));
        return dataSet;
    }

    /**
     *
     */
    @Test
    public void testFindByName() {
        Pid entity1 = PidDao.findByName("1");
        Pid entity2 = PidDao.findByName("2");
        Assert.assertNotNull(entity1);
        Assert.assertNotNull(entity2);
    }

    /**
     * missing javadoc
     *
     */
    @Test
    public void testSavePid() {
        Pid autoSample = getSampleAutoId();
        Pid customSample = getSampleCustomId();

        PidDao.savePid(autoSample);
        PidDao.savePid(customSample);

        Assert.assertEquals(PidDao.findAllPids().size(), 4);
    }        

    /**
     * missing javadoc
     *
     */
    @Test
    public void testFindAllPids() {
        int size = PidDao.findAllPids().size();
        
        Assert.assertEquals(size, 2);
    }
    
    /**
     * 
     */
    @Test(expectedExceptions = NonUniqueObjectException.class)
    public void testNonUniqueObjectExceptionWithAutoId(){
        Pid autoSample1 = getSampleAutoId();
        Pid autoSample2 = getSampleAutoId();
        
        PidDao.savePid(autoSample1);
        PidDao.savePid(autoSample2);
    }
    
    /**
     * 
     */
    @Test(expectedExceptions = NonUniqueObjectException.class)
    public void testNonUniqueObjectExceptionWithCustomId(){
        Pid customSample1 = getSampleCustomId();
        Pid customSample2 = getSampleCustomId();
        
        PidDao.savePid(customSample1);
        PidDao.savePid(customSample2);
    }

    private Pid getSampleAutoId() {
        Pid sample = new AutoId("", new int[1], "a");
        sample.getName();

        return sample;
    }

    private Pid getSampleCustomId() {
        Pid sample = new CustomId("", new int[1], new String[]{"b"});
        sample.getName();

        return sample;
    }

}
