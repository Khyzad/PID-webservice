package com.hida.dao;

import com.hida.model.Purl;

/**
 *
 * @author lruffin
 */
public interface PurlDao {

    public Purl findByPurl(String purl);

    public void save(Purl purl);

    public void delete(Purl purl);
}
