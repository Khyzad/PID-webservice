package com.hida.dao;

import com.hida.model.Citation;

/**
 * This class is used to define the possible operations that Hibernate can
 perform on Citation objects
 *
 * @author lruffin
 */
public interface CitationDao {

    public Citation findByPurl(String identifier);

    public void savePurl(Citation purl);

    public void deletePurl(Citation purl);
}
