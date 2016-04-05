package com.hida.model;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author lruffin
 */
public class CustomIdGeneratorTest implements Comparator<String> {   

    protected static final Logger Logger = LoggerFactory.getLogger(CustomIdGeneratorTest.class);

    /**
     * missing javadoc
     *
     * @return
     */
    @DataProvider(name = "sansVowel")
    public Object[][] sansVowelParameters() {
        return new Object[][]{
            {"", true, "d", 10},
            {"", true, "l", 20},
            {"", true, "u", 20},
            {"", true, "m", 40},
            {"", true, "e", 50},
            {"", true, "dl", 200},
            {"", true, "du", 200},
            {"", false, "d", 10},
            {"", false, "l", 26},
            {"", false, "u", 26},
            {"", false, "m", 52},
            {"", false, "e", 62},
            {"", false, "dl", 260},
            {"", false, "du", 260},};
    }

    /**
     * missing javadoc
     *
     * @return
     */
    @DataProvider(name = "prefix")
    public Object[][] prefixParameters() {
        return new Object[][]{
            {"", false, "d", 10},
            {"!@*(", false, "d", 10},
            {"www", false, "d", 10},
            {"123", false, "d", 10},
            {"123abc", false, "d", 10},
            {"!a1", false, "d", 10},
            {" ", false, "d", 10}
        };
    }

    /**
     * missing javadoc
     *
     * @return
     */
    @DataProvider(name = "rootLength")
    public Object[][] rootLengthParameters() {
        Object[][] parameter = new Object[2][];
        String charMap = "d";
        for (int i = 1; i <= 2; i++) {
            Object[] array = {"", false, charMap, (int) Math.pow(10, i)};
            parameter[i - 1] = array;
            charMap += "d";
        }
        return parameter;
    }

    /**
     * missing javadoc
     *
     * @param prefix
     * @param sansVowel
     * @param charMap
     * @param amount
     */
    @Test(dataProvider = "sansVowel")
    public void testSequentialMintSansVowels(String prefix, boolean sansVowel,
            String charMap, int amount) {
        Logger.trace("inside testSequentialMintSansVowels");

        Set<Pid> sequentialSet = testSequentialMint(prefix, sansVowel, charMap, amount);

        // test sequential mint
        String prev = null;
        Iterator<Pid> iter = sequentialSet.iterator();
        while (iter.hasNext()) {
            // fail if the id does not match the token 
            String current = iter.next().toString();
            if (!containsCorrectCharacters(prefix, current, sansVowel, charMap)) {
                Assert.fail(String.format("Id \"%s\" does not match %s, sansVowels = %b",
                        current, charMap, sansVowel));
            }

            // fail the test if ids aren't ordered
            if (prev != null && compare(prev, current) > -1) {
                Assert.fail(String.format("The ids are not sequential: prev=%s\tcurrent=%s",
                        prev, current));
            }
            prev = current;
        }

        Assert.assertEquals(sequentialSet.size(), amount);
    }

    /**
     * missing javadoc
     *
     * @param prefix
     * @param sansVowel
     * @param charMap
     * @param amount
     */
    @Test(dataProvider = "sansVowel")
    public void testRandomMintSansVowels(String prefix, boolean sansVowel,
            String charMap, int amount) {
        Logger.debug("inside testRandomMintSansVowels");

        Set<Pid> randomSet = testRandomMint(prefix, sansVowel, charMap, amount);

        // test random mint
        for (Pid id : randomSet) {
            // fail if the id does not match the token 
            if (!containsCorrectCharacters(prefix, id.toString(), sansVowel, charMap)) {
                Assert.fail(String.format("Id \"%s\" does not match %s, sansVowels = %b",
                        id.toString(), charMap, sansVowel));
            }
        }
        // test to see if the amount matches the size of the generated set        
        Assert.assertEquals(randomSet.size(), amount);
    }

