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

import com.hida.model.DefaultSetting;
import com.hida.model.Pid;
import com.hida.repositories.DefaultSettingRepository;
import com.hida.repositories.PidRepository;
import com.hida.repositories.UsedSettingRepository;
import java.io.IOException;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A service that directly creates transactions with Hibernate
 *
 * @author lruffin
 */
@Service("minterService")
@Transactional
public class RepositoryService {

    @Autowired
    private PidRepository pidRepo_;

    @Autowired
    private UsedSettingRepository usedSettingRepo_;

    @Autowired
    private DefaultSettingRepository defaultSettingRepo_;

    /**
     * Creates a set of Pids that are guaranteed to be disjoint from the set of
     * Pids persisted in the database.
     *
     * @param setting The settings the Pids are based off of
     * @param amount The requested amount of Pids
     * @return A set of Pids
     */
    public Set<Pid> generatePids(DefaultSetting setting, long amount) {
        return null;
    }

    /**
     * Persists a set of Pids in the database. Also adds the Prepend to the 
     * Pids
     *
     * @param setting The settings the Pids are based off of
     * @param set A set of Pids
     */
    public void persistPids(DefaultSetting setting, Set<Pid> set) {

    }
    
    /**
     * Returns the difference between the total permutations and the amount of
     * Pids that were already created using the requested settings.
     *
     * @param setting The settings the Pids are based off of
     * @param amount The requested amount of Pids
     * @return The amount of permutations remaining
     */
    public long getRemainingPermutations(DefaultSetting setting, long amount){
        return -1;
    }
    
    /**
     * Updates the CurrentDefaultSetting to match the values in the given
     * DefaultSetting.
     *
     * @param newSetting A DefaultSetting object that contains newly requested
     * values
     * @throws IOException Thrown when the file cannot be found
     */
    public void updateCurrentSetting(DefaultSetting newSetting) throws IOException {
        
    }
    
    /**
     * Initializes the storedSetting_ field by reading its value in the
     * database. If its null, then it is given initial values.
     *
     * @throws IOException Thrown when the file cannot be found
     */
    public void initializeStoredSetting() throws IOException {
        
    }

}
