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

import com.hida.model.Pid;
import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This service allows the Minter to preemptively create Pids when its idle,
 * reducing the the time it may take to handle a request.
 *
 * @author lruffin
 */
@Service("cacheService")
public class CacheService {
    
    /**
     * Logger; logfile to be stored in resource folder
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);

    /**
     * A set to hold a cache of Pids 
     */
    private final Set<Pid> cache_ = new LinkedHashSet<>();
    
    
}
