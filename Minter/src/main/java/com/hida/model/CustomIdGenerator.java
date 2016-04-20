package com.hida.model;

import static com.hida.model.IdGenerator.Rng;
import static com.hida.model.IdGenerator.Logger;
import java.util.LinkedHashSet;
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

        // assign base map the appropriate values
        if (sansVowel) {
            this.BaseMap.put("d", DIGIT_TOKEN);
            this.BaseMap.put("l", SANS_VOWEL_TOKEN);
            this.BaseMap.put("u", SANS_VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put("m", SANS_VOWEL_TOKEN + SANS_VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put("e", DIGIT_TOKEN + SANS_VOWEL_TOKEN + SANS_VOWEL_TOKEN.toUpperCase());
        }
        else {
            this.BaseMap.put("d", DIGIT_TOKEN);
            this.BaseMap.put("l", VOWEL_TOKEN);
            this.BaseMap.put("u", VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put("m", VOWEL_TOKEN + VOWEL_TOKEN.toUpperCase());
            this.BaseMap.put("e", DIGIT_TOKEN + VOWEL_TOKEN + VOWEL_TOKEN.toUpperCase());
        }
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
        String[] tokenMapArray = getBaseCharMapping();
        Set<Pid> tempIdList = new LinkedHashSet();

        for (int i = 0; i < amount; i++) {
            int[] tempIdBaseMap = new int[CharMap.length()];
            for (int j = 0; j < CharMap.length(); j++) {
                tempIdBaseMap[j] = Rng.nextInt(tokenMapArray[j].length());
            }
            Pid currentId = new CustomId(Prefix, tempIdBaseMap, tokenMapArray);
            Logger.info("Generated Custom Random ID: " + currentId);
            while (!tempIdList.add(currentId)) {
                currentId.incrementId();
            }
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
        //Logger.info("in genIdCustomSequential: " + amount);

        // checks to see if its possible to produce or add requested amount of
        long total = calculatePermutations();
        if (total < amount) {
            throw new NotEnoughPermutationsException();
        }

        // generate ids
        String[] tokenMapArray = getBaseCharMapping();
        Set<Pid> idSet = new TreeSet();

        int[] previousIdBaseMap = new int[CharMap.length()];
        CustomId currentId = new CustomId(Prefix, previousIdBaseMap, tokenMapArray);
        for (int i = CharMap.length() - 1; i >= 0; i--) {
            for (int j = CharMap.length() - 1; j >= 0; j--) {

            }
        }
        for (int i = 0; i < amount; i++) {
            CustomId nextId = new CustomId(currentId);
            idSet.add(currentId);
            Logger.info("Generated Custom Sequential ID: " + currentId);
            nextId.incrementId();
            currentId = new CustomId(nextId);
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
}
