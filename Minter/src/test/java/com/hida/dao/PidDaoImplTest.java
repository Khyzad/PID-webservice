package com.hida.dao;

import com.hida.model.AutoId;
import com.hida.model.CustomId;
import com.hida.model.Pid;
import java.util.Iterator;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.hibernate.NonUniqueObjectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the functionality of PidDao and ensures that it properly interacts with
 * Hibernate.
 *
 * @author lruffin
 */
public class PidDaoImplTest extends EntityDaoImplTest {

    @Autowired
    private PidDao PidDao;
    
    @Autowired
    private PidRepository PidRepo;

    /**
     * Retrieves data from an xml file sheet to mock Pids.
     *
     * @return a data set
     * @throws Exception
     */
    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet dataSet = new FlatXmlDataSet(this.getClass().getClassLoader().
                getResourceAsStream("Pid.xml"));
        return dataSet;
    }

    /**
     * Tests to see if PidDao can find an Pid by it's name.
     */
    @Test
    public void testFindByName() {
        Pid entity1 = PidRepo.findOne("1");
        Pid entity2 = PidRepo.findOne("2");
        Assert.assertNotNull(entity1);
        Assert.assertNotNull(entity2);
    }

    /**
     * Tests to see if Pid can be saved.
     */
    @Test
    public void testSavePid() {
        Pid autoSample = getSampleAutoId();
        Pid customSample = getSampleCustomId();

        PidRepo.save(autoSample);
        PidRepo.save(customSample);
        
        Pid autoEntity = PidRepo.findOne(autoSample.getName());
        Pid customEntity = PidRepo.findOne(customSample.getName());

        Assert.assertEquals(autoSample.getName(), autoEntity.getName());
        Assert.assertEquals(customSample.getName(), customEntity.getName());
    }

    /**
     * Tests to see if all the Pids can be listed and returned.
     */
    @Test
    public void testFindAllPids() {
        Iterator<Pid> iterator = PidRepo.findAll().iterator();
        
        int size = 0;
        while(iterator.hasNext()){
            iterator.next();
            size++;
        }

        Assert.assertEquals(size, 2);
    }

    /**
     * Tests to see if AutoIds with the same name can be added to the database.
     */
    @Test(expectedExceptions = NonUniqueObjectException.class)
    public void testNonUniqueObjectExceptionWithAutoId() {
        Pid autoSample1 = getSampleAutoId();
        Pid autoSample2 = getSampleAutoId();

        PidRepo.save(autoSample1);
        PidRepo.save(autoSample2);
    }

    /**
     * Tests to see if CustomIds with the same name can be added to the
     * database.
     */
    @Test(expectedExceptions = NonUniqueObjectException.class)
    public void testNonUniqueObjectExceptionWithCustomId() {
        Pid customSample1 = getSampleCustomId();
        Pid customSample2 = getSampleCustomId();

        PidRepo.save(customSample1);
        PidRepo.save(customSample2);
    }

    /**
     * returns a sample AutoId.
     *
     * @return
     */
    private Pid getSampleAutoId() {
        Pid sample = new AutoId("", new int[1], "a");
        sample.getName();

        return sample;
    }

    /**
     * Returns a sample CustomId.
     *
     * @return
     */
    private Pid getSampleCustomId() {
        Pid sample = new CustomId("", new int[1], new String[]{"b"});
        sample.getName();

        return sample;
    }

}
