package com.hida.service;

import org.apache.log4j.Logger;
import com.hida.controller.ResolverController;
import com.hida.model.Citation;
import com.hida.repositories.CitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ResolverServiceImpl
 *
 * holds functions that allow communication, and communicate with database
 * methods 1.ResolverServiceImpl - sets up conn, specify, serverURL, username,
 * password of database 2.Openconnection 3.closeConnection 4.retrieveUrl -
 * retrieve url requested 5.insertCitation - insert purl, along with its url,
 * who, what, when, erc 6.editUrl - edits url of a specified purl
 *
 * @author lruffin
 * @author: leland lopez
 */
@Service("resolverService")
@Transactional
public class ResolverServiceImpl implements ResolverService {
   

    @Autowired
    private CitationRepository CitationRepo;

    final static Logger logger = Logger.getLogger(ResolverController.class);

    /**
     * retrieves url of provided purlid returns url string if successfull, null
     * if not
     *
     * @param purl purlid of desired row
     * @return String
     */
    @Override
    public String retrieveUrl(String purl) {
        Citation entity = CitationRepo.findOne(purl);
        String url = entity.getUrl();

        return url;
    }

    /**
     * inserts PURL into database returns true if successful, false if not
     *
     * @param citation Citation to insert into database
     */
    @Override
    public void insertCitation(Citation citation) {
        CitationRepo.save(citation);
    }

    /**
     * edits url of db row with corresponding purlid. returns true if
     * successful, false if not
     *
     * @param purl purlid of desired edited row
     * @param url url that desired row url will be changed to
     */
    @Override
    public void editUrl(String purl, String url) {
        Citation entity = CitationRepo.findOne(purl);
        entity.setUrl(url);
    }

    /**
     * deletes db row with corresponding purlid returns true if successful,
     * false if not
     *
     * @param purl purlid of desired deleted row
     */
    @Override
    public void deleteCitation(String purl) {
        Citation entity = CitationRepo.findOne(purl);
        CitationRepo.delete(entity);
    }

    /**
     * retrieves model of purl_id object returns the respective purl db row.
     *
     * @param purl purlid of desired row that will become Citation
     * @return Citation
     */
    @Override
    public Citation retrieveCitation(String purl) {
        Citation entity = CitationRepo.findOne(purl);

        return entity;
    }
}
