package com.hida.model;

import java.util.Comparator;
import junit.framework.Assert;

/**
 * A class created to test the validity of a Pid
 *
 * @author lruffin
 */
public class PidTest {

    /**
     * Tests the name to see if it matches the provided prepend
     *
     * @param name Unique identifier of a Pid
     * @param setting The desired setting used to create a Pid
     */
    public void testPrepend(String name, DefaultSetting setting) {
        String prepend = setting.getPrepend();

        Assert.assertTrue(name + ", testing prepend: " + prepend, name.startsWith(prepend));
    }

    /**
     * Tests the name to see if it matches the provided prefix
     *
     * @param name Unique identifier of a Pid
     * @param setting The desired setting used to create a Pid
     */
    public void testPrefix(String name, Setting setting) {
        String prefix = setting.getPrefix();

        Assert.assertTrue(name + ", testing prefix: " + prefix, name.startsWith(prefix));
    }

    /**
     * Tests the name to see if it matches the provided token type
     *
     * @param name Unique identifier of a Pid
     * @param setting The desired setting used to create a Pid
     */
    public void testTokenType(String name, Setting setting) {
        String prefix = setting.getPrefix();
        TokenType tokenType = setting.getTokenType();
        boolean sansVowel = setting.isSansVowels();

        boolean matchesToken = containsCorrectCharacters(prefix, name, tokenType, sansVowel);
        Assert.assertEquals(name + ", testing tokenType: " + tokenType, true, matchesToken);
    }

    /**
     * Tests the name to see if it matches the provided root length
     *
     * @param name Unique identifier of a Pid
     * @param setting The desired setting used to create a Pid
     */
    public void testRootLength(String name, Setting setting) {
        String prefix = setting.getPrefix();

        name = name.replace(prefix, "");
        int rootLength = name.length();
        int expRootLength = setting.getRootLength();
        Assert.assertEquals(name + ", testing rootLength: " + rootLength,
                rootLength, expRootLength);
    }

    /**
     * Tests the name to see if it matches the provided char map
     *
     * @param name Unique identifier of a Pid
     * @param setting The desired setting used to create a Pid
     */
    public void testCharMap(String name, Setting setting) {
        String prefix = setting.getPrefix();
        String charMap = setting.getCharMap();
        boolean sansVowel = setting.isSansVowels();

        boolean matchesToken = containsCorrectCharacters(prefix, name, sansVowel, charMap);
        Assert.assertEquals(name + ", testing charMap: " + charMap, true, matchesToken);
    }

    /**
     * Tests the order of two names. The previous name must have a lesser value
     * than the next name to pass the test.
     *
     * @param previous The previous name
     * @param next The next name
     */
    public void testOrder(String previous, String next) {
        PidComparator comparator = new PidComparator();
        Assert.assertEquals(-1, comparator.compare(previous, next));
    }

    /**
     * Checks to see if the Pid matches the given parameters
     *
     * @param prefix A sequence of characters that appear in the beginning of
     * PIDs
     * @param name Unique identifier of a Pid
     * @param tokenType An enum used to configure PIDS
     * @param sansVowel Dictates whether or not vowels are allowed
     * @return True if the Pid matches the parameters, false otherwise
     */
    private boolean containsCorrectCharacters(String prefix, String name, TokenType tokenType,
            boolean sansVowel) {
        String regex = retrieveRegex(tokenType, sansVowel);
        return name.matches(String.format("^(%s)%s$", prefix, regex));
    }

    /**
     * Checks to see if the Pid matches the given parameters
     *
     * @param prefix A sequence of characters that appear in the beginning of
     * PIDs
     * @param name Unique identifier of a Pid
     * @param tokenType An enum used to configure PIDS
     * @param sansVowel Dictates whether or not vowels are allowed
     * @return True if the Pid matches the parameters, false otherwise
     */
    private boolean containsCorrectCharacters(String prefix, String name, boolean sansVowel,
            String charMap) {
        String regex = retrieveRegex(charMap, sansVowel);
        return name.matches(String.format("^(%s)%s$", prefix, regex));
    }

