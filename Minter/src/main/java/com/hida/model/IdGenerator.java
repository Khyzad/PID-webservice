package com.hida.model;

import java.security.SecureRandom;
import java.util.Set;
import java.util.LinkedHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract Id generator used to create Pids
 *
 * @author Brittany Cruz
 * @author lruffin
 */
public abstract class IdGenerator {

    protected long maxPermutation_;

    /**
     * Creates and new random number generator to aid in the production of
     * non-deterministic ids.
     */
    private static final SecureRandom rng_ = new SecureRandom();

    /**
     * LOGGER; logfile to be stored in resource folder
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IdGenerator.class);

    /**
     * The string that will be at the front of every id
     */
    protected String prefix_;

    public IdGenerator(String prefix) {
        this.prefix_ = prefix;
    }

    public abstract long getMaxPermutation();

    public abstract void incrementPid(Pid pid);

    protected abstract String longToName(long value);

    protected abstract long PidToLong(Pid pid);

    /**
     * Creates Pids without regard to a natural order.
     *
     * @param amount The number of Pids to be created
     * @return A set of Pids
     */
    public Set<Pid> randomMint(long amount) {
        // checks to see if its possible to produce or add requested amount of
        if (maxPermutation_ < amount) {
            throw new NotEnoughPermutationsException(maxPermutation_, amount);
        }
        
        // generate longs               
        Set<Long> longSet = new LinkedHashSet<>();
        for (int i = 0; i < amount; i++) {
            long value = Math.abs(rng_.nextLong()) % maxPermutation_;
            
            // create value and add it to the set
            while (!longSet.add(value)) {
                value = (value + 1) % maxPermutation_;
            }            
        }
        
        // convert the longs into Pids
        Set<Pid> pidSet = new LinkedHashSet<>();
        for (long l : longSet){
            pidSet.add(new Pid(longToName(l)));
        }        

        return pidSet;
    }

    /**
     * Creates Pids in ascending order
     *
     * @param amount The number of PIDs to be created
     * @return A set of Pids
     */
    public Set<Pid> sequentialMint(long amount) {
        // checks to see if its possible to produce or add requested amount of
        if (maxPermutation_ < amount) {
            throw new NotEnoughPermutationsException(maxPermutation_, amount);
        }

        // create a set to contain Pids
        Set<Pid> pidSet = new LinkedHashSet<>();

        long startVal = 0;
        for (int i = 0; i < amount; i++) {
            Pid newPid = new Pid(longToName(startVal));
            pidSet.add(newPid);
            startVal++;

            LOGGER.trace("Generated Custom Sequential ID: {}", newPid);
        }
        return pidSet;
    }

    /**
     * Creates Pids in ascending order starting at an arbitrary value.
     *
     * @param amount The number of Pids to be created
     * @param startingValue The value to start sequentially generating Pids
     * @return A set of Pids
     */
    public Set<Pid> sequentialMint(long amount, long startingValue) {
        if (maxPermutation_ < amount) {
            throw new NotEnoughPermutationsException(maxPermutation_, amount);
        }

        // create a set to contain Pids
        Set<Pid> pidSet = new LinkedHashSet<>();
        for (int i = 0; i < amount; i++) {
            Pid newPid = new Pid(longToName(startingValue));
            pidSet.add(newPid);
            startingValue = (startingValue + 1) % maxPermutation_;
            
            LOGGER.trace("Generated Custom Sequential ID: {}", newPid);
        }

        return pidSet;
    }

    /**
     * Checks whether or not the prefix is valid.
     *
     * @param prefix The string that will be at the front of every id.
     * @return true if it contains numbers and letters and does not exceed 20
     * characters.
     */
    public static final boolean isValidPrefix(String prefix) {
        return prefix.matches("^[0-9a-zA-Z]*$") && prefix.length() <= 20;
    }

    /**
     * Checks whether or not the requested amount is valid.
     *
     * @param amount The amount of ids requested.
     * @return True if amount is non-negative.
     */
    public static final boolean isValidAmount(long amount) {
        return amount >= 0;
    }

    /**
     * Checks whether or not the requested root length is valid
     *
     * @param rootLength Designates the length of the id's root.
     * @return True if rootLength is non-negative and less than or equal to 10.
     */
    public static final boolean isValidRootLength(long rootLength) {
        return rootLength >= 0 && rootLength <= 10;
    }

    /**
     * Checks whether or not the given charMap is valid for this minter.
     *
     * @param charMap The mapping used to describe range of possible characters
     * at each of the id's root's digits.
     * @return True if charMap only contains the characters: 'd', 'l', 'u', 'm',
     * or 'e'.
     */
    public static final boolean isValidCharMap(String charMap) {
        return charMap.matches("^[dlume]+$");
    }

    /* typical getter and setter methods */
    public String getPrefix() {
        return prefix_;
    }

    public void setPrefix(String Prefix) {
        this.prefix_ = Prefix;
    }
}
