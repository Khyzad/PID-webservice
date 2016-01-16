package com.hida;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minter class creates formatted ids in json using a variety of parameters.
 *
 * @author Brittany Cruz
 * @author lruffin
 */
public abstract class TestMinter {

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
    protected static final Logger Logger = LoggerFactory.getLogger(TestMinter.class);               

    /**
     * Contains the mappings for either tokens or the charMaps. The AutoMinter
     * constructor will assign token mappings. The CustomMinter constructor will
     * assign character mappings. The values will depend on whether or sansVowel
     * specified in the constructor.
     */
    protected final HashMap<Object, String> BaseMap = new HashMap<>();

    /**
     * This value is not added to the database, however this will be displayed.
     * The value is usually used to determine the type of format, if requested,
     * of the id.
     */
    protected String Prepend;

    
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
     * @param prepend
     * @param prefix
     * @param sansVowel 
     */
    public TestMinter(String prepend, String prefix, boolean sansVowel){
        this.Prepend = prepend;
        this.Prefix = prefix;
        this.SansVowel = sansVowel;
    }
    
    public abstract Set<Id> randomMint(long amount);
    public abstract Set<Id> sequentialMint(long amount);
    
    /**
     * missing javadoc
     * @param set
     * @return 
     */
    public Set<Id> randomizeIdSet(Set<Id> set){
        return set;
    }
    
    /**
     * missing javadoc
     * @param set
     * @return 
     */
    public Set<Id> incrementIdSet(Set<Id> set){
        return set;
    }
            
    /**
     * Constructor for AutoMinters. In an AutoMinter scheme, the generation of ids
     * is dependent on the parameter RootLength and TokenType. The AutoMinter 
     * generates a sequence of characters equal to the root length. The type of characters
     * used is based on TokenType.
     *
     * @param DatabaseManager Used to add and check ids against database.
     * @param Prepend Designates the format of the id. Will not appear in
     * database.
     * @param TokenType
     * @param RootLength Designates the length of the id's root
     * @param Prefix The string that will be at the front of every id
     * @param sansVowel Designates whether or not the id's root contains vowels.
     * If the root does not contain vowels, the sansVowel is true; false
     * otherwise.
     * @throws BadParameterException thrown whenever a malformed or invalid
     * parameter is passed
     
    public TestMinter(DatabaseManager DatabaseManager, String Prepend, TokenType TokenType,
            int RootLength, String Prefix, boolean sansVowel) throws BadParameterException {        
        this.Prepend = Prepend;
        this.SansVowel = sansVowel;

        // checks the validity of the parameters, throw an exception if they aren't
        if (isValidRootLength(RootLength)) {
            this.RootLength = RootLength;
        } else {
            Logger.error("Error with Rootlength of: "+RootLength);
            throw new BadParameterException(RootLength, "RootLength");
        }
        if (isValidPrefix(Prefix)) {
            this.Prefix = Prefix;
        } else {
            Logger.error("Error with Prefix of: "+Prefix);
            throw new BadParameterException(Prefix, "Prefix");
        }        
            this.TokenType = TokenType;
            
        // assign base map the appropriate values
        this.BaseMap.put(TokenType.DIGIT, DIGIT_TOKEN);
        if (sansVowel) {
            this.BaseMap.put(TokenType.LOWERCASE, SANS_VOWEL_TOKEN);
            this.BaseMap.put(TokenType.UPPERCASE, SANS_VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put(TokenType.MIXEDCASE, 
                    SANS_VOWEL_TOKEN + SANS_VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put(TokenType.LOWER_EXTENDED, DIGIT_TOKEN + SANS_VOWEL_TOKEN);
            this.BaseMap.put(TokenType.UPPER_EXTENDED, 
                    DIGIT_TOKEN + SANS_VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put(TokenType.MIXED_EXTENDED,
                    DIGIT_TOKEN + SANS_VOWEL_TOKEN + SANS_VOWEL_TOKEN.toUpperCase());
        } else {
            this.BaseMap.put(TokenType.LOWERCASE, VOWEL_TOKEN);
            this.BaseMap.put(TokenType.UPPERCASE, VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put(TokenType.MIXEDCASE, VOWEL_TOKEN + VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put(TokenType.LOWER_EXTENDED, DIGIT_TOKEN + VOWEL_TOKEN);
            this.BaseMap.put(TokenType.UPPER_EXTENDED, DIGIT_TOKEN + VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put(TokenType.MIXED_EXTENDED,
                    DIGIT_TOKEN + VOWEL_TOKEN + VOWEL_TOKEN.toUpperCase());
        }
        Logger.info("BaseMap values set to: "+BaseMap);
    }
*/
    /**
     * Constructor for CustomMinters. In a CustomMinter scheme, the generation of ids
     * is dependent on the parameter CharMap. The CustomMinter will generate as many
     * characters as the length of CharMap. Each letter of the CharMap dictates the
     * range of characters in it's position.
     *
     * @param DatabaseManager Used to add and check ids against database.
     * @param CharMap The mapping used to describe range of possible characters
     * at each of the id's root's digits
     * @param Prepend Designates the format of the id. Will not appear in
     * database.
     * @param Prefix The string that will be at the front of every id
     * @param sansVowel Designates whether or not the id's root contains vowels.
     * If the root does not contain vowels, the sansVowel is true; false
     * otherwise.
     * @throws BadParameterException thrown whenever a malformed or invalid
     * parameter is passed
     
    public TestMinter(DatabaseManager DatabaseManager, String Prepend, String CharMap, String Prefix,
            boolean sansVowel) throws BadParameterException {        
        this.Prepend = Prepend;
        this.Prefix = Prefix;
        this.RootLength = CharMap.length();
        this.SansVowel = sansVowel;
        this.TokenType = convertToToken(CharMap);

        // checks the validity of the parameters, throw an exception if they aren't
        if (isValidCharMap(CharMap)) {
            this.CharMap = CharMap;
        } else {
            throw new BadParameterException(CharMap, "Char Map");
        }
        if (isValidPrefix(Prefix)) {
            this.Prefix = Prefix;
        } else {
            throw new BadParameterException(Prefix, "Prefix");
        }

        // assign base map the appropriate values
        if (sansVowel) {
            this.BaseMap.put("d", DIGIT_TOKEN);
            this.BaseMap.put("l", SANS_VOWEL_TOKEN);
            this.BaseMap.put("u", SANS_VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put("m", SANS_VOWEL_TOKEN + SANS_VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put("e", DIGIT_TOKEN + SANS_VOWEL_TOKEN + SANS_VOWEL_TOKEN.toUpperCase());
        } else {
            this.BaseMap.put("d", DIGIT_TOKEN);
            this.BaseMap.put("l", VOWEL_TOKEN);
            this.BaseMap.put("u", VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put("m", VOWEL_TOKEN + VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put("e", DIGIT_TOKEN + VOWEL_TOKEN + VOWEL_TOKEN.toUpperCase());
        }
        Logger.info("BaseMap values set to: "+BaseMap);
    }
*/
        
