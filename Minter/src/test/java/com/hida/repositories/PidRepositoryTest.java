package com.hida.repositories;

import com.hida.repositories.PidRepository;
import com.hida.configuration.RepositoryConfiguration;
import com.hida.model.AutoId;
import com.hida.model.CustomId;
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
        Pid autoSample = getSampleAutoId();
        Pid customSample = getSampleCustomId();

        PidRepo.save(autoSample);
        PidRepo.save(customSample);

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

        Assert.assertEquals(size, 1);
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

        Assert.assertEquals(size, 1);
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

    /**
     * Deletes all entries in the in-memory database after each test
     */
    @After
    public void tearDown() {
        PidRepo.deleteAll();
    }
}
