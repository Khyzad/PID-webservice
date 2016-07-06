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
package com.hida.service;

import org.apache.log4j.Logger;
import com.hida.model.Citation;
import com.hida.repositories.CitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Allows the performance of CRUD operations on citation objects by by serving
 * as a middle ground between the database and the controller
 *
 * @author lruffin
 * @author: leland lopez
 */
@Service("resolverService")
@Transactional
public class ResolverService {

    @Autowired
    private CitationRepository CitationRepo;

    private final static Logger logger = Logger.getLogger(ResolverService.class);

    /**
     * Retrieves url of provided purlid
     *
     * @param purl purlid of desired row
     * @return String
     */
    public String retrieveUrl(String purl) {
        Citation entity = CitationRepo.findOne(purl);
        String url = entity.getUrl();

        return url;
    }

    /**
     * Inserts PURL into database
     *
     * @param citation Citation to insert into database
     */
    public void insertCitation(Citation citation) {
        CitationRepo.save(citation);
    }

    /**
     * Edits url of db row with corresponding purlid
     *
     * @param purl purlid of desired edited row
     * @param url url that desired row url will be changed to
     */
    public void editUrl(String purl, String url) {
        Citation entity = CitationRepo.findOne(purl);
        entity.setUrl(url);
    }

    /**
     * Deletes db row with corresponding purlid
     *
     * @param purl purlid of desired deleted row
     */
    public void deleteCitation(String purl) {
        Citation entity = CitationRepo.findOne(purl);
        CitationRepo.delete(entity);
    }

    /**
     * Retrieves citation corresponding to a particular purl
     *
     * @param purl purlid of desired row that will become Citation
     * @return Citation
     */
    public Citation retrieveCitation(String purl) {
        Citation entity = CitationRepo.findOne(purl);

        return entity;
    }
}
