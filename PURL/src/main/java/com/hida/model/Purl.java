package com.hida.model;

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
@Table(name = "PURL")
public class Purl {

    //variables
    @Id        
    private String Identifier;
    private String URL;    
    private String ERC;
    private String Who;
    private String What;
    private String When;

    /**
     * Purl Constructor
     *
     * @param PURL purlid of model_purl
     */
    public Purl(String PURL) {
        this.Identifier = PURL;
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
        return Identifier;
    }

    public void setIdentifier(String Identifier) {
        this.Identifier = Identifier;
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

    public String getWhen() {
        return When;
    }

    public void setWhen(String when) {
        When = when;
    }

    /**
     *
     * @return Json String
     */
    public String toJSON() {
        String json = "";
        json += "{";
        json += "\"PURL\":\"" + Identifier + "\",";
        json += "\"URL\":\"" + URL + "\",";
        json += "\"ERC\":\"" + ERC + "\",";
        json += "\"Who\":\"" + Who + "\",";
        json += "\"What\":\"" + What + "\",";
        json += "\"When\":\"" + When + "\"";
        json += "}";
        return json;
    }

}
