package com.hida.repositories;

import com.hida.configuration.RepositoryConfiguration;
import com.hida.model.Pid;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.Assert;

/**
 * Tests the functionality of PidDao and ensures that it properly interacts with
 * Hibernate.
 *
 * @author lruffin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {RepositoryConfiguration.class})
@TestPropertySource(locations = "classpath:testConfig.properties")
public class PidRepositoryTest {

    private PidRepository PidRepo;

    @Autowired
    public void setPidRepository(PidRepository PidRepo) {
        this.PidRepo = PidRepo;
    }   
    
    /**
     * A test that ensures that Pids with the same name cannot be added to the
     * database
     */
    @Test
    public void testUniqeness(){
        Pid pid1 = getSamplePid();
        Pid pid2 = getSamplePid();
        
        PidRepo.save(pid1);
        PidRepo.save(pid2);
        
        long sizeBefore = PidRepo.count();        
        Assert.assertEquals(sizeBefore, 1);
    }
    
    /**
     * Tests the save and delete function
     */
    @Test
    public void testSaveAndDelete(){
        Pid pid = getSamplePid();
        
        PidRepo.save(pid);
        
        long sizeBefore = PidRepo.count();        
        Assert.assertEquals(sizeBefore, 1);
        
        PidRepo.delete(pid);
        
        long sizeAfter = PidRepo.count();        
        Assert.assertEquals(sizeAfter, 0);
    }
    
    /**
     * Test to see if a Pid can be located by its Name
     */
    @Test
    public void testFindByName(){
        Pid pid = getSamplePid();
        
        PidRepo.save(pid);
        
        Pid entity = PidRepo.findOne(pid.getName());
        Assert.assertNotNull(entity);
    }      
    
    private Pid getSamplePid(){
        Pid sample = new Pid("a");
        
        return sample;
    }

    /**
     * Deletes all entries in the in-memory database after each test
     */
    @After
    public void tearDown() {
        PidRepo.deleteAll();
    }
}
