package com.hida.service;

import com.hida.dao.DefaultSettingDao;
import com.hida.dao.PidDao;
import com.hida.model.BadParameterException;
import com.hida.model.TokenType;
import com.hida.dao.UsedSettingDao;
import com.hida.model.AutoIdGenerator;
import com.hida.model.CustomIdGenerator;
import com.hida.model.DefaultSetting;
import com.hida.model.Pid;
import com.hida.model.IdGenerator;
import com.hida.model.NotEnoughPermutationsException;
import com.hida.model.UsedSetting;
import java.sql.SQLException;
import java.util.Set;

import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

/**
 * A class used to manage http requests so that data integrity can be maintained
 *
 * @author lruffin
 */
@Service("minterService")
@Transactional
public class MinterServiceImpl implements MinterService {

    // Logger; logfile to be stored in resource folder
    private static final Logger Logger = LoggerFactory.getLogger(MinterServiceImpl.class);
       
    /**
     * missing javadoc
     */
    @Autowired
    private PidDao PidDao;

    /**
     * missing javadoc
     */
    @Autowired
    private UsedSettingDao UsedSettingDao;

    /**
     * missing javadoc
     */
    @Autowired
    private DefaultSettingDao DefaultSettingDao;

    /**
     * Declares a Generator object to manage
     */
    private IdGenerator Generator;

    /**
     * missing javadoc
     */
    private DefaultSetting CurrentSetting;

    

    /**
     * missing javadoc
     */
    public MinterServiceImpl() {
        
    }

    /**
     * Attempts to connect to the database.
     *
     * Upon connecting to the database, the method will try to detect whether or
     * not a table exists. A table is created if it does not already exists.
     *
     * @return true if connection and table creation was successful, false
     * otherwise
     * @throws ClassNotFoundException thrown whenever the JDBC driver is not
     * found
     * @throws SQLException thrown whenever there is an error with the database
     */
    @Override
    public boolean createConnection() throws ClassNotFoundException, SQLException {        
        DefaultSetting defaultEntity = DefaultSettingDao.getDefaultSetting();
        if (defaultEntity == null) {

            // create initial default values to be stored in the database
            defaultEntity = new DefaultSetting("", // prepend
                    "", // prefix
                    TokenType.DIGIT, // token type
                    "ddddd", // charmap
                    5, // rootlength
                    true, // sans vowel
                    true, // is auto
                    true); // is random

            //DefaultSettingDao.save(defaultEntity);
        }
        
        CurrentSetting = defaultEntity;
        return true;
    }

    /**
     * missing javadoc
     *
     * @param prefix
     * @param sansVowel
     * @param tokenType
     * @param rootLength
     * @throws BadParameterException
     */
    public void createAutoMinter(String prefix, boolean sansVowel,
            TokenType tokenType, int rootLength) throws BadParameterException {
        Generator = new AutoIdGenerator(prefix, sansVowel, tokenType, rootLength);
    }

    /**
     * missing javadoc
     *
     * @param prefix
     * @param sansVowel
     * @param charMap
     * @throws BadParameterException
     */
    public void createCustomMinter(String prefix, boolean sansVowel,
            String charMap) throws BadParameterException {
        Generator = new CustomIdGenerator(prefix, sansVowel, charMap);
    }

    /**
     * missing javadoc
     *
     * @return
     * @throws BadParameterException
     */
    private long getRemainingPermutations() throws BadParameterException {
        Logger.info("in getRemainingPerumtations");
        long totalPermutations = Generator.calculatePermutations();
        long amountCreated = getAmountCreated();

        return totalPermutations - amountCreated;
    }

    /**
     * missing javadoc
     *
     * @return
     */
    private long getAmountCreated() {
        UsedSetting entity = findUsedSetting();
        return entity.getAmount();
    }

    /**
     * missing javadoc
     *
     * @param total
     * @param amountCreated
     * @return
     */
    private boolean isValidRequest(long remaining, long amount) {
        Logger.info("in isValidRequest");
        // throw an error if its impossible to generate ids 
        if (remaining < amount) {
            Logger.info("request is invalid");
            /*
            Logger.error("Not enough remaining Permutations, "
                    + "Requested Amount=" + amount + " --> "
                    + "Amount Remaining=" + remaining);
            */
            throw new NotEnoughPermutationsException(remaining, amount);

        }
        Logger.info("request is valid");
        return remaining < amount;
    }

