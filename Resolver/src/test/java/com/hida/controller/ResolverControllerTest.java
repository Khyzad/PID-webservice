package com.hida.controller;

import com.hida.model.Citation;
import com.hida.service.ResolverService;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

/**
 * Tests the functionality of ResolverController to see if it is capable of
 * performing REST calls.
 *
 * @author lruffin
 */
public class ResolverControllerTest {

    @Mock
    private ResolverService service_;

    @InjectMocks
    private ResolverController controller_;

    @BeforeClass
    public void setUpClass() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test the retrieve REST call
     *
     * @throws Exception
     */
    @Test
    public void testRetrieve() throws Exception {
        Citation entity = getSampleCitation();
        when(service_.retrieveCitation(any(String.class))).thenReturn(entity);

        // test to see that Service at least makes a call to get a Citation object
        verify(service_, atLeastOnce()).retrieveCitation(any(String.class));

        // test to see that Json is formated properly
        Citation retrievedCitation = controller_.retrieve(entity.getPurl());

        Assert.assertEquals(entity, retrievedCitation);
    }

    /**
     * Test the edit REST call
     *
     * @throws Exception
     */
    @Test
    public void testEdit() throws Exception {
        // create citation entity to reflect the change that happens after calling edit
        Citation entity = getSampleCitation();

        // pretend the entity was edited and return new entity
        doNothing().when(service_).editUrl(any(String.class), any(String.class));
        when(service_.retrieveCitation(any(String.class))).thenReturn(entity);

        // test to see that Json is formated properly
        String response = controller_.edit(entity.getPurl(), entity.getUrl());

        Assert.assertEquals("", response);
    }

    /**
     * Tests the insert REST call
     *
     * @throws Exception
     */
    @Test
    public void testInsert() throws Exception {
        // create citation entites to reflect the change that happens after calling edit
        Citation entity = getSampleCitation();

        // pretend the entity was inserted 
        doNothing().when(service_).insertCitation(any(Citation.class));

        // test to see that Json is formated properly
        String response = controller_.insert(entity.getPurl(),
                entity.getUrl(),
                entity.getErc(),
                entity.getWho(),
                entity.getWhat(),
                entity.getDate());
        Assert.assertEquals("", response);
    }

    /**
     * Tests the delete REST call
     *
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        // pretend the entity was deleted
        doNothing().when(service_).deleteCitation(any(String.class));

        // call delete
        String response = controller_.delete("");

        // test to see that Json is formated properly
        Assert.assertEquals("", response);
    }

    /**
     * Gets a sample Citation object for testing
     *
     * @return sample Citation object
     */
    private Citation getSampleCitation() {
        Citation citation = new Citation("samplePid",
                "sampleURL",
                "sampleERC",
                "sampleWho",
                "sampleWhat",
                "sampleTime");

        return citation;
    }
}
