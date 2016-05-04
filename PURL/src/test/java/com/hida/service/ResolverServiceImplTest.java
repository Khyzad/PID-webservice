package com.hida.service;

import com.hida.model.Citation;
import com.hida.repositories.CitationRepository;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

/**
 * This class tests the functionality of MinterServiceImpl using Mockito.
 *
 * @author lruffin
 */
public class ResolverServiceImplTest {

    @Mock
    private CitationRepository CitationRepo;

    @InjectMocks
    private ResolverServiceImpl Service;

    /**
     * Sets up Mockito
     *
     * @throws Exception
     */
    @BeforeClass
    public void setUpClass() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests to see if the URL of a given Citation entity is properly retrieved
     */
    @Test
    public void testRetrieveUrl() {
        Citation entity = new Citation();
        entity.setUrl("");
        when(CitationRepo.findOne(any(String.class))).thenReturn(entity);

        Service.retrieveUrl("");

        verify(CitationRepo, atLeastOnce()).findOne(any(String.class));
    }

    /**
     * Tests to see if a Citation entity can be edited
     */
    @Test
    public void testEditUrl() {
        Citation entity = new Citation();
        when(CitationRepo.findOne(any(String.class))).thenReturn(entity);

        Service.editUrl("", "");
        verify(CitationRepo, atLeastOnce()).findOne(any(String.class));
    }

    /**
     * Tests to see if a Citation entity can be deleted
     */
    @Test
    public void testDeleteCitation() {
        Citation entity = new Citation();
        when(CitationRepo.findOne(any(String.class))).thenReturn(entity);
        doNothing().when(CitationRepo).delete(entity);

        Service.deleteCitation("");
        verify(CitationRepo, atLeastOnce()).delete(any(Citation.class));
    }

    /**
     * Tests to see if a Citation entity is retrievable
     */
    @Test
    public void testRetrieveModel() {
        Citation entity = new Citation();
        when(CitationRepo.findOne(any(String.class))).thenReturn(entity);

        verify(CitationRepo, atLeastOnce()).findOne(any(String.class));
    }

    /**
     * Tests to see if a Citation object can be persisted
     */
    @Test
    public void testInsertCitation() {
        Citation purl = new Citation();
        when(CitationRepo.save(purl)).thenReturn(null);
        

        Service.insertCitation(purl);
        verify(CitationRepo, atLeastOnce()).save(any(Citation.class));
    }

}
