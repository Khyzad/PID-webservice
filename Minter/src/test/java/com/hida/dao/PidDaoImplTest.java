package com.hida.dao;

import com.hida.configuration.RepositoryConfiguration;
import com.hida.model.AutoId;
import com.hida.model.CustomId;
import com.hida.model.Pid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the functionality of PidDao and ensures that it properly interacts with
 * Hibernate.
 *
 * @author lruffin
 */
@SpringApplicationConfiguration(classes = {RepositoryConfiguration.class})
public class PidDaoImplTest {
        
    private PidRepository PidRepo;
        
    @Autowired
    public void setPidRepository(PidRepository PidRepo) {
        this.PidRepo = PidRepo;
    } 
        
    /**
     * Tests to see if Pid can be saved and found.
     */
    @Test
    public void testFindAndSave() {
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
        long size = PidRepo.count();
        
        Assert.assertEquals(size, 2);
    }

    /**
     * Tests to see if AutoIds with the same name can be added to the database.
     */
    @Test
    public void testUniqueAutoId() {        
        Pid autoSample1 = getSampleAutoId();
        Pid autoSample2 = getSampleAutoId();

        PidRepo.save(autoSample1);
        PidRepo.save(autoSample2);
        
        long size = PidRepo.count();
        
        Assert.assertEquals(size, 2);
        
    }

    /**
     * Tests to see if CustomIds with the same name can be added to the
     * database.
     */
    @Test
    public void testUniqueCustomId() {
        Pid customSample1 = getSampleCustomId();
        Pid customSample2 = getSampleCustomId();

        PidRepo.save(customSample1);
        PidRepo.save(customSample2);
        
        long size = PidRepo.count();
        
        Assert.assertEquals(size, 2);
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
