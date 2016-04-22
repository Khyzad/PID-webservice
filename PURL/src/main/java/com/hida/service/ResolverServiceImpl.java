package com.hida.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import org.apache.log4j.Logger;
import com.hida.controller.ResolverController;
import com.hida.dao.PurlDao;
import com.hida.model.Purl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * DBconn
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
    //variables
    private String serverURL;
    private String username;
    private String password;
    private Connection conn = null;

    /**
     * DBConn constructor. Retrieves server credentials from
     * serverCredential.properties in resources directory
     *
     * @throws IOException throws if getServerCredentials() also throws an
     * IOException
     */
    public ResolverServiceImpl() throws IOException {
        getServerCredentials();
    }

    /**
     * opens db connection returns true if successful, false if not
     *
     * @return boolean
     */
    public boolean openConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            if ((conn = DriverManager.getConnection(serverURL, username, password)) != null) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception ee) {
            logger.error("Threw a BadException in DBConn::openConnection, full stack trace follows:", ee);
            return false;
        }
    }

    /**
     * closes db connection true if successful, false if not
     *
     * @return boolean
     */
    public boolean closeConnection() {
        try {
            if (conn != null) {
                conn.close();
                conn = null;
                return true;
            }
            return true;

        }
        catch (Exception ex) {
            logger.error("Threw a BadException in DBConn::closeConnection, full stack trace follows:", ex);
            return false;
        }
    }

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
     * @param PURLID purlid to be inserted
     * @param URL url to be inserted
     * @param ERC erc to be inserted
     * @param Who who to be inserted
     * @param What what to be inserted
     * @param When when to be inserted
     * @return boolean
     */
    @Override
    public boolean insertPURL(String PURLID, String URL, String ERC, String Who, String What, String When) {
        Purl purl = new Purl(PURLID, URL, ERC, Who, What, When);
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

    /**
     * gets server credentials from serverCredential.properties in the resources
     * directory returns true if successful, false if not.
     *
     * @return boolean
     * @throws IOException throws if input stream could not be closed
     */
    public boolean getServerCredentials() throws IOException {

        InputStream inputStream = null;

        try {
            Properties prop = new Properties();
            String propFileName = "serverCredential.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            }
            else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            this.serverURL = prop.getProperty("server");
            this.username = prop.getProperty("username");
            this.password = prop.getProperty("password");
            return true;

        }
        catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        finally {
            inputStream.close();
        }
        return false;
    }

}
