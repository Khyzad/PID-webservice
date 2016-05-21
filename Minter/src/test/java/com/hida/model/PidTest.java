package com.hida.model;

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
        String rootName = name.replace(prefix,"");
        
        // check to see if each character in the rootName is contained by the TokenType
        boolean contains = true;        
        for(int i = 0; i < rootName.length() && contains; i++){
            char c = rootName.charAt(i);
            
            // if the name doesn't contain the character, then contains is false
            if(!tokenType.getCharacters().contains(c + "")){
                contains = false;
            }
        }
        
        Assert.assertEquals(name + ", testing tokenType: " + tokenType, true, contains);
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
    public void testOrder(Pid previous, Pid next) {        
        Assert.assertEquals(-1, previous.compareTo(next));
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
            case LOWER_ALPHABET:
                return (sansVowel) ? "([^aeiouyA-Z\\W\\d]*)" : "([a-z]*)";
            case UPPER_ALPHABET:
                return (sansVowel) ? "([^a-zAEIOUY\\W\\d]*)" : "([A-Z]*)";
            case MIXED_ALPHABET:
                return (sansVowel) ? "([^aeiouyAEIOUY\\W\\d]*)" : "([a-zA-Z]*)";
            case LOWER_ALPHABET_EXTENDED:
                return (sansVowel) ? "([^aeiouyA-Z\\W]*)" : "([a-z\\d]*)";
            case UPPER_ALPHABET_EXTENDED:
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
}
