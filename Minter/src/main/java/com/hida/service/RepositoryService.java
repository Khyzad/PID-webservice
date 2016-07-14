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
import com.hida.repositories.DefaultSettingRepository;
import com.hida.repositories.PidRepository;
import com.hida.repositories.UsedSettingRepository;
import java.io.IOException;
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

    IdGenerator generator_;

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
        Set<Pid> set;
        generator_ = getGenerator(setting);
        if (setting.isRandom()) {
            set = generator_.randomMint(amount);
        }
        else {
            set = generator_.sequentialMint(amount);
        }

        long total = generator_.getMaxPermutation();

        // check ids and increment them appropriately
        set = rollPidSet(set, total, amount);

        return set;
    }

    /**
     * Persists a set of Pids in the database. Also adds the Prepend to the Pids
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
    public long getRemainingPermutations(DefaultSetting setting, long amount) {
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
    private Set<Pid> rollPidSet(Set<Pid> set, long totalPermutations, long amount) {
        LOGGER.info("in rollIdSet");
        // Used to count the number of unique ids. Size methods aren't used because int is returned
        long uniqueIdCounter = 0;

        // Declares and initializes a list that holds unique values.          
        Set<Pid> uniqueList = new LinkedHashSet<>();

        // iterate through every id 
        for (Pid currentId : set) {
            // counts the number of times an id has been rejected
            long counter = 0;

            // continuously increments invalid or non-unique ids
            while (!isValidPid(currentId) || uniqueList.contains(currentId)) {
                /* 
                 if counter exceeds totalPermutations, then id has iterated through every 
                 possible permutation. Related format is updated as a quick look-up reference
                 with the number of ids that were inadvertedly been created using other formats.
                 NotEnoughPermutationsException is thrown stating remaining number of ids.
                 */
                if (counter > totalPermutations) {
                    LOGGER.error("Total number of Permutations Exceeded: Total Permutation Count="
                            + totalPermutations);
                    throw new NotEnoughPermutationsException(uniqueIdCounter, amount);
                }
                generator_.incrementPid(currentId);
                counter++;
            }
            // unique ids are added to list and uniqueIdCounter is incremented.
            // Size methods aren't used because int is returned
            uniqueIdCounter++;
            uniqueList.add(currentId);
        }
        return uniqueList;
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

}
