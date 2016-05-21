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
     * Tests to see if all the Pids can be listed and returned.
     */
    @Test
    public void testFindAllPids() {
        /*
        Pid autoSample = getSampleAutoId();
        Pid customSample = getSampleCustomId();

        PidRepo.save(autoSample);
        PidRepo.save(customSample);

        long size = PidRepo.count();

        Assert.assertEquals(size, 2);
        */
    }
    
    @Test
    public void testUniqeness(){
        Assert.fail("unimplemented");
    }
    
    @Test
    public void testSaveAndDelete(){
        Assert.fail("unimplemented");
    }
    
    @Test
    public void testFindByName(){
        Assert.fail("unimplemented");
    }      
    
    private Pid getSamplePid(){
        Pid sample = new Pid(new int[] {1}, "");
        sample.setName("a");
        
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
