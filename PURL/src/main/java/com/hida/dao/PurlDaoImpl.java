package com.hida.dao;

import com.hida.model.Purl;
import org.springframework.stereotype.Repository;

/**
 * Programmatic implementation of PurlDao
 *
 * @author lruffin
 */
@Repository("purlDao")
public class PurlDaoImpl extends AbstractDao<String, Purl> implements PurlDao {

    /**
     * Attempts to find a Purl by its identifier.
     *
     * @param identifier Unique name for a Purl
     * @return The entity if found, null otherwise
     */
    @Override
    public Purl findByPurl(String identifier) {
        return this.getByKey(identifier);
    }

    /**
     * Attempts to save the given purl.
     *
     * @param purl
     */
    @Override
    public void savePurl(Purl purl) {
        this.persist(purl);
    }

    /**
     * Attempts to delete the given purl.
     *
     * @param purl
     */
    @Override
    public void deletePurl(Purl purl) {
        this.delete(purl);
    }

}
