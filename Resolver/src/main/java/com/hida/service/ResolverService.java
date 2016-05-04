package com.hida.service;

import com.hida.model.Citation;

/**
 * This class is used to define the possible operations that Hibernate can
 * perform on MinterService objects
 *
 * @author lruffin
 */
public interface ResolverService {

    public String retrieveUrl(String purl);

    public void editUrl(String purl, String url);

    public void deleteCitation(String purl);

    public Citation retrieveCitation(String purl);

    public void insertCitation(Citation citation);
}
