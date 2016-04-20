package com.hida.service;

import com.hida.dao.DefaultSettingDao;
import com.hida.dao.PidDao;
import com.hida.model.TokenType;
import com.hida.dao.UsedSettingDao;
import com.hida.model.AutoIdGenerator;
import com.hida.model.CustomIdGenerator;
import com.hida.model.DefaultSetting;
import com.hida.model.Pid;
import com.hida.model.IdGenerator;
import com.hida.model.NotEnoughPermutationsException;
import com.hida.model.UsedSetting;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A service class that is used as a medium between the requests received by the
 * controller and the transactions done by Hibernate.
 *
 * @author lruffin
 */
@Service("minterService")
@Transactional
public class MinterServiceImpl implements MinterService {

    /**
     * Logger; logfile to be stored in resource folder
     */
    private static final Logger Logger = LoggerFactory.getLogger(MinterServiceImpl.class);

    @Autowired
    private PidDao PidDao;

    @Autowired
    private UsedSettingDao UsedSettingDao;

    @Autowired
    private DefaultSettingDao DefaultSettingDao;

    /**
     * Declares a Generator object to manage
     */
    private IdGenerator Generator;

    /**
     * Used to store the value of the current settings
     */
    private DefaultSetting CurrentDefaultSetting;

    /**
     * No-arg constructor
     */
    public MinterServiceImpl() {

    }

    /**
     * Returns the difference between the total permutations and the amount of
     * Pids that were already created using the requested settings.
     *
     * @return The amount of permutations remaining
     */
    private long getRemainingPermutations() {
        Logger.info("in getRemainingPerumtations");
        long totalPermutations = Generator.calculatePermutations();
        long amountCreated = getAmountCreated();

        return totalPermutations - amountCreated;
    }

    /**
     * Returns the amount of Pids that were created using the requested settings
     *
     * @return amount of Pids
     */
    private long getAmountCreated() {
        UsedSetting entity = findUsedSetting();
        if (entity == null) {
            return 0;
        }
        else {
            return entity.getAmount();
        }
    }

    /**
     * Creates a generator to be used in accordance to the setting
     */
    private void createGenerator() {
        Logger.info("in createGenerator");
        if (CurrentDefaultSetting.isAuto()) {
            Generator = new AutoIdGenerator(
                    CurrentDefaultSetting.getPrefix(),
                    CurrentDefaultSetting.isSansVowels(),
                    CurrentDefaultSetting.getTokenType(),
                    CurrentDefaultSetting.getRootLength());

            Logger.info("AutoGenerator created");
        }
        else {
            Generator = new CustomIdGenerator(
                    CurrentDefaultSetting.getPrefix(),
                    CurrentDefaultSetting.isSansVowels(),
                    CurrentDefaultSetting.getCharMap());
            Logger.info("CustomIdGenerator created");
        }
    }