    /**
     * missing javadoc
     *
     * @param prefix
     * @param sansVowel
     * @param charMap
     * @param amount
     */
    @Test(dataProvider = "prefix")
    public void testSequentialMintPrefix(String prefix, boolean sansVowel, String charMap,
            int amount) {
        Logger.debug("inside testSequentialMintPrefix");

        Set<Pid> sequentialSet
                = testSequentialMint(prefix, sansVowel, charMap, amount);

        // test sequential mint
        String prev = null;
        Iterator<Pid> iter = sequentialSet.iterator();
        while (iter.hasNext()) {
            String current = iter.next().toString();
            if (!current.startsWith(prefix)) {
                Assert.fail(String.format("Id \"%s\" does not start with \"$s\"", 
                        current, prefix));
            }

            // fail the test if ids aren't ordered
            if (prev != null && compare(prev, current) > -1) {
                Assert.fail(String.format("The ids are not sequential: prev=%s\tcurrent=%s",
                        prev, current));
            }
            prev = current;
        }

        // test to see if the amount matches the size of the generated set
        Assert.assertEquals(sequentialSet.size(), amount);
    }

    /**
     * missing javadoc
     *
     * @param prefix
     * @param sansVowel
     * @param charMap
     * @param amount
     */
    @Test(dataProvider = "prefix")
    public void testRandomMintPrefix(String prefix, boolean sansVowel,
            String charMap, int amount) {
        Logger.debug("inside testRandomMintPrefix");
        Set<Pid> randomSet = testRandomMint(prefix, sansVowel, charMap, amount);

        // test random mint
        for (Pid id : randomSet) {
            if (!id.toString().startsWith(prefix)) {
                Assert.fail(String.format("Id \"%s\" does not start with \"$s\"", id, prefix));
            }
        }

        // test to see if the amount matches the size of the generated set
        Assert.assertEquals(randomSet.size(), amount);
    }

    /**
     * missing javadoc
     *
     * @param prefix
     * @param sansVowel
     * @param charMap
     * @param amount
     */
    @Test(dataProvider = "rootLength")
    public void testSequentialLength(String prefix, boolean sansVowel, String charMap,
            int amount) {
        Logger.debug("inside testSequentialLength");

        CustomIdGenerator minter = new CustomIdGenerator(prefix, sansVowel, charMap);
        long total = minter.calculatePermutations();
        if (amount > total) {
            amount = (int) total;
        }
        Set<Pid> sequentialSet = minter.sequentialMint(amount);

        // test sequential mint
        String prev = null;
        int nameLength = prefix.length() + charMap.length();
        Iterator<Pid> iter = sequentialSet.iterator();
        while (iter.hasNext()) {
            // fail if the length does not match
            String current = iter.next().toString();
            Assert.assertEquals(current.length(), nameLength,
                    String.format("Id \"%s\" length is not %d", current, nameLength));

            // fail the test if ids aren't ordered
            if (prev != null && compare(prev, current) > -1) {
                Assert.fail(String.format("The ids are not sequential: prev=%s\tcurrent=%s",
                        prev, current));
            }
            prev = current;
        }

        // test to see if the amount matches the size of the generated set
        Assert.assertEquals(sequentialSet.size(), amount);
    }

    /**
     * missing javadoc
     *
     * @param prefix
     * @param sansVowel
     * @param charMap
     * @param amount
     */
    @Test(dataProvider = "rootLength")
    public void testRandomLength(String prefix, boolean sansVowel, String charMap, int amount) {
        Logger.debug("inside testRandomLength");

        CustomIdGenerator minter = new CustomIdGenerator(prefix, sansVowel, charMap);
        long total = minter.calculatePermutations();
        if (amount > total) {
            amount = (int) total;
        }
        Set<Pid> randomSet = minter.randomMint(amount);

        // test random mint
        int nameLength = prefix.length() + charMap.length();
        for (Pid id : randomSet) {
            Assert.assertEquals(id.toString().length(), nameLength,
                    String.format("Id \"%s\" length is not %d", id, nameLength));
        }

        // test to see if the amount matches the size of the generated set
        Assert.assertEquals(randomSet.size(), amount);
    }   

