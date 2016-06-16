package com.hida.repositories;

import com.hida.configuration.RepositoryConfiguration;
import com.hida.model.Citation;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * Tests the functionality of CitationDaoTest and ensures that it properly interacts
 * with Hibernate.
 *
 * @author lruffin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {RepositoryConfiguration.class})
@TestPropertySource(locations = "classpath:testConfig.properties")
public class CitationRepositoryTest {
    
    @Autowired
    private CitationRepository citationRepo_;   

    /**
     * Tests to see if a Citation entity is retrievable
     */
    @Test
    public void testFindByPurl() {
        Citation entity1 = citationRepo_.findOne("abc123");
        Citation entity2 = citationRepo_.findOne("xyz");
        Citation entity3 = citationRepo_.findOne("null");
        
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
        citationRepo_.save(purl);
        
        Citation entity = citationRepo_.findOne("pid");
        Assert.assertNotNull(entity);
    }

    /**
     * Tests to see if a Citation entity can be removed
     */
    @Test
    public void testDeletePurl() {
        Citation entity = citationRepo_.findOne("abc123");
        
        citationRepo_.delete(entity);
        
        Citation nullEntity = citationRepo_.findOne("abc123");
        Assert.assertNull(nullEntity);       
    }

}