    /**
     * Attempts to create a number of Pids and store them in database
     *
     * @param amount The number of PIDs to be created
     * @param setting The desired setting used to create a Pid
     * @return
     */
    @Override
    public Set<Pid> mint(long amount, DefaultSetting setting) {
        Logger.info("in mint");

        // store the desired setting values 
        this.CurrentDefaultSetting = setting;

        // create appropriate generator
        createGenerator();

        // calculate total number of permutations
        long total = Generator.calculatePermutations();

        // determine remaining amount of permutations
        long remaining = getRemainingPermutations();

        // determine if its possible to create the requested amount of ids
        if (remaining < amount) {
            Logger.error("Not enough remaining Permutations, "
                    + "Requested Amount=" + amount + " --> "
                    + "Amount Remaining=" + remaining);
            throw new NotEnoughPermutationsException(remaining, amount);
        }
        Logger.info("request is valid");

        /* 
         if the current setting is random, have the generator return a random set,
         otherwise, have the generator return a sequential set
         */
        Set<Pid> set = (CurrentDefaultSetting.isRandom()) ? Generator.randomMint(amount)
                : Generator.sequentialMint(amount);

        // check ids and increment them appropriately
        set = rollIdSet(set, total, amount);

        // add the set of ids to the id table in the database and their formats
        addIdList(set, amount);

        // return the set of ids
        return set;
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
    private Set<Pid> rollIdSet(Set<Pid> set, long totalPermutations, long amount) {
        Logger.info("in rollIdSet");
        // Used to count the number of unique ids. Size methods aren't used because int is returned
        long uniqueIdCounter = 0;

        // Declares and initializes a list that holds unique values.          
        Set<Pid> uniqueList = new TreeSet<>();

        // iterate through every id 
        for (Pid currentId : set) {
            // counts the number of times an id has been rejected
            long counter = 0;

            // continuously increments invalid or non-unique ids
            while (!isValidId(currentId) || uniqueList.contains(currentId)) {
                /* 
                 if counter exceeds totalPermutations, then id has iterated through every 
                 possible permutation. Related format is updated as a quick look-up reference
                 with the number of ids that were inadvertedly been created using other formats.
                 NotEnoughPermutationsException is thrown stating remaining number of ids.
                 */
                if (counter > totalPermutations) {
                    Logger.error("Total number of Permutations Exceeded: Total Permutation Count="
                            + totalPermutations);
                    throw new NotEnoughPermutationsException(uniqueIdCounter, amount);
                }
                currentId.incrementId();
                counter++;
            }
            // unique ids are added to list and uniqueIdCounter is incremented.
            // Size methods aren't used because int is returned
            uniqueIdCounter++;
            uniqueList.add(currentId);
        }
        return uniqueList;
    }

    /**
     * Adds a requested amount of formatted ids to the database.
     *
     * @param list list of ids to check.
     * @param amountCreated Holds the true size of the list as list.size method
     * can only return the maximum possible value of an integer.
     * @param prefix The string that will be at the front of every id.
     * @param tokenType Designates what characters are contained in the id's
     * root.
     * @param sansVowel Designates whether or not the id's root contains vowels.
     * @param rootLength Designates the length of the id's root.
     */
    private void addIdList(Set<Pid> list, long amountCreated) {
        Logger.info("in addIdlIst");

        for (Pid pid : list) {
            PidDao.savePid(pid);
        }

        Logger.info("DatabaseUpdated with new pids");
        // update table format
        recordSettings(amountCreated);

        //Logger.info("Finished; IDs printed to Database");
    }

    /**
     * Attempts to find a UsedSetting based on the currently used DefaultSetting
     *
     * @return Returns a UsedSetting entity if found, null otherwise
     */
    private UsedSetting findUsedSetting() {
        Logger.info("in findUsedSetting");
        UsedSetting setting = new UsedSetting(CurrentDefaultSetting.getPrefix(),
                CurrentDefaultSetting.getTokenType(),
                CurrentDefaultSetting.getCharMap(),
                CurrentDefaultSetting.getRootLength(),
                CurrentDefaultSetting.isSansVowels(),
                0);

        return UsedSettingDao.findUsedSetting(setting);
    }

    /**
     * Attempts to record the setting that were used to create the current set
     * of Pids
     *
     * @param amount The number of PIDs that were created
     */
    private void recordSettings(long amount) {
        Logger.info("in recordSettings");

        UsedSetting entity = findUsedSetting();

        if (entity == null) {
            entity = new UsedSetting(CurrentDefaultSetting.getPrefix(),
                    CurrentDefaultSetting.getTokenType(),
                    CurrentDefaultSetting.getCharMap(),
                    CurrentDefaultSetting.getRootLength(),
                    CurrentDefaultSetting.isSansVowels(),
                    amount);

            UsedSettingDao.save(entity);
        }
        else {
            long previousAmount = entity.getAmount();
            entity.setAmount(previousAmount + amount);
        }
    }

    /**
     * Checks to see if a Pid already exists in the database.
     *
     * @param pid Pid to be checked
     * @return Returns true if a Pid with the same doesn't exist, false
     * otherwise
     */
    private boolean isValidId(Pid pid) {
        Logger.info("in isValidId");
        Pid entity = this.PidDao.findByName(pid.getName());
        return entity == null;
    }

    /**
     * Updates the CurrentDefaultSetting to match the values in the given
     * DefaultSetting.
     *
     * @param newSetting A DefaultSetting object that contains newly requested
     * values     
     */
    @Override
    public void updateCurrentSetting(DefaultSetting newSetting) {
        Logger.info("in updateCurrentSetting");

        CurrentDefaultSetting = DefaultSettingDao.getDefaultSetting();
        CurrentDefaultSetting.setPrepend(newSetting.getPrepend());
        CurrentDefaultSetting.setPrefix(newSetting.getPrefix());
        CurrentDefaultSetting.setCharMap(newSetting.getCharMap());
        CurrentDefaultSetting.setRootLength(newSetting.getRootLength());
        CurrentDefaultSetting.setTokenType(newSetting.getTokenType());
        CurrentDefaultSetting.setAuto(newSetting.isAuto());
        CurrentDefaultSetting.setRandom(newSetting.isRandom());
        CurrentDefaultSetting.setSansVowels(newSetting.isSansVowels());
    }

    /* typical getters and setters */
    @Override
    public DefaultSetting getCurrentSetting() {
        CurrentDefaultSetting = DefaultSettingDao.getDefaultSetting();
        if (CurrentDefaultSetting == null) {

            // create initial default values to be stored in the database
            CurrentDefaultSetting = new DefaultSetting("", // prepend
                    "", // prefix
                    TokenType.DIGIT, // token type
                    "ddddd", // charmap
                    5, // rootlength
                    true, // sans vowel
                    true, // is auto
                    true); // is random

            DefaultSettingDao.save(CurrentDefaultSetting);
        }
        return CurrentDefaultSetting;
    }

    public void setCurrentSetting(DefaultSetting CurrentSetting) {
        this.CurrentDefaultSetting = CurrentSetting;
    }

}
