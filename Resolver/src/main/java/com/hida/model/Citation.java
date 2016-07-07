/*
 * Copyright 2016 Lawrence Ruffin, Leland Lopez, Brittany Cruz, Stephen Anspach
 *
 * Developed in collaboration with the Hawaii State Digital Archives.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.hida.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * POJO for Citations, logical units of transaction that contain Purls that can
 * be resolved into various locations.
 *
 * @author lruffin
 * @author: leland lopez
 */
@Entity
@Table(name = "CITATION_TABLE")
public class Citation {

    @Id
    @Column(name = "PURL")
    private String Purl;

    @Column(name = "URL")
    private String Url;

    @Column(name = "ERC")
    private String Erc;

    @Column(name = "WHO")
    private String Who;

    @Column(name = "WHAT")
    private String What;

    @Column(name = "DATE")
    private String Date;

    /**
     * Citation Constructor
     *
     * @param Purl Uniquely identifies a Citation
     */
    public Citation(String Purl) {
        this.Purl = Purl;
    }

    public Citation(String Purl, String Url, String Erc, String Who, String What, String Date) {
        this.Purl = Purl;
        this.Url = Url;
        this.Erc = Erc;
        this.Who = Who;
        this.What = What;
        this.Date = Date;
    }

    /**
     * No-arg constructor used by Hibernate
     */
    public Citation() {

    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String Url) {
        this.Url = Url;
    }

    public String getPurl() {
        return Purl;
    }

    public void setPurl(String Purl) {
        this.Purl = Purl;
    }

    public String getErc() {
        return Erc;
    }

    public void setErc(String Erc) {
        this.Erc = Erc;
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
    
}
