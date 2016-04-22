package com.hida.dao;

import com.hida.model.Purl;

/**
 *
 * @author lruffin
 */
public interface PurlDao {

    public Purl findByPurl(String identifier);

    public void savePurl(Purl purl);

    public void deletePurl(Purl purl);
}
