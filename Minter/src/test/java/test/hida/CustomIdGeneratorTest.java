/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.hida;

import com.hida.util.CustomIdGenerator;
import com.hida.util.Id;
import com.hida.util.IdGenerator;
import com.hida.util.NotEnoughPermutationsException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author lruffin
 */
public class CustomIdGeneratorTest implements Comparator<String> {

    public CustomIdGeneratorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * missing javadoc
     *
     * @return
     */
    @DataProvider(name = "sansVowel")
    public Object[][] sansVowelParameters() {
        return new Object[][]{
            {"", true, "ddddd", 1000},
            {"", true, "lllll", 1000},
            {"", true, "uuuuu", 1000},
            {"", true, "mmmmm", 1000},
            {"", true, "eeeee", 1000},
            {"", true, "ddldd", 1000},
            {"", true, "ddudd", 1000},
            {"", false, "ddddd", 1000},
            {"", false, "lllll", 1000},
            {"", false, "uuuuu", 1000},
            {"", false, "mmmmm", 1000},
            {"", false, "eeeee", 1000},
            {"", false, "ddldd", 1000},
            {"", false, "ddudd", 1000},};
    }

    /**
     * missing javadoc
     *
     * @return
     */
    @DataProvider(name = "prefix")
    public Object[][] prefixParameters() {
        return new Object[][]{
            {"", false, "ddddd", 1000},
            {"!@*(", false, "ddddd", 1000},
            {"www", false, "ddddd", 1000},
            {"123", false, "ddddd", 1000},
            {"123abc", false, "ddddd", 1000},
            {"!a1", false, "ddddd", 1000},
            {" ", false, "ddddd", 1000}
        };
    }
    
    /**
     * missing javadoc
     *
     * @return
     */
    @DataProvider(name = "rootLength")
    public Object[][] rootLengthParameters() {
        Object[][] parameter = new Object[5][];
        String charMap = "d";
        for (int i = 1; i <= 5; i++) {
            Object[] array = {"", false, charMap, 1000};
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
        Set<Id> sequentialSet = testSequentialMint(prefix, sansVowel, charMap, amount);

        // test sequential mint
        String prev = null;
        Iterator<Id> iter = sequentialSet.iterator();
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
    public void testRandomMintSansVowels(String prefix, boolean sansVowel, String charMap, int amount
    ) {
        Set<Id> randomSet = testRandomMint(prefix, sansVowel, charMap, amount);

        // test random mint
        for (Id id : randomSet) {
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
        Set<Id> sequentialSet
                = testSequentialMint(prefix, sansVowel, charMap, amount);

        // test sequential mint
        String prev = null;
        Iterator<Id> iter = sequentialSet.iterator();
        while (iter.hasNext()) {
            String current = iter.next().toString();
            if (!current.startsWith(prefix)) {
                Assert.fail(String.format("Id \"%s\" does not start with \"$s\"", current, prefix));
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
    public void testRandomMintPrefix(String prefix, boolean sansVowel, String charMap,
            int amount) {

        Set<Id> randomSet = testRandomMint(prefix, sansVowel, charMap, amount);

        // test random mint
        for (Id id : randomSet) {
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
        Set<Id> sequentialSet
                = testSequentialMint(prefix, sansVowel, charMap, amount);

        // test sequential mint
        String prev = null;
        int nameLength = prefix.length() + charMap.length();
        Iterator<Id> iter = sequentialSet.iterator();
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

        Set<Id> randomSet = testRandomMint(prefix, sansVowel, charMap, amount);

        // test random mint
        int nameLength = prefix.length() + charMap.length();
        for (Id id : randomSet) {
            Assert.assertEquals(id.toString().length(), nameLength,
                    String.format("Id \"%s\" length is not %d", id, nameLength));
        }

        // test to see if the amount matches the size of the generated set
        Assert.assertEquals(randomSet.size(), amount);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testSequentialMintMax() {
        IdGenerator minter = new CustomIdGenerator("", true, "ddddd");
        long total = minter.calculatePermutations();

        Set<Id> sequentialSet = minter.sequentialMint(total);

        Assert.assertEquals(sequentialSet.size(), total);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testRandomMintMax() {
        IdGenerator minter = new CustomIdGenerator("", true, "ddddd");
        long total = minter.calculatePermutations();

        Set<Id> randomSet = minter.randomMint(total);

        Assert.assertEquals(randomSet.size(), total);
    }

    /**
     * missing javadoc
     */
    @Test(expectedExceptions = NotEnoughPermutationsException.class)
    public void testSequentialNotEnoughPermutationException() {
        IdGenerator minter = new CustomIdGenerator("", true, "ddddd");
        long total = minter.calculatePermutations();

        Set<Id> sequentialSet = minter.randomMint(total + 1);
    }

    /**
     * missing javadoc
     */
    @Test(expectedExceptions = NotEnoughPermutationsException.class)
    public void testRandomNotEnoughPermutationException() {
        IdGenerator minter = new CustomIdGenerator("", true, "ddddd");
        long total = minter.calculatePermutations();

        Set<Id> randomSet = minter.randomMint(total + 1);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testRandomMintNegativeAmount() {
        IdGenerator minter = new CustomIdGenerator("", true, "ddddd");

        Set<Id> randomSet = minter.randomMint(-1);
        Assert.assertEquals(randomSet.isEmpty(), true);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testSequentialMintNegativeAmount() {
        IdGenerator minter = new CustomIdGenerator("", true, "ddddd");
        long total = minter.calculatePermutations();

        Set<Id> sequentialSet = minter.sequentialMint(-1);
        Assert.assertEquals(sequentialSet.isEmpty(), true);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testRandomMintZeroAmount() {
        IdGenerator minter = new CustomIdGenerator("", true, "ddddd");

        Set<Id> randomSet = minter.randomMint(0);
        Assert.assertEquals(randomSet.isEmpty(), true);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testSequentialMintZeroAmount() {
        IdGenerator minter = new CustomIdGenerator("", true, "ddddd");

        Set<Id> sequentialSet = minter.sequentialMint(0);
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
    private Set<Id> testSequentialMint(String prefix, boolean sansVowel, String charMap,
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
    private Set<Id> testRandomMint(String prefix, boolean sansVowel, String charMap, int amount) {
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
            } else if (key == 'l') {
                regex += (sansVowel) ? "[^aeiouyA-Z\\W\\d]" : "[a-z]";
            } else if (key == 'u') {
                regex += (sansVowel) ? "[^a-zAEIOUY\\W\\d]" : "[A-Z]";
            } else if (key == 'm') {
                regex += (sansVowel) ? "[^aeiouyAEIOUY\\W\\d]" : "[a-zA-Z]";
            } else if (key == 'e') {
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
        } else if (id1.length() > id2.length()) {
            return 1;
        } else {
            for (int i = 0; i < id1.length(); i++) {
                char c1 = id1.charAt(i);
                char c2 = id2.charAt(i);
                if (Character.isDigit(c1) && Character.isLetter(c2)
                        || Character.isLowerCase(c1) && Character.isUpperCase(c2)
                        || c1 < c2) {
                    return -1;
                } else if ((Character.isLetter(c1) && Character.isDigit(c2))
                        || Character.isUpperCase(c1) && Character.isLowerCase(c2)
                        || c1 > c2) {
                    return 1;
                }
            }
            return 0;
        }
    }

}
