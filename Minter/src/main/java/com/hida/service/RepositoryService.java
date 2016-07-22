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

import com.hida.model.AutoIdGenerator;
import com.hida.model.CustomIdGenerator;
import com.hida.model.DefaultSetting;
import com.hida.model.IdGenerator;
import com.hida.model.NotEnoughPermutationsException;
import com.hida.model.Pid;
import com.hida.model.Token;
import com.hida.model.UsedSetting;
import com.hida.repositories.DefaultSettingRepository;
import com.hida.repositories.PidRepository;
import com.hida.repositories.UsedSettingRepository;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A service that directly creates transactions with Hibernate
 *
 * @author lruffin
 */
@Service("repositoryService")
@Transactional
public class RepositoryService {

    @Autowired
    private PidRepository pidRepo_;

    @Autowired
    private UsedSettingRepository usedSettingRepo_;

    @Autowired
    private DefaultSettingRepository defaultSettingRepo_;

    private final Set<Pid> cache_ = new LinkedHashSet<>();

    private IdGenerator generator_;
    
    private DefaultSetting cacheSetting_;

    /**
     * Logger; logfile to be stored in resource folder
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryService.class);

    /**
     * Creates a set of Pids that are guaranteed to be disjoint from the set of
     * Pids persisted in the database. If the current setting is random, have
     * the generator return a random set, otherwise, have the generator return a
     * sequential set
     *
     * @param setting The settings the Pids are based off of
     * @param amount The requested amount of Pids
     * @return A set of Pids
     */
    public Set<Pid> generatePids(DefaultSetting setting, long amount) {
        // create a generator
        Set<Pid> set;
        generator_ = getGenerator(setting);
        if (setting.isRandom()) {
            set = generator_.randomMint(amount);
        }
        else {
            set = generator_.sequentialMint(amount);
        }

        // calculat maximum permutations
        long total = generator_.getMaxPermutation();

        // check ids and increment them appropriately
        set = rollPidSet(set, total);

        return set;
    }

    /**
     * Creates a set of Pids at a starting value that are guaranteed to be
     * disjoint from the set of Pids persisted in the database. If the current
     * setting is random, have the generator return a random set, otherwise,
     * have the generator return a sequential set
     *
     * @param setting The settings the Pids are based off of
     * @param amount The requested amount of Pids
     * @param startingValue The value to start at
     * @return A set of Pids
     */
    public Set<Pid> generatePids(DefaultSetting setting, long amount, long startingValue) {
        // create a generator
        generator_ = getGenerator(setting);

        // generate a set of Pids at an arbitrary starting value
        Set<Pid> set = generator_.sequentialMint(amount, startingValue);

        // calculat maximum permutations
        long total = generator_.getMaxPermutation();

        // check ids and increment them appropriately
        set = rollPidSet(set, total);

        return set;
    }

    /**
     * Persists a set of Pids in the database. Also adds the Prepend to the Pids
     *
     * @param setting The settings the Pids are based off of
     * @param set A set of Pids
     * @param amountCreated Amount of pids that were created
     */
    public void persistPids(DefaultSetting setting, Set<Pid> set, long amountCreated) {
        LOGGER.info("in persistPids");

        for (Pid pid : set) {
            pidRepo_.save(pid);
            pid.setName(setting.getPrepend() + pid.getName());
        }

        LOGGER.info("DatabaseUpdated with new pids");

        // update table format
        recordSettings(setting, amountCreated);
    }

    public long getMaxPermutation(DefaultSetting setting) {
        generator_ = this.getGenerator(setting);
        return generator_.getMaxPermutation();
    }

    /**
     * Returns the amount of Pids that were created using the settings
     *
     * @param setting The settings the Pids are based off of
     * @return The amount of permutations remaining
     */
    public long getCurrentAmount(DefaultSetting setting) {
        UsedSetting entity = this.findUsedSetting(setting);

        return entity.getAmount();
    }

    /**
     * Updates the CurrentDefaultSetting to match the values in the given
     * DefaultSetting.
     *
     * @param newSetting A DefaultSetting object that contains newly requested
     * values
     */
    public void updateCurrentSetting(DefaultSetting newSetting) {
        LOGGER.info("in updateCurrentSetting");

        DefaultSetting oldSetting = defaultSettingRepo_.findCurrentDefaultSetting();
        oldSetting.setPrepend(newSetting.getPrepend());
        oldSetting.setPrefix(newSetting.getPrefix());
        oldSetting.setCharMap(newSetting.getCharMap());
        oldSetting.setRootLength(newSetting.getRootLength());
        oldSetting.setTokenType(newSetting.getTokenType());
        oldSetting.setAuto(newSetting.isAuto());
        oldSetting.setRandom(newSetting.isRandom());
        oldSetting.setSansVowels(newSetting.isSansVowels());
    }

    /**
     * Generates the cache
     *
     * @param setting The setting values to base the Pids off of
     */
    public void generateCache(DefaultSetting setting) {
        // store the setting
        this.cacheSetting_ = setting;
        
        // try to fulfill the requested cache size
        long totalPermutations = this.getMaxPermutation(setting);
        long amount;        
        if (totalPermutations > setting.getCacheSize()) {
            amount = setting.getCacheSize() - cache_.size();
        }
        else {
            amount = totalPermutations - cache_.size();
        }
        Set<Pid> set = this.generatePids(setting, amount);

        boolean flag = true;
        Iterator<Pid> iter = set.iterator();
        // iterate through every id 
        while (iter.hasNext() && flag) {
            Pid pid = iter.next();

            // counts the number of times an id has been rejected
            long counter = 0;

            // continuously increments invalid or non-unique ids
            while (!isValidPid(pid) || !cache_.add(pid)) {

                if (counter > totalPermutations) {
                    // all possible permutations have been checked, raise flag
                    flag = false;
                    break;
                }
                else {
                    // all possible permutations have not been checked, increment
                    generator_.incrementPid(pid);
                    counter++;
                }

            }
        }
    }

