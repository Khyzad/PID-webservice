package com.hida.model;

import java.security.SecureRandom;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract Id generator that all potential Pid generators should extend. 
 *
 * @author Brittany Cruz
 * @author lruffin
 */
public abstract class IdGenerator {

    /**
     * Creates and new random number generator to aid in the production of
     * non-deterministic ids.
     */
    protected static final SecureRandom Rng = new SecureRandom();   

    /**
     * LOGGER; logfile to be stored in resource folder
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(IdGenerator.class);   

    /**
     * The string that will be at the front of every id
     */
    protected String Prefix;   

    /**
     * missing javadoc
     *
     * @param prefix
     */
    public IdGenerator(String prefix) {
        this.Prefix = prefix;
    }

    public abstract Set<Pid> randomMint(long amount);

    public abstract Set<Pid> sequentialMint(long amount);

    public abstract long calculatePermutations();   
    
    public abstract void incrementPid(Pid pid);
    
    protected abstract void assignName(Pid pid);

    /**
     * Checks whether or not the prefix is valid.
     *
     * @param prefix The string that will be at the front of every id.
     * @return true if it contains numbers and letters and does not exceed 20
     * characters.
     */
    public final boolean isValidPrefix(String prefix) {
        return prefix.matches("^[0-9a-zA-Z]*$") && prefix.length() <= 20;
    }

    /**
     * Checks whether or not the requested amount is valid.
     *
     * @param amount The amount of ids requested.
     * @return True if amount is non-negative.
     */
    public final boolean isValidAmount(long amount) {
        return amount >= 0;
    }

    /**
     * Checks whether or not the requested root length is valid
     *
     * @param rootLength Designates the length of the id's root.
     * @return True if rootLength is non-negative and less than or equal to 10.
     */
    public final boolean isValidRootLength(long rootLength) {
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
    public final boolean isValidCharMap(String charMap) {
        return charMap.matches("^[dlume]+$");
    }

    /* typical getter and setter methods */
    public String getPrefix() {
        return Prefix;
    }

    public void setPrefix(String Prefix) {
        this.Prefix = Prefix;
    }   
}
