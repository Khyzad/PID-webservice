/*
 * Copyright 2016 Lawrence Ruffin, Leland Lopez, Brittany Cruz, Stephen Anspach
 *
 * Developed in collaboration with the Hawaii State Digital Archives.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.hida.repositories;

import com.hida.configuration.RepositoryConfiguration;
import com.hida.model.Pid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * Tests the functionality of PidRepository
 *
 * @author lruffin
 */
@WebAppConfiguration
@IntegrationTest
@SpringApplicationConfiguration(classes = {RepositoryConfiguration.class})
@TestPropertySource(locations = "classpath:testConfig.properties")
@TestExecutionListeners(inheritListeners = false, listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class})
public class PidRepositoryTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private PidRepository pidRepo_;

    /**
     * A test that ensures that Pids with the same name cannot be added to the
     * database
     */
    @Test
    public void testUniqeness() {
        Pid pid1 = getSamplePid();
        Pid pid2 = getSamplePid();

        pidRepo_.save(pid1);
        pidRepo_.save(pid2);

        long sizeBefore = pidRepo_.count();
        Assert.assertEquals(sizeBefore, 1);
    }

    /**
     * Tests the save and delete function
     */
    @Test
    public void testSaveAndDelete() {
        Pid pid = getSamplePid();

        pidRepo_.save(pid);

        long sizeBefore = pidRepo_.count();
        Assert.assertEquals(sizeBefore, 1);

        pidRepo_.delete(pid);

        long sizeAfter = pidRepo_.count();
        Assert.assertEquals(sizeAfter, 0);
    }

    /**
     * Test to see if a Pid can be located by its Name
     */
    @Test
    public void testFindByName() {
        Pid pid = getSamplePid();

        pidRepo_.save(pid);

        Pid entity = pidRepo_.findOne(pid.getName());
        Assert.assertNotNull(entity);
    }

    private Pid getSamplePid() {
        Pid sample = new Pid("a");

        return sample;
    }

    /**
     * Deletes all entries in the in-memory database after each test
     */
    @AfterMethod
    public void tearDown() {
        pidRepo_.deleteAll();
    }
}
