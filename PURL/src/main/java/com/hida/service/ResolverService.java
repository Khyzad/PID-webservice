package com.hida.service;

import com.hida.model.Purl;

/**
 * This class is used to define the possible operations that Hibernate can
 * perform on MinterService objects
 *
 * @author lruffin
 */
public interface ResolverService {
    public String retrieveURL(String purlId);
    public boolean editURL(String PURLID, String URL);
    public boolean deletePURL(String PURLID);
    public Purl retrieveModel(String PURLID);
    public boolean insertPURL(String PURLID, String URL, String 
            ERC, String Who, String What, String When);
}
