package com.hida.model;



import static com.hida.model.IdGenerator.Rng;
import static com.hida.model.IdGenerator.Logger;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
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
     * LOWERCASE: Lowercase letters only.
     * UPPERCASE: Uppercase letters only.
     * MIXEDCASE: Lowercase and Uppercase letters only.
     * LOWER_EXTENDED: Digit values and Lowercase letters only.
     * UPPER_EXTENDED: Digits and Uppercase letters only
     * MIXED_EXTENDED: All characters specified by previous tokens
     * </pre>
     *
     */
    private TokenType TokenType;

    /**
     * Designates the length of the id's root.
     */
    private int RootLength;

    /**
     * missing javadoc
     *
     * @param prefix
     * @param sansVowel
     * @param tokenType
     * @param rootLength
     */
    public AutoIdGenerator(String prefix, boolean sansVowel, TokenType tokenType, int rootLength) {
        super(prefix, sansVowel);
        this.TokenType = tokenType;
        this.RootLength = rootLength;

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
    }

    /**
     * missing javadoc
     *
     * @param amount
     * @return
     */
    @Override
    public Set<Pid> randomMint(long amount) {
        // checks to see if its possible to produce or add requested amount of
        long total = calculatePermutations();
        if (total < amount) {
            throw new NotEnoughPermutationsException();
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
            Logger.info("Generated Auto Random ID: " + currentId);

            while (tempIdList.contains(currentId)) {
                currentId.incrementId();
            }
            tempIdList.add(currentId);
        }
        return tempIdList;
    }

    /**
     * missing javadoc
     *
     * @param amount
     * @return
     */
    @Override
    public Set<Pid> sequentialMint(long amount) {
        //Logger.info("in genIdAutoSequential: " + amount);        

        // checks to see if its possible to produce or add requested amount of
        long total = calculatePermutations();
        if (total < amount) {
            throw new NotEnoughPermutationsException();
        }

        // generate ids
        String tokenMap = BaseMap.get(TokenType);
        Set<Pid> idSet = new TreeSet();

        int[] previousIdBaseMap = new int[RootLength];        
        AutoId currentId = new AutoId(Prefix, previousIdBaseMap, tokenMap);
        for (int i = 0; i < amount; i++) {
            AutoId nextId = new AutoId(currentId);
            idSet.add(currentId);
            Logger.info("Generated Auto Sequential ID: " + currentId);
            nextId.incrementId();            
            currentId = new AutoId(nextId);            
        }

        return idSet;
    }

    /**
     * missing javadoc
     *
     * @return
     */
    @Override
    public long calculatePermutations() {
        // get the base of each character
        int base = 0;
        switch (TokenType) {
            case DIGIT:
                base = 10;
                break;
            case LOWERCASE:
            case UPPERCASE:
                base = (SansVowel) ? 20 : 26;
                break;
            case MIXEDCASE:
                base = (SansVowel) ? 40 : 52;
                break;
            case LOWER_EXTENDED:
            case UPPER_EXTENDED:
                base = (SansVowel) ? 30 : 36;
                break;
            case MIXED_EXTENDED:
                base = (SansVowel) ? 50 : 62;
                break;
        }

        // raise it to the power of how ever long the rootLength is
        return ((long) Math.pow(base, RootLength));
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

    /**
     * Created and used by AutoIdGenerator
     */
    private class AutoId extends Pid {

        private String TokenMap;

        public AutoId(AutoId id) {
            super(id);
            this.TokenMap = id.getTokenMap();
        }

        public AutoId(String prefix, int[] baseMap, String tokenMap) {
            super(baseMap, prefix);
            this.TokenMap = tokenMap;
        }

        /**
         * missing javadoc
         *
         * @return
         */
        @Override
        public boolean incrementId() {
            int range = this.getBaseMap().length - 1;

            boolean overflow = true;
            for (int k = 0; k < this.getBaseMap().length && overflow; k++) {
                // record value of current index
                int value = this.getBaseMap()[range - k];

                if (value == TokenMap.length() - 1) {
                    this.getBaseMap()[range - k] = 0;
                } else {
                    this.getBaseMap()[range - k]++;
                    overflow = false;
                }
            }

            return !overflow;
        }

        /**
         * missing javadoc
         *
         * @return
         */
        @Override
        public String getRootName() {
            String charId = "";
            for (int i = 0; i < this.getBaseMap().length; i++) {
                charId += TokenMap.charAt(this.getBaseMap()[i]);
            }
            return charId;
        }

        @Override
        public String toString() {
            return Prefix + this.getRootName();
        }

        // getters and setters
        public String getTokenMap() {
            return TokenMap;
        }

        public void setTokenMap(String TokenMap) {
            this.TokenMap = TokenMap;
        }

    }
}
