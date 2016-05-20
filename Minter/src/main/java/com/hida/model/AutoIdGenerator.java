package com.hida.model;

import static com.hida.model.IdGenerator.Rng;
import static com.hida.model.IdGenerator.Logger;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * An Id Generator that creates Pids primarily based on tokenType and
 * rootLength.
 *
 * @author lruffin
 */
public class AutoIdGenerator extends IdGenerator {

    /**
     * Designates what characters are contained in the id's root. There are 7
     * types of token maps, each describing a range of possible characters in
     * the id. This range is further affected by the variable SansVowel.
     *
     * <pre>
     * DIGIT: Digit values only.
     * LOWER_ALPHABET: Lowercase letters only.
     * UPPER_ALPHABET: Uppercase letters only.
     * MIXED_ALPHABET: Lowercase and Uppercase letters only.
     * LOWER_ALPHABET_EXTENDED: Digit values and Lowercase letters only.
     * UPPER_ALPHABET_EXTENDED: Digits and Uppercase letters only
     * MIXED_ALPHABET_EXTENDED: All characters specified by previous tokens
     * </pre>
     *
     */
    private TokenType TokenType;

    /**
     * Designates the length of the id's root.
     */
    private int RootLength;

    /**
     * Default constructor. Aside from TokenType, there are no restrictions
     * placed on the parameters and can be used however one sees fit.
     *
     * @param prefix A sequence of characters that appear in the beginning of
     * PIDs
     * @param sansVowel Dictates whether or not vowels are allowed
     * @param tokenType An enum used to configure PIDS
     * @param rootLength Designates the length of the id's root
     */
    public AutoIdGenerator(String prefix, boolean sansVowel, TokenType tokenType, int rootLength) {
        super(prefix, sansVowel);
        this.TokenType = tokenType;
        this.RootLength = rootLength;

        // assign base map the appropriate values
        this.BaseMap.put(TokenType.DIGIT, DIGIT_TOKEN);
        if (sansVowel) {
            this.BaseMap.put(TokenType.LOWER_ALPHABET, SANS_VOWEL_TOKEN);
            this.BaseMap.put(TokenType.UPPER_ALPHABET, SANS_VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put(TokenType.MIXED_ALPHABET,
                    SANS_VOWEL_TOKEN + SANS_VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put(TokenType.LOWER_ALPHABET_EXTENDED, DIGIT_TOKEN + SANS_VOWEL_TOKEN);
            this.BaseMap.put(TokenType.UPPER_ALPHABET_EXTENDED,
                    DIGIT_TOKEN + SANS_VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put(TokenType.MIXED_ALPHABET_EXTENDED,
                    DIGIT_TOKEN + SANS_VOWEL_TOKEN + SANS_VOWEL_TOKEN.toUpperCase());
        }
        else {
            this.BaseMap.put(TokenType.LOWER_ALPHABET, VOWEL_TOKEN);
            this.BaseMap.put(TokenType.UPPER_ALPHABET, VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put(TokenType.MIXED_ALPHABET, VOWEL_TOKEN + VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put(TokenType.LOWER_ALPHABET_EXTENDED, DIGIT_TOKEN + VOWEL_TOKEN);
            this.BaseMap.put(TokenType.UPPER_ALPHABET_EXTENDED, DIGIT_TOKEN + VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put(TokenType.MIXED_ALPHABET_EXTENDED,
                    DIGIT_TOKEN + VOWEL_TOKEN + VOWEL_TOKEN.toUpperCase());
        }
    }

    /**
     * Creates Pids without regard to a natural order.
     *
     * @param amount The number of PIDs to be created
     * @return A set of Pids
     */
    @Override
    public Set<Pid> randomMint(long amount) {
        // checks to see if its possible to produce or add requested amount of
        long total = calculatePermutations();
        if (total < amount) {
            throw new NotEnoughPermutationsException(total, amount);
        }

        // generate ids
        String tokenMap = BaseMap.get(TokenType);
        Set<Pid> tempIdList = new LinkedHashSet();

        for (int i = 0; i < amount; i++) {
            int[] tempIdBaseMap = new int[RootLength];
            for (int j = 0; j < RootLength; j++) {
                tempIdBaseMap[j] = Rng.nextInt(tokenMap.length());
            }
            Pid currentId = new AutoId(Prefix, tempIdBaseMap, tokenMap);
            Logger.trace("Generated Auto Random ID: " + currentId);

            while (tempIdList.contains(currentId)) {
                currentId.incrementId();
            }
            tempIdList.add(currentId);
        }
        return tempIdList;
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
            throw new NotEnoughPermutationsException(total, amount);
        }

        // generate ids
        String tokenMap = BaseMap.get(TokenType);
        Set<Pid> idSet = new TreeSet();

        int[] previousIdBaseMap = new int[RootLength];
        AutoId currentId = new AutoId(Prefix, previousIdBaseMap, tokenMap);
        for (int i = 0; i < amount; i++) {
            AutoId nextId = new AutoId(currentId);
            idSet.add(currentId);
            Logger.trace("Generated Auto Sequential ID: " + currentId);
            nextId.incrementId();
            currentId = new AutoId(nextId);
        }

        return idSet;
    }

    /**
     * This method calculates and returns the total possible number of
     * permutations using the values given in the constructor.
     *
     * @return number of permutations
     */
    @Override
    public long calculatePermutations() {
        // get the base of each character
        int base = 0;
        switch (TokenType) {
            case DIGIT:
                base = 10;
                break;
            case LOWER_ALPHABET:
            case UPPER_ALPHABET:
                base = (SansVowel) ? 20 : 26;
                break;
            case MIXED_ALPHABET:
                base = (SansVowel) ? 40 : 52;
                break;
            case LOWER_ALPHABET_EXTENDED:
            case UPPER_ALPHABET_EXTENDED:
                base = (SansVowel) ? 30 : 36;
                break;
            case MIXED_ALPHABET_EXTENDED:
                base = (SansVowel) ? 50 : 62;
                break;
        }

        // raise it to the power of how ever long the rootLength is
        return ((long) Math.pow(base, RootLength));
    }

    /**
     * Increments a value of a PID. If the maximum limit is reached the values
     * will wrap around.
     *
     * @param pid The pid to increment
     */
    @Override
    public void incrementPid(Pid pid) {
        int range = TokenType.getCharacters().length() - 1;
        boolean overflow = true;

        // increment the values in a pid's basemap
        for (int k = 0; k <= range && overflow; k++) {
            // record value of current index
            int value = pid.getBaseMap()[range - k];

            // if the last value is reached then wrap around
            if (value == range) {
                pid.getBaseMap()[range - k] = 0;
            }
            // otherwise increment the value at the current index and break the loop
            else {
                pid.getBaseMap()[range - k]++;
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
            Name += TokenType.getCharacters().charAt(pid.getBaseMap()[i]);
        }
        pid.setName(this.getPrefix() + Name);
    }

    /* getters and setters */
    public TokenType getTokenType() {
        return TokenType;
    }

    public void setTokenType(TokenType TokenType) {
        this.TokenType = TokenType;
    }

    public int getRootLength() {
        return RootLength;
    }

    public void setRootLength(int RootLength) {
        this.RootLength = RootLength;
    }
}