    /**
     * missing javadoc
     */
    private void createGenerator() {
        Logger.info("in createGenerator");
        if (CurrentSetting.isAuto()) {
            Generator = new AutoIdGenerator(
                    CurrentSetting.getPrefix(),
                    CurrentSetting.isSansVowels(),
                    CurrentSetting.getTokenType(),
                    CurrentSetting.getRootLength());
            
            Logger.info("AutoGenerator created");
        } else {
            Generator = new CustomIdGenerator(
                    CurrentSetting.getPrefix(),
                    CurrentSetting.isSansVowels(),
                    CurrentSetting.getCharMap());
            Logger.info("CustomIdGenerator created");
        }
    }

    /**
     * missing javadoc
     *
     * @param amount
     * @param setting
     * @return
     * @throws SQLException
     * @throws BadParameterException
     */
    @Override
    public Set<Pid> mint(long amount, DefaultSetting setting) throws SQLException, BadParameterException {
        Logger.info("in mint");
        
        // store the desired setting values 
        this.CurrentSetting = setting;

        // create appropriate generator
        createGenerator();

        // calculate total number of permutations
        long total = Generator.calculatePermutations();

        // determine remaining amount of permutations
        long remaining = getRemainingPermutations();
        
        // determine if its possible to create the requested amount of ids
        if (isValidRequest(remaining, amount)) {
            Logger.error("Not enough remaining Permutations, "
                    + "Requested Amount=" + amount + " --> "
                    + "Amount Remaining=" + remaining);
            throw new NotEnoughPermutationsException(remaining, amount);
        }

        /* 
         if the current setting is random, have the generator return a random set,
         otherwise, have the generator return a sequential set
         */
        Set<Pid> set = (CurrentSetting.isRandom()) ? Generator.randomMint(amount)
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
     * @return A set of unique ids
     * @throws SQLException - thrown whenever there is an error with the
     * database.
     */
    private Set<Pid> rollIdSet(Set<Pid> set, long totalPermutations, long amount)
            throws SQLException, NotEnoughPermutationsException, BadParameterException {
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
     * @throws SQLException thrown whenever there is an error with the database
     * @throws BadParameterException thrown whenever a malformed or invalid
     * parameter is passed
     */
    private void addIdList(Set<Pid> list, long amountCreated) throws SQLException, BadParameterException {
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
     * missing javadoc
     *
     * @return
     */
    private UsedSetting findUsedSetting() {
        Logger.info("in findUsedSetting");
        return UsedSettingDao.findUsedSetting(CurrentSetting.getPrefix(),
                CurrentSetting.getTokenType(),
                CurrentSetting.getCharMap(),
                CurrentSetting.getRootLength(),
                CurrentSetting.isSansVowels()).get(0);
    }

    /**
     * missing javadoc
     *
     * @param amount
     * @param setting
     */
    private void recordSettings(long amount) {
        Logger.info("in recordSettings");
        
        UsedSetting entity = findUsedSetting();

        if (entity == null) {
            entity = new UsedSetting(CurrentSetting.getPrefix(),
                    CurrentSetting.getTokenType(),
                    CurrentSetting.getCharMap(),
                    CurrentSetting.getRootLength(),
                    CurrentSetting.isSansVowels(),
                    amount);

            UsedSettingDao.save(entity);
        } else {
            long previousAmount = entity.getAmount();
            entity.setAmount(previousAmount + amount);
        }
    }   
   

    /**
     * missing javadocs
     * @param pid
     * @return 
     */
    private boolean isValidId(Pid pid){
        Logger.info("in isValidId");
        Pid entity = this.PidDao.findByName(pid.getName());
        return entity != null;        
    }            
       
           
    @Override
    public void updateCurrentSetting(DefaultSetting newSetting){
        Logger.info("in updateCurrentSetting");
        CurrentSetting.setPrepend(newSetting.getPrepend());
        CurrentSetting.setPrefix(newSetting.getPrefix());
        CurrentSetting.setCharMap(newSetting.getCharMap());
        CurrentSetting.setRootLength(newSetting.getRootLength());
        CurrentSetting.setTokenType(newSetting.getTokenType());
        CurrentSetting.setAuto(newSetting.isAuto());
        CurrentSetting.setRandom(newSetting.isRandom());
        CurrentSetting.setSansVowels(newSetting.isSansVowels());
    }
    
    
    /**
     * Used by an external method to close this connection.
     *
     * @return
     * @throws SQLException thrown whenever there is an error with the database
     */
    @Override
    public boolean closeConnection() throws SQLException {
        Logger.info("DB Connection Closed");
        return true;
    }
    
    
    /* typical getters and setters */
    @Override
    public DefaultSetting getCurrentSetting() {
        return CurrentSetting;
    }

    public void setCurrentSetting(DefaultSetting CurrentSetting) {
        this.CurrentSetting = CurrentSetting;
    }       

}
