package com.hida.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * POJO for PURLs, logical units of transaction that can be resolved into
 * various records and locations.
 *
 * @author lruffin
 * @author: leland lopez
 */
@Entity
@Table(name = "PURL_TABLE")
public class Purl {

    //variables
    @Id 
    @Column(name = "PID")
    private String Pid;
    
    @Column(name = "URL")
    private String URL;    
    
    @Column(name = "ERC")
    private String ERC;
    
    @Column(name = "WHO")
    private String Who;
    
    @Column(name = "WHAT")
    private String What;
    
    @Column(name = "DATE")
    private String Date;

    /**
     * Purl Constructor
     *
     * @param PURL purlid of model_purl
     */
    public Purl(String PURL) {
        this.Pid = PURL;
    }

    public Purl(String Identifier, String URL, String ERC, String Who, String What, String When) {
        this.Pid = Identifier;
        this.URL = URL;
        this.ERC = ERC;
        this.Who = Who;
        this.What = What;
        this.Date = When;
    }
    
    

    /**
     * No-arg constructor used by Hibernate
     */
    public Purl() {

    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getIdentifier() {
        return Pid;
    }

    public void setIdentifier(String Identifier) {
        this.Pid = Identifier;
    }

    public String getERC() {
        return ERC;
    }

    public void setERC(String ERC) {
        this.ERC = ERC;
    }

    public String getWho() {
        return Who;
    }

    public void setWho(String who) {
        Who = who;
    }

    public String getWhat() {
        return What;
    }

    public void setWhat(String what) {
        What = what;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String when) {
        Date = when;
    }

    /**
     *
     * @return Json String
     */
    public String toJSON() {
        String json = "";
        json += "{";
        json += "\"PURL\":\"" + Pid + "\",";
        json += "\"URL\":\"" + URL + "\",";
        json += "\"ERC\":\"" + ERC + "\",";
        json += "\"Who\":\"" + Who + "\",";
        json += "\"What\":\"" + What + "\",";
        json += "\"When\":\"" + Date + "\"";
        json += "}";
        return json;
    }

}