    public Set<Id> incrementIds(Set<Id> set, Set<Id> duplicateSet, long totalPermutations){
        Set<Id> uniqueSet = new TreeSet<>();
        for(Id id : set){
            int counter = 0;
            while((!id.isUnique() || uniqueSet.contains(id) || duplicateSet.contains(id)) ||
                    counter <= totalPermutations){
                id.incrementId();
                counter++;
            }
            
        }
    }
    
    /**
     * Continuously increments a set of ids until the set is completely filled
     * with unique ids.
     *
     * @param original the original set of ds
     * @param order determines whether or not the ids will be ordered
     * @param isAuto determines whether or not the ids are AutoId or CustomId
     * @param amount the amount of ids to be created.
     * @return A set of unique ids
     * @throws SQLException - thrown whenever there is an error with the
     * database.
     */
    protected Set<Id> incremnetIds(Set<Id> original, boolean order, long totalPermutations, long amount)
            throws SQLException, NotEnoughPermutationsException {
        

        // Used to count the number of unique ids. Size methods aren't used because int is returned
        long uniqueIdCounter = 0;

        /* 
         Declares and initializes a list that holds unique values. 
         If order matters, tree set is used.
         */
        Set<Id> uniqueList;
        if (order) {
            uniqueList = new TreeSet<>();
        } else {
            uniqueList = new LinkedHashSet<>(original.size());
        }

        // iterate through every id 
        for (Id currentId : original) {
            // counts the number of times an id has been rejected
            long counter = 0;

            // continuously increments invalid or non-unique ids
            while (!DatabaseManager.isValidId(currentId) || uniqueList.contains(currentId)) {
                /* 
                 if counter exceeds totalPermutations, then id has iterated through every 
                 possible permutation. Related format is updated as a quick look-up reference
                 with the number of ids that were inadvertedly been created using other formats.
                 NotEnoughPermutationsException is thrown stating remaining number of ids.
                 */
                if (counter > totalPermutations) {
                    long amountTaken = totalPermutations - uniqueIdCounter;

                    
                    Logger.error("Total number of Permutations Exceeded: Total Permutation COunt="+totalPermutations);
                    DatabaseManager.setAmountCreated(
                            Prefix, TokenType, SansVowel, RootLength, amountTaken);
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
     * Generates random ids automatically based on root length. The contents of
     * the ids are determined by the tokenType.
     *
     * @param amount the amount of ids to create
     * @return a JSON list of unique ids.
     * @throws SQLException - thrown whenever there is an error with the
     * database
     * @throws BadParameterException thrown whenever a malformed or invalid
     * parameter is passed
     * @throws java.io.IOException
     
    public Set<Id> genIdAutoRandom(long amount) throws SQLException, IOException,
            NotEnoughPermutationsException, BadParameterException {
        
        String tokenMap = BaseMap.get(TokenType);

        Set<Id> tempIdList = new LinkedHashSet((int) amount);

        for (int i = 0; i < amount; i++) {
            int[] tempIdBaseMap = new int[RootLength];
            for (int j = 0; j < RootLength; j++) {
                tempIdBaseMap[j] = Rng.nextInt(tokenMap.length());
            }
            Id currentId = new AutoId(Prefix, tempIdBaseMap, tokenMap);
            Logger.info("Generated Auto Random ID: "+currentId);
            
            while (tempIdList.contains(currentId)) {
                currentId.incrementId();
            }
            tempIdList.add(currentId);
        }
        
        
        return tempIdList;        
    }
*/
    /**
     * Sequentially generates ids automatically based on root length. The
     * contents of the ids are determined by the tokenType.
     *
     * @param amount amount of ids to create
     * @return a JSON list of unique ids.
     * @throws SQLException thrown whenever there is an error with the database
     * @throws java.io.IOException
     * @throws BadParameterException thrown whenever a malformed or invalid
     * parameter is passed
     
    public Set<Id> genIdAutoSequential(long amount)
            throws SQLException, IOException, BadParameterException,
            NotEnoughPermutationsException {
        
        //Logger.info("in genIdAutoSequential: " + amount);
        

        // checks to see if its possible to produce or add requested amount of
        // ids to database
        String tokenMap = BaseMap.get(TokenType);

        Set<Id> tempIdList = new TreeSet();

        int[] previousIdBaseMap = new int[RootLength];
        AutoId firstId = new AutoId(Prefix, previousIdBaseMap, tokenMap);
        Logger.info("Generated Auto Sequential ID: "+firstId);
        tempIdList.add(firstId);

        for (int i = 0; i < amount - 1; i++) {
            AutoId currentId = new AutoId(firstId);
            Logger.info("Generated Auto Sequential ID: "+currentId);
            currentId.incrementId();
            tempIdList.add(currentId);
            firstId = new AutoId(currentId);
        }
        
        return tempIdList;        
    }
*/
    /**
     * Creates random ids based on charMaping.
     *
     * @param amount amount of ids to create
     * @return a JSON list of unique ids.
     * @throws SQLException thrown whenever there is an error with the database
     * @throws java.io.IOException
     * @throws BadParameterException thrown whenever a malformed or invalid
     * parameter is passed
     
    public Set<Id> genIdCustomRandom(long amount)
            throws SQLException, IOException, BadParameterException,
            NotEnoughPermutationsException {
        
        String[] tokenMapArray = getBaseCharMapping();
              
        Set<Id> tempIdList = new LinkedHashSet((int) amount);

        for (int i = 0; i < amount; i++) {
            int[] tempIdBaseMap = new int[RootLength];
            for (int j = 0; j < RootLength; j++) {
                tempIdBaseMap[j] = Rng.nextInt(tokenMapArray[j].length());
            }
            Id currentId = new CustomId(Prefix, tempIdBaseMap, tokenMapArray);
            Logger.info("Generated Custom Random ID: "+currentId);
            while (!tempIdList.add(currentId)) {
                currentId.incrementId();
            }
        }
        
        return tempIdList;

    }
*/
    /**
     * Sequentially generates ids based on char mapping.
     *
     * @param amount amount of ids to create
     * @return a JSON list of unique ids.
     * @throws SQLException thrown whenever there is an error with the database
     * @throws java.io.IOException
     * @throws com.hida.BadParameterException
     
    public Set<Id> genIdCustomSequential(long amount)
            throws SQLException, IOException, BadParameterException, NotEnoughPermutationsException {
        
        //Logger.info("in genIdCustomSequential: " + amount);
        if (!isValidAmount(amount)) {
            //Logger.error("Amount Requested"+amount);
            throw new BadParameterException(amount, "Amount Requested");
        }
        // checks to see if its possible to produce or add requested amount of
        // ids to database
        String[] tokenMapArray = getBaseCharMapping();

        Set<Id> tempIdList = new TreeSet();

        int[] previousIdBaseMap = new int[RootLength];
        CustomId firstId = new CustomId(Prefix, previousIdBaseMap, tokenMapArray);
        Logger.info("Custom Sequential ID Generated: "+firstId);
        tempIdList.add(firstId);

        for (int i = 0; i < amount - 1; i++) {
            CustomId currentId = new CustomId(firstId);
            Logger.info("Custom Sequential ID Generated: "+currentId);
            currentId.incrementId();
            tempIdList.add(currentId);
            firstId = new CustomId(currentId);
        }
        
        return tempIdList;
        
    }
*/
    /**
     * This method returns an equivalent token for any given charMap
     *
     * @param charMap The mapping used to describe range of possible characters
     * at each of the id's root's digits
     * @return the token equivalent to the charMap
     * @throws BadParameterException thrown whenever a malformed or invalid
     * parameter is passed
     */
    private TokenType convertToToken(String charMap) throws BadParameterException {

        // true if charMap only contains character 'd'
        if (charMap.matches("^[d]+$")) {
            return TokenType.DIGIT;
        } // true if charMap only contains character 'l'.
        else if (charMap.matches("^[l]+$")) {
            return TokenType.LOWERCASE;
        } // true if charMap only contains character 'u'
        else if (charMap.matches("^[u]+$")) {
            return TokenType.UPPERCASE;
        } // true if charMap only contains character groups 'lu' or 'm'
        else if (charMap.matches("(^(?=[lum]*l)(?=[lum]*u)[lum]*$)" + "|"
                + "(^(?=[lum]*m)[lum]*$)")) {
            return TokenType.MIXEDCASE;
        } // true if charMap only contains characters 'dl'
        else if (charMap.matches("(^(?=[dl]*l)(?=[ld]*d)[dl]*$)")) {
            return TokenType.LOWER_EXTENDED;
        } // true if charMap only contains characters 'du'
        else if (charMap.matches("(^(?=[du]*u)(?=[du]*d)[du]*$)")) {
            return TokenType.UPPER_EXTENDED;
        } // true if charMap at least contains character groups 'dlu' or 'md' or 'e' respectively
        else if (charMap.matches("(^(?=[dlume]*d)(?=[dlume]*l)(?=[dlume]*u)[dlume]*$)" + "|"
                + "(^(?=[dlume]*m)(?=[dlume]*d)[dlume]*$)" + "|"
                + "(^(?=[dlume]*e)[dlume]*$)")) {
            return TokenType.MIXED_EXTENDED;
        } else {
            throw new BadParameterException(charMap, "detected in getToken method");
        }
    }

    /**
     * Creates an array that stores a range of characters that designates a
     * sequence of possible characters at that specific location.
     *
     * @return the range of characters.
     
    private String[] getBaseCharMapping() {
        String[] baseTokenMapArray = new String[CharMap.length()];
        for (int i = 0; i < CharMap.length(); i++) {
            // more efficient than charMap.charAt(i) + ""
            String c = String.valueOf(CharMap.charAt(i));
            baseTokenMapArray[i] = BaseMap.get(c);
        }
        return baseTokenMapArray;
    }
*/
    

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
     * Checks whether or not the given tokenType is valid for this minter.
     *
     * @param tokenType Designates what characters are contained in the id's
     * root.
     * @return True if its equal to one of the pre-defined tokens.
     
    public final boolean isValidTokenType(TokenType tokenType) {
        
        return tokenType.equals("DIGIT") || tokenType.equals("LOWERCASE")
                || tokenType.equals("UPPERCASE") || tokenType.equals("MIXEDCASE")
                || tokenType.equals("LOWER_EXTENDED") || tokenType.equals("UPPER_EXTENDED")
                || tokenType.equals("MIXED_EXTENDED");
        
        
    }
*/
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
    public String getPrepend() {
        return Prepend;
    }

    public void setPrepend(String Prepend) {
        this.Prepend = Prepend;
    }

    

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
