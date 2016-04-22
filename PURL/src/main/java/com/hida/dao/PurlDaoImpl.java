package com.hida.dao;

import com.hida.model.Purl;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lruffin
 */
@Repository("defaultSettingDao")
public class PurlDaoImpl extends AbstractDao<String, Purl> implements PurlDao {

    @Override
    public Purl findByPurl(String purl) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save(Purl purl) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Purl purl) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
