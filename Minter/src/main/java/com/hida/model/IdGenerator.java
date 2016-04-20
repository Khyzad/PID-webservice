package com.hida.model;

import java.security.SecureRandom;
import java.util.HashMap;
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
     * Contains the range of all the digits
     */
    protected final static String DIGIT_TOKEN = "0123456789";

    /**
     * Contains the range of the English alphabet without vowels and y.
     */
    protected final static String SANS_VOWEL_TOKEN = "bcdfghjklmnpqrstvwxz";

    /**
     * Contains the range of the English alphabet with vowels and y.
     */
    protected final static String VOWEL_TOKEN = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Logger; logfile to be stored in resource folder
     */
    protected static final Logger Logger = LoggerFactory.getLogger(IdGenerator.class);

    /**
     * Contains the mappings for either tokens or the charMaps. The AutoMinter
     * constructor will assign token mappings. The CustomMinter constructor will
     * assign character mappings. The values will depend on whether or sansVowel
     * specified in the constructor.
     */
    protected final HashMap<Object, String> BaseMap = new HashMap<>();

    /**
     * The string that will be at the front of every id
     */
    protected String Prefix;

    /**
     * A variable that will affect whether or not vowels have the possibility of
     * being included in each id.
     */
    protected boolean SansVowel;

    /**
     * missing javadoc
     *
     * @param prefix
     * @param sansVowel
     */
    public IdGenerator(String prefix, boolean sansVowel) {
        this.Prefix = prefix;
        this.SansVowel = sansVowel;
    }

    public abstract Set<Pid> randomMint(long amount);

    public abstract Set<Pid> sequentialMint(long amount);

    public abstract long calculatePermutations();   

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
        return charMap.matches("^[dlume]*$");
    }

    /* typical getter and setter methods */
    public String getPrefix() {
        return Prefix;
    }

    public void setPrefix(String Prefix) {
        this.Prefix = Prefix;
    }

    public boolean isSansVowel() {
        return SansVowel;
    }

    public void setSansVowel(boolean SansVowel) {
        this.SansVowel = SansVowel;
    }
}
