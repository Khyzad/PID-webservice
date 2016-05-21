package com.hida.model;

import static com.hida.model.IdGenerator.Rng;
import static com.hida.model.IdGenerator.Logger;
import java.util.Set;
import java.util.TreeSet;

/**
 * An Id Generator that creates Pids primarily based on a charMap.
 *
 * @author lruffin
 */
public class CustomIdGenerator extends IdGenerator {

    /**
     * The mapping used to describe range of possible characters at each of the
     * id's root's digits. There are total of 5 different ranges that a charMap
     * can contain:
     *
     * <pre>
     * d: digits only
     * l: lower case letters only
     * u: upper case letters only
     * m: letters only
     * e: any valid character specified by d, l, u, and m.
     * </pre>
     *
     */
    private String CharMap;

    private final String[] TokenMap;   

    /**
     * Instantiates an Id Generator that creates Pids primarily based on a
     * charMap. The only valid charMap characters are regex("[dlume]+"). No
     * restrictions are placed on the other parameters.
     *
     * @param prefix A sequence of characters that appear in the beginning of
     * PIDs
     * @param sansVowel Dictates whether or not vowels are allowed
     * @param charMap A sequence of characters used to configure Pids
     */
    public CustomIdGenerator(String prefix, boolean sansVowel, String charMap) {
        super(prefix, sansVowel);
        this.CharMap = charMap;
        this.TokenMap = new String[charMap.length()];
        this.SansVowel = sansVowel;

        initializeTokenMap();        
    }

    /**
     * Creates Pids without regard to a natural order.
     *
     * @param amount The number of Pids to be created
     * @return A set of Pids
     */
    @Override
    public Set<Pid> randomMint(long amount) {
        // checks to see if its possible to produce or add requested amount of
        long total = calculatePermutations();
        if (total < amount) {
            throw new NotEnoughPermutationsException();
        }
        // generate ids        
        Set<Pid> pidSet = new TreeSet<>();

        for (int i = 0; i < amount; i++) {
            int[] tempIdBaseMap = new int[CharMap.length()];
            for (int j = 0; j < CharMap.length(); j++) {
                tempIdBaseMap[j] = Rng.nextInt(TokenMap[j].length());
            }
            Pid currentId = new Pid(tempIdBaseMap, Prefix);
            this.assignName(currentId);
            
            Logger.trace("Generated Custom Random ID: " + currentId);
            while (!pidSet.add(currentId)) {                
                this.incrementPid(currentId);
            }
        }
        return pidSet;
    }

    /**
     * Creates Pids in ascending order
     *
     * @param amount The number of PIDs to be created
     * @return A set of Pids
     */
    @Override
    public Set<Pid> sequentialMint(long amount) {
        // checks to see if its possible to produce or add requested amount of
        long total = calculatePermutations();
        if (total < amount) {
            throw new NotEnoughPermutationsException();
        }

        // generate ids
        Set<Pid> pidSet = new TreeSet<>();

        int[] previousIdBaseMap = new int[CharMap.length()];
        Pid currentId = new Pid(previousIdBaseMap, Prefix); 
        this.assignName(currentId);
        for (int i = 0; i < amount; i++) {
            Pid nextId = new Pid(currentId);
            pidSet.add(currentId);
            Logger.trace("Generated Custom Sequential ID: " + currentId);            
            this.incrementPid(nextId);
            currentId = new Pid(nextId);
        }
        return pidSet;
    }

    /**
     * This method calculates and returns the total possible number of
     * permutations using the values given in the constructor.
     *
     * @return number of permutations
     */
    @Override
    public long calculatePermutations() {
        long totalPermutations = 1;
        for (int i = 0; i < CharMap.length(); i++) {
            if (CharMap.charAt(i) == 'd') {
                totalPermutations *= 10;
            }
            else if (CharMap.charAt(i) == 'l' || CharMap.charAt(i) == 'u') {
                totalPermutations *= (SansVowel) ? 20 : 26;
            }
            else if (CharMap.charAt(i) == 'm') {
                totalPermutations *= (SansVowel) ? 40 : 52;
            }
            else if (CharMap.charAt(i) == 'e') {
                totalPermutations *= (SansVowel) ? 50 : 62;
            }
        }
        return totalPermutations;
    }
    
    /**
     * Initializes TokenMap to contain a String of characters at each index to
     * designate the possible values that can be assigned to each index of a
     * Pid's BaseMap.
     */
    private void initializeTokenMap() {
        for (int i = 0; i < TokenMap.length; i++) {
            // get char
            char c = CharMap.charAt(i);

            // assign each index a string of characters
            if (c == 'd') {
                TokenMap[i] = TokenType.DIGIT.getCharacters();
            }
            else if (c == 'l') {
                TokenMap[i] = (SansVowel) ? TokenType.LOWER_CONSONANTS.getCharacters()
                        : TokenType.LOWER_ALPHABET.getCharacters();
            }
            else if (c == 'u') {
                TokenMap[i] = (SansVowel) ? TokenType.UPPER_CONSONANTS.getCharacters()
                        : TokenType.UPPER_ALPHABET.getCharacters();
            }
            else if (c == 'm') {
                TokenMap[i] = (SansVowel) ? TokenType.MIXED_CONSONANTS.getCharacters()
                        : TokenType.MIXED_ALPHABET.getCharacters();
            }
            else {
                TokenMap[i] = (SansVowel) ? TokenType.MIXED_CONSONANTS_EXTENDED.getCharacters()
                        : TokenType.MIXED_ALPHABET_EXTENDED.getCharacters();
            }
        }
    }

    /**
     * Increments a value of a PID. If the maximum limit is reached the values
     * will wrap around.
     *
     * @param pid The pid to increment
     */
    @Override
    public void incrementPid(Pid pid) {
        boolean overflow = true;

        int lastIndex = pid.getBaseMap().length - 1;
        // increment the values in a pid's basemap
        for (int i = lastIndex; overflow && i >= 0; i--) {

            // if the last value is reached then wrap around
            if (pid.getBaseMap()[i] == TokenMap[i].length() - 1) {
                pid.getBaseMap()[i] = 0;
            }
            // otherwise increment the value at the current index and break the loop
            else {
                pid.getBaseMap()[i]++;
                overflow = false;
            }
        }

        // assign a new name to the Pid based on its base map
        assignName(pid);
    }

    /**
     * Creates and sets a new name for a pid based on its indices contained in
     * the BaseMap and the characters in the TokenType. This should be called
     * whenever the values in a Pid's BaseMap has been changed.
     *
     * @param pid The pid that needs a new name.
     */
    @Override
    protected void assignName(Pid pid) {
        String Name = "";
        for (int i = 0; i < pid.getBaseMap().length; i++) {
            Name += TokenMap[i].charAt(pid.getBaseMap()[i]);
        }
        pid.setName(this.getPrefix() + Name);
    }

    /* getters and setters */
    public String getCharMap() {
        return CharMap;
    }

    public void setCharMap(String CharMap) {
        this.CharMap = CharMap;
    }        
    
}