    /**
     * Retrieves the requested amount of Pids from the cache. Also removes the
     * selected Pids from the cache.
     *
     * @param amount The amount of Pids
     * @return A set containing the requested amount of Pids
     */
    public Set<Pid> collectCache(long amount) {
        Set<Pid> set1 = new LinkedHashSet<>();

        Iterator<Pid> iter = cache_.iterator();
        for (long i = 0; i < amount && iter.hasNext(); i++) {
            Pid pid = iter.next();
            set1.add(pid);
        }

        cache_.removeAll(set1);
        
        if(amount > set1.size()){
            Set<Pid> set2 = this.generatePids(cacheSetting_, amount - set1.size());
            combinePidSet(set1, set2, this.getMaxPermutation(cacheSetting_));
        }

        return set1;
    }

    /**
     * Initializes the storedSetting_ field by reading its value in the
     * database. If its null, then it is given initial values.
     *
     * @throws IOException Thrown when the file cannot be found
     */
    public void initializeStoredSetting() throws IOException {

    }

    public Set<Pid> getCache() {
        return this.cache_;
    }

    /**
     * Checks to see if a Pid already exists in the database.
     *
     * @param pid Pid to be checked
     * @return Returns true if a Pid with the same doesn't exist, false
     * otherwise
     */
    private boolean isValidPid(Pid pid) {
        LOGGER.info("in isValidId");
        Pid entity = this.pidRepo_.findOne(pid.getName());
        return entity == null;
    }

    /**
     * Continuously increments a set of ids until the set is completely filled
     * with unique ids.
     *
     * @param set the set of ids
     * @param order determines whether or not the ids will be ordered
     * @param isAuto determines whether or not the ids are AutoId or CustomId
     * @param amount the amount of ids to be created.
     * @return A set of unique ids database.
     */
    private Set<Pid> rollPidSet(Set<Pid> set, long totalPermutations) {
        LOGGER.info("in rollIdSet");
        // Declares and initializes a list that holds unique values.          
        Set<Pid> uniqueList = new LinkedHashSet<>();

        // iterate through every id 
        for (Pid currentId : set) {
            // counts the number of times an id has been rejected
            long counter = 0;

            // continuously increments invalid or non-unique ids
            while (!isValidPid(currentId) || uniqueList.contains(currentId)) {
                // return a partially complete unique set if needed
                if (counter > totalPermutations) {
                    return uniqueList;
                }
                generator_.incrementPid(currentId);
                counter++;
            }

            // a unique Pid has been found, add it to the list
            uniqueList.add(currentId);
        }
        return uniqueList;
    }

    /**
     * Continuously increments a set of ids until the set is completely filled
     * with unique ids.
     *
     * @param set1 the set of pids to add into
     * @param set2 the set of pids to add from
     * @param order determines whether or not the ids will be ordered
     * @param isAuto determines whether or not the ids are AutoId or CustomId
     * @param amount the amount of ids to be created.
     * @return A set of unique ids database.
     */
    private void combinePidSet(Set<Pid> set1, Set<Pid> set2, long totalPermutations) {
        boolean flag = true;
        Iterator<Pid> iter = set2.iterator();
        // iterate through every id 
        while (iter.hasNext() && flag) {
            Pid pid = iter.next();

            // counts the number of times an id has been rejected
            long counter = 0;

            // continuously increments invalid or non-unique ids
            while (!isValidPid(pid) || !set1.add(pid)) {

                if (counter > totalPermutations) {
                    return;
                }
                else {
                    // all possible permutations have not been checked, increment
                    generator_.incrementPid(pid);
                    counter++;
                }

            }
        }
    }

    private IdGenerator getGenerator(DefaultSetting setting) {
        LOGGER.info("in createGenerator");
        if (setting.isAuto()) {
            LOGGER.info("AutoGenerator created");
            return new AutoIdGenerator(
                    setting.getPrefix(),
                    setting.getTokenType(),
                    setting.getRootLength());
        }
        else {
            LOGGER.info("CustomIdGenerator created");
            return new CustomIdGenerator(
                    setting.getPrefix(),
                    setting.isSansVowels(),
                    setting.getCharMap());
        }
    }

    /**
     * Attempts to record the setting that were used to create the current set
     * of Pids
     *
     * @param amount The number of PIDs that were created
     */
    private void recordSettings(DefaultSetting setting, long amount) {
        LOGGER.info("in recordSettings");

        UsedSetting entity = findUsedSetting(setting);

        if (entity == null) {
            entity = new UsedSetting(setting.getPrefix(),
                    setting.getTokenType(),
                    setting.getCharMap(),
                    setting.getRootLength(),
                    setting.isSansVowels(),
                    amount);

            usedSettingRepo_.save(entity);
        }
        else {
            long previousAmount = entity.getAmount();
            entity.setAmount(previousAmount + amount);
        }
    }

    /**
     * Attempts to find a UsedSetting based on the currently used DefaultSetting
     *
     * @return Returns a UsedSetting entity if found, null otherwise
     */
    private UsedSetting findUsedSetting(DefaultSetting setting) {
        LOGGER.info("in findUsedSetting");

        return usedSettingRepo_.findUsedSetting(setting.getPrefix(),
                setting.getTokenType(),
                setting.getCharMap(),
                setting.getRootLength(),
                setting.isSansVowels());
    }
}
