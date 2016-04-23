package com.hida.service;

import com.hida.dao.CitationDao;
import com.hida.model.Citation;
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
    private CitationDao Dao;

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
        when(Dao.findByPurl(any(String.class))).thenReturn(entity);

        Service.retrieveUrl("");

        verify(Dao, atLeastOnce()).findByPurl(any(String.class));
    }

    /**
     * Tests to see if a Citation entity can be edited
     */
    @Test
    public void testEditUrl() {
        Citation entity = new Citation();
        when(Dao.findByPurl(any(String.class))).thenReturn(entity);

        Service.editUrl("", "");
        verify(Dao, atLeastOnce()).findByPurl(any(String.class));
    }

    /**
     * Tests to see if a Citation entity can be deleted
     */
    @Test
    public void testDeleteCitation() {
        Citation entity = new Citation();
        when(Dao.findByPurl(any(String.class))).thenReturn(entity);
        doNothing().when(Dao).deletePurl(entity);

        Service.deleteCitation("");
        verify(Dao, atLeastOnce()).deletePurl(any(Citation.class));
    }

    /**
     * Tests to see if a Citation entity is retrievable
     */
    @Test
    public void testRetrieveModel() {
        Citation entity = new Citation();
        when(Dao.findByPurl(any(String.class))).thenReturn(entity);

        verify(Dao, atLeastOnce()).findByPurl(any(String.class));
    }

    /**
     * Tests to see if a Citation object can be persisted
     */
    @Test
    public void testInsertCitation() {
        Citation purl = new Citation();
        doNothing().when(Dao).savePurl(purl);

        Service.insertCitation(purl);
        verify(Dao, atLeastOnce()).savePurl(any(Citation.class));
    }

}
