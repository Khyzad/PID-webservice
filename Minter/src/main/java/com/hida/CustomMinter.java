/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hida;

import static com.hida.TestMinter.Rng;
import static com.hida.TestMinter.Logger;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author lruffin
 */
public class CustomMinter extends TestMinter {

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
     *
     */
    private String CharMap;

    /**
     * missing javadoc
     *
     * @param prepend
     * @param prefix
     * @param sansVowel
     * @param charMap
     */
    public CustomMinter(String prepend, String prefix, boolean sansVowel, String charMap) {
        super(prepend, prefix, sansVowel);
        this.CharMap = charMap;

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
        Logger.info("BaseMap values set to: " + BaseMap);
    }

    /**
     * missing javadoc
     *
     * @param amount
     * @return
     */
    @Override
    public Set<Id> sequentialMint(long amount) {
        //Logger.info("in genIdCustomSequential: " + amount);

        // checks to see if its possible to produce or add requested amount of
        // ids to database
        String[] tokenMapArray = getBaseCharMapping();

        Set<Id> tempIdList = new TreeSet();

        int[] previousIdBaseMap = new int[CharMap.length()];
        CustomId firstId = new CustomId(Prefix, previousIdBaseMap, tokenMapArray);
        Logger.info("Custom Sequential ID Generated: " + firstId);
        tempIdList.add(firstId);

        for (int i = 0; i < amount - 1; i++) {
            CustomId currentId = new CustomId(firstId);
            Logger.info("Custom Sequential ID Generated: " + currentId);
            currentId.incrementId();
            tempIdList.add(currentId);
            firstId = new CustomId(currentId);
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
    public Set<Id> randomMint(long amount) {
        String[] tokenMapArray = getBaseCharMapping();

        Set<Id> tempIdList = new LinkedHashSet((int) amount);

        for (int i = 0; i < amount; i++) {
            int[] tempIdBaseMap = new int[CharMap.length()];
            for (int j = 0; j < CharMap.length(); j++) {
                tempIdBaseMap[j] = Rng.nextInt(tokenMapArray[j].length());
            }
            Id currentId = new CustomId(Prefix, tempIdBaseMap, tokenMapArray);
            Logger.info("Generated Custom Random ID: " + currentId);
            while (!tempIdList.add(currentId)) {
                currentId.incrementId();
            }
        }

        return tempIdList;

    }

    /**
     * missing javadoc
     *
     * @return
     */
    @Override
    public long calculatePermutations() {
        long totalPermutations = 1;
        for (int i = 0; i < CharMap.length(); i++) {
            if (CharMap.charAt(i) == 'd') {
                totalPermutations *= 10;
            } else if (CharMap.charAt(i) == 'l' || CharMap.charAt(i) == 'u') {
                totalPermutations *= (SansVowel) ? 20 : 26;
            } else if (CharMap.charAt(i) == 'm') {
                totalPermutations *= (SansVowel) ? 40 : 52;
            } else if (CharMap.charAt(i) == 'e') {
                totalPermutations *= (SansVowel) ? 50 : 62;
            }
        }
        return totalPermutations;
    }

    /**
     * Creates an array that stores a range of characters that designates a
     * sequence of possible characters at that specific location.
     *
     * @return the range of characters.
     */
    private String[] getBaseCharMapping() {
        String[] baseTokenMapArray = new String[CharMap.length()];
        for (int i = 0; i < CharMap.length(); i++) {
            // more efficient than charMap.charAt(i) + ""
            String c = String.valueOf(CharMap.charAt(i));
            baseTokenMapArray[i] = BaseMap.get(c);
        }
        return baseTokenMapArray;
    }

    /* getters and setters */
    public String getCharMap() {
        return CharMap;
    }

    public void setCharMap(String CharMap) {
        this.CharMap = CharMap;
    }

    /**
     * Created and used by CustomMinters
     */
    private class CustomId extends Id {

        private String[] TokenMapArray;

        public CustomId(CustomId id) {
            super(id);
            this.TokenMapArray = Arrays.copyOf(id.getTokenMapArray(), id.getTokenMapArray().length);
        }

        public CustomId(String prefix, int[] baseMap, String[] tokenMapArray) {
            super(baseMap, prefix);
            this.TokenMapArray = Arrays.copyOf(tokenMapArray, tokenMapArray.length);
        }

        /**
         * Created and used by CustomMinters
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

                if (value == TokenMapArray[k].length() - 1) {
                    this.getBaseMap()[range - k] = 0;
                } else {
                    this.getBaseMap()[range - k]++;
                    overflow = false;
                }
            }
            return !overflow;
        }

        /**
         * Converts the BaseMap into a String representation of this id's name.
         *
         * There is a one-to-one mapping of BaseMap, dependent on a given
         * DatabaseManager, to every possible name an Id can have.
         *
         * @return - the name of an Id.
         */
        @Override
        public String getRootName() {
            String charId = "";

            for (int i = 0; i < this.getBaseMap().length; i++) {
                charId += TokenMapArray[i].charAt(this.getBaseMap()[i]);
            }
            return charId;
        }

        @Override
        public String toString() {
            return Prefix + this.getRootName();
        }

        /**
         * Retrieves the TokenMapArray assigned to this id
         *
         * @return The array assigned to this id.
         */
        public String[] getTokenMapArray() {
            return TokenMapArray;
        }

        /**
         * Sets the TokenMapArray to this id. Note that the the length of the
         * array must be of the same length id, otherwise index out of bounds
         * exceptions will be thrown.
         *
         * @param TokenMapArray The array to be assigned.
         */
        public void setTokenMapArray(String[] TokenMapArray) {
            this.TokenMapArray = TokenMapArray;
        }
    }
}
