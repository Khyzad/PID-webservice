package com.hida.dao;

import com.hida.model.Purl;

/**
 * This class is used to define the possible operations that Hibernate can
 * perform on Purl objects
 *
 * @author lruffin
 */
public interface PurlDao {

    public Purl findByPurl(String identifier);

    public void savePurl(Purl purl);

    public void deletePurl(Purl purl);
}
