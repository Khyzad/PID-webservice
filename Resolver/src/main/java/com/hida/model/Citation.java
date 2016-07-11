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
    private String purl_;

    @Column(name = "URL")
    private String url_;

    @Column(name = "ERC")
    private String erc_;

    @Column(name = "WHO")
    private String who_;

    @Column(name = "WHAT")
    private String what_;

    @Column(name = "DATE")
    private String date_;

    /**
     * Citation Constructor
     *
     * @param Purl Uniquely identifies a Citation
     */
    public Citation(String Purl) {
        this.purl_ = Purl;
    }

    public Citation(String Purl, String Url, String Erc, String Who, String What, String Date) {
        this.purl_ = Purl;
        this.url_ = Url;
        this.erc_ = Erc;
        this.who_ = Who;
        this.what_ = What;
        this.date_ = Date;
    }

    /**
     * No-arg constructor used by Hibernate
     */
    public Citation() {

    }

    public String getUrl() {
        return url_;
    }

    public void setUrl(String Url) {
        this.url_ = Url;
    }

    public String getPurl() {
        return purl_;
    }

    public void setPurl(String Purl) {
        this.purl_ = Purl;
    }

    public String getErc() {
        return erc_;
    }

    public void setErc(String Erc) {
        this.erc_ = Erc;
    }

    public String getWho() {
        return who_;
    }

    public void setWho(String who) {
        who_ = who;
    }

    public String getWhat() {
        return what_;
    }

    public void setWhat(String what) {
        what_ = what;
    }

    public String getDate() {
        return date_;
    }

    public void setDate(String when) {
        date_ = when;
    }

}