    /**
     * missing javadoc
     */
    @Test(expectedExceptions = NotEnoughPermutationsException.class)
    public void testSequentialNotEnoughPermutationException() {
        Logger.debug("inside testSequentialNotEnoughPermutationException");

        IdGenerator minter = new CustomIdGenerator("", true, "ddddd");
        long total = minter.calculatePermutations();

        Set<Pid> sequentialSet = minter.randomMint(total + 1);
    }

    /**
     * missing javadoc
     */
    @Test(expectedExceptions = NotEnoughPermutationsException.class)
    public void testRandomNotEnoughPermutationException() {
        Logger.debug("inside testRandomNotEnoughPermutationException");

        IdGenerator minter = new CustomIdGenerator("", true, "ddddd");
        long total = minter.calculatePermutations();

        Set<Pid> randomSet = minter.randomMint(total + 1);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testRandomMintNegativeAmount() {
        Logger.debug("inside testRandomMintNegativeAmount");

        IdGenerator minter = new CustomIdGenerator("", true, "ddddd");

        Set<Pid> randomSet = minter.randomMint(-1);
        Assert.assertEquals(randomSet.isEmpty(), true);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testSequentialMintNegativeAmount() {
        Logger.debug("inside testSequentialMintNegativeAmount");

        IdGenerator minter = new CustomIdGenerator("", true, "ddddd");
        long total = minter.calculatePermutations();

        Set<Pid> sequentialSet = minter.sequentialMint(-1);
        Assert.assertEquals(sequentialSet.isEmpty(), true);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testRandomMintZeroAmount() {
        Logger.debug("inside testRandomMintZeroAmount");

        IdGenerator minter = new CustomIdGenerator("", true, "ddddd");

        Set<Pid> randomSet = minter.randomMint(0);
        Assert.assertEquals(randomSet.isEmpty(), true);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testSequentialMintZeroAmount() {
        Logger.debug("inside testSequentialMintZeroAmount");

        IdGenerator minter = new CustomIdGenerator("", true, "ddddd");

        Set<Pid> sequentialSet = minter.sequentialMint(0);
        Assert.assertEquals(sequentialSet.isEmpty(), true);
    }

    /**
     * missing javadoc
     *
     * @param prefix
     * @param sansVowel
     * @param tokenType
     * @param rootLength
     * @return
     */
    private Set<Pid> testSequentialMint(String prefix, boolean sansVowel, String charMap,
            int amount) {
        IdGenerator generator = new CustomIdGenerator(prefix, sansVowel, charMap);
        return generator.sequentialMint(amount);
    }

    /**
     * missing javadoc
     *
     * @param prefix
     * @param sansVowel
     * @param tokenType
     * @param rootLength
     * @return
     */
    private Set<Pid> testRandomMint(String prefix, boolean sansVowel, String charMap, int amount) {
        IdGenerator generator = new CustomIdGenerator(prefix, sansVowel, charMap);
        return generator.randomMint(amount);
    }

    /**
     * missing javadoc
     *
     * @param prefix
     * @param name
     * @param tokenType
     * @param sansVowel
     * @return
     */
    private boolean containsCorrectCharacters(String prefix, String name, boolean sansVowel,
            String charMap) {
        String regex = retrieveRegex(charMap, sansVowel);
        return name.matches(String.format("^%s%s$", prefix, regex));
    }

    /**
     * Returns an equivalent regular expression that'll map that maps to a
     * specific TokenType
     *
     * @param charMap Designates what characters are contained in the id's root
     * @param sansVowel
     * @return a regular expression
     */
    private String retrieveRegex(String charMap, boolean sansVowel) {
        String regex = "(^";
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
        return regex += "$)";
    }

    /**
     * Used to compare to ids. If the first id has a smaller value than the
     * second id, -1 is returned. If they are equal, 0 is returned. Otherwise 1
     * is returned. In terms of value, each character has a unique value
     * associated with them. Numbers are valued less than lowercase letters,
     * which are valued less than upper case letters.
     *
     * The least and greatest valued number is 0 and 9 respectively. The least
     * and greatest valued lowercase letter is a and z respectively. The least
     * and greatest valued uppercase letter is A and Z respectively.
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