    /**
     * Returns an equivalent regular expression that'll map that maps to a
     * specific TokenType
     *
     * @param tokenType Designates what characters are contained in the id's
     * root
     * @param sansVowel Dictates whether or not vowels are allowed
     * @return a regular expression
     */
    private String retrieveRegex(TokenType tokenType, boolean sansVowel) {

        switch (tokenType) {
            case DIGIT:
                return "([\\d]*)";
            case LOWERCASE:
                return (sansVowel) ? "([^aeiouyA-Z\\W\\d]*)" : "([a-z]*)";
            case UPPERCASE:
                return (sansVowel) ? "([^a-zAEIOUY\\W\\d]*)" : "([A-Z]*)";
            case MIXEDCASE:
                return (sansVowel) ? "([^aeiouyAEIOUY\\W\\d]*)" : "([a-zA-Z]*)";
            case LOWER_EXTENDED:
                return (sansVowel) ? "([^aeiouyA-Z\\W]*)" : "([a-z\\d]*)";
            case UPPER_EXTENDED:
                return (sansVowel) ? "([^a-zAEIOUY\\W]*)" : "([A-Z\\d]*)";
            default:
                return (sansVowel) ? "([^aeiouyAEIOUY\\W]*)" : "(^[a-zA-z\\d]*)";
        }
    }

    /**
     * Returns an equivalent regular expression that'll map that maps to a
     * specific TokenType
     *
     * @param charMap Designates what characters are contained in the id's root
     * @param sansVowel Dictates whether or not vowels are allowed
     * @return a regular expression
     */
    private String retrieveRegex(String charMap, boolean sansVowel) {
        String regex = "";
        for (int i = 0; i < charMap.length(); i++) {
            char key = charMap.charAt(i);
            if (key == 'd') {
                regex += "[\\d]";
            }
            else if (key == 'l') {
                regex += (sansVowel) ? "[^aeiouyA-Z\\W\\d]" : "[a-z]";
            }
            else if (key == 'u') {
                regex += (sansVowel) ? "[^a-zAEIOUY\\W\\d]" : "[A-Z]";
            }
            else if (key == 'm') {
                regex += (sansVowel) ? "[^aeiouyAEIOUY\\W\\d]" : "[a-zA-Z]";
            }
            else if (key == 'e') {
                regex += (sansVowel) ? "[^aeiouyAEIOUY\\W]" : "[a-zA-z\\d]";
            }
        }
        return regex;
    }

    /**
     * Comparator object used to compare the value of a Pid's name
     */
    private static class PidComparator implements Comparator<String> {

        /**
         * Used to compare to ids. If the first id has a smaller value than the
         * second id, -1 is returned. If they are equal, 0 is returned.
         * Otherwise 1 is returned. In terms of value, each character has a
         * unique value associated with them. Numbers are valued less than
         * lowercase letters, which are valued less than upper case letters.
         *
         * The least and greatest valued number is 0 and 9 respectively. The
         * least and greatest valued lowercase letter is a and z respectively.
         * The least and greatest valued uppercase letter is A and Z
         * respectively.
         *
         * @param id1 the first id
         * @param id2 the second id
         * @return result of the comparison.
         */
        @Override
        public int compare(String id1, String id2) {
            if (id1.length() < id2.length()) {
                return -1;
            }
            else if (id1.length() > id2.length()) {
                return 1;
            }
            else {
                for (int i = 0; i < id1.length(); i++) {
                    char c1 = id1.charAt(i);
                    char c2 = id2.charAt(i);
                    if (Character.isDigit(c1) && Character.isLetter(c2)
                            || Character.isLowerCase(c1) && Character.isUpperCase(c2)
                            || c1 < c2) {
                        return -1;
                    }
                    else if ((Character.isLetter(c1) && Character.isDigit(c2))
                            || Character.isUpperCase(c1) && Character.isLowerCase(c2)
                            || c1 > c2) {
                        return 1;
                    }
                }
                return 0;
            }
        }

    }
}
