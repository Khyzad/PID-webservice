package com.hida.dao;

import com.hida.model.Citation;
import org.springframework.stereotype.Repository;

/**
 * Programmatic implementation of CitationDao
 *
 * @author lruffin
 */
@Repository("citationDao")
public class CitationDaoImpl extends AbstractDao<String, Citation> implements CitationDao {

    /**
     * Attempts to find a Citation by its identifier.
     *
     * @param identifier Unique name for a Citation
     * @return The entity if found, null otherwise
     */
    @Override
    public Citation findByPurl(String identifier) {
        return this.getByKey(identifier);
    }

    /**
     * Attempts to save the given Citation.
     *
     * @param citation Entity that holds referential information
     */
    @Override
    public void savePurl(Citation citation) {
        this.persist(citation);
    }

    /**
     * Attempts to delete the given purl.
     *
     * @param citation Entity that holds referential information
     */
    @Override
    public void deletePurl(Citation citation) {
        this.delete(citation);
    }

}
