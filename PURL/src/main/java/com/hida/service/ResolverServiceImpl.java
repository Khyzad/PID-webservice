package com.hida.service;

import org.apache.log4j.Logger;
import com.hida.controller.ResolverController;
import com.hida.dao.PurlDao;
import com.hida.model.Purl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ResolverServiceImpl
 *
 * holds functions that allow communication, and communicate with database
 * methods 1.ResolverServiceImpl - sets up conn, specify, serverURL, username,
 * password of database 2.Openconnection 3.closeConnection 4.retrieveURL -
 * retrieve url requested 5.insertPURL - insert purl, along with its url, who,
 * what, when, erc 6.editURL - edits url of a specified purl
 *
 * @author lruffin
 * @author: leland lopez
 */
@Service("resolverService")
@Transactional
public class ResolverServiceImpl implements ResolverService {

    @Autowired
    private PurlDao PurlDao;
    
    final static Logger logger = Logger.getLogger(ResolverController.class);

    
    /**
     * retrieves url of provided purlid returns url string if successfull, null
     * if not
     *
     * @param PURLID purlid of desired row
     * @return String
     */
    @Override
    public String retrieveURL(String PURLID) {
        Purl entity = PurlDao.findByPurl(PURLID);
        String url = entity.getURL();
        
        return url;

    }

    /**
     * inserts PURL into database returns true if successful, false if not
     *     
     * @param purl Purl to insert into database
     * @return boolean
     */
    @Override
    public boolean insertPURL(Purl purl) {        
        PurlDao.savePurl(purl);
        
        return true;
    }

    /**
     * edits url of db row with corresponding purlid. returns true if
     * successful, false if not
     *
     * @param PURLID purlid of desired edited row
     * @param URL url that desired row url will be changed to
     * @return boolean
     */
    @Override
    public boolean editURL(String PURLID, String URL) {
        Purl entity = PurlDao.findByPurl(PURLID);
        entity.setURL(URL);
        
        return true;
    }

    /**
     * deletes db row with corresponding purlid returns true if successful,
     * false if not
     *
     * @param PURLID purlid of desired delted row
     * @return boolean
     */
    @Override
    public boolean deletePURL(String PURLID) {
        Purl entity = PurlDao.findByPurl(PURLID);
        PurlDao.deletePurl(entity);
        
        return true;
    }

    /**
     * retrieves model of purl_id object returns the respective purl db row.
     *
     * @param PURLID purlid of desired row that will become Purl
     * @return Purl
     */
    @Override
    public Purl retrieveModel(String PURLID) {
        Purl entity = PurlDao.findByPurl(PURLID);
        
        return entity;
        
    }   
}
