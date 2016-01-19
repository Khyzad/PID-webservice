/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.hida;

import com.hida.util.AutoIdGenerator;
import com.hida.util.Id;
import com.hida.util.IdGenerator;
import com.hida.util.NotEnoughPermutationsException;
import com.hida.util.TokenType;
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
public class AutoIdGeneratorTest implements Comparator<String> {

    public AutoIdGeneratorTest() {
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
            {"", true, TokenType.DIGIT, 4, 1000},
            {"", true, TokenType.LOWERCASE, 4, 1000},
            {"", true, TokenType.UPPERCASE, 4, 1000},
            {"", true, TokenType.MIXEDCASE, 4, 1000},
            {"", true, TokenType.LOWER_EXTENDED, 4, 1000},
            {"", true, TokenType.UPPER_EXTENDED, 4, 1000},
            {"", true, TokenType.MIXED_EXTENDED, 4, 1000},
            {"", false, TokenType.DIGIT, 4, 1000},
            {"", false, TokenType.LOWERCASE, 4, 1000},
            {"", false, TokenType.UPPERCASE, 4, 1000},
            {"", false, TokenType.MIXEDCASE, 4, 1000},
            {"", false, TokenType.LOWER_EXTENDED, 4, 1000},
            {"", false, TokenType.UPPER_EXTENDED, 4, 1000},
            {"", false, TokenType.MIXED_EXTENDED, 4, 1000}
        };
    }

    /**
     * missing javadoc
     *
     * @return
     */
    @DataProvider(name = "prefix")
    public Object[][] prefixParameters() {
        return new Object[][]{
            {"", false, TokenType.DIGIT, 4, 1000},
            {"!@*(", false, TokenType.DIGIT, 4, 1000},
            {"www", false, TokenType.DIGIT, 4, 1000},
            {"123", false, TokenType.DIGIT, 4, 1000},
            {"123abc", false, TokenType.DIGIT, 4, 1000},
            {"!a1", false, TokenType.DIGIT, 4, 1000},
            {" ", false, TokenType.DIGIT, 4, 1000}
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
        for (int i = 1; i <= 5; i++) {
            Object[] array = {"", false, TokenType.DIGIT, i, 1000};
            parameter[i - 1] = array;
        }
        return parameter;
    }

    /**
     * missing javadoc
     *
     * @param prefix
     * @param sansVowel
     * @param tokenType
     * @param rootLength
     * @param amount
     */
    @Test(dataProvider = "sansVowel")
    public void testSequentialMintSansVowels(String prefix, boolean sansVowel, TokenType tokenType,
            int rootLength, int amount) {
        Set<Id> sequentialSet
                = testSequentialMint(prefix, sansVowel, tokenType, rootLength, amount);

        // test sequential mint
        String prev = null;
        Iterator<Id> iter = sequentialSet.iterator();
        while (iter.hasNext()) {
            // fail if the id does not match the token 
            String current = iter.next().toString();
            if (!containsCorrectCharacters(prefix, current, tokenType, sansVowel)) {
                Assert.fail(String.format("Id \"%s\" does not match %s, sansVowels = %b",
                        current, tokenType, sansVowel));
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
     * @param tokenType
     * @param rootLength
     * @param amount
     */
    @Test(dataProvider = "sansVowel")
    public void testRandomMintSansVowels(String prefix, boolean sansVowel, TokenType tokenType,
            int rootLength, int amount) {
        Set<Id> randomSet = testRandomMint(prefix, sansVowel, tokenType, rootLength, amount);

        // test random mint
        for (Id id : randomSet) {
            // fail if the id does not match the token 
            if (!containsCorrectCharacters(prefix, id.toString(), tokenType, sansVowel)) {
                Assert.fail(String.format("Id \"%s\" does not match %s, sansVowels = %b",
                        id.toString(), tokenType, sansVowel));
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
     * @param tokenType
     * @param rootLength
     * @param amount
     */
    @Test(dataProvider = "prefix")
    public void testSequentialMintPrefix(String prefix, boolean sansVowel, TokenType tokenType,
            int rootLength, int amount) {
        Set<Id> sequentialSet
                = testSequentialMint(prefix, sansVowel, tokenType, rootLength, amount);

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
     * @param tokenType
     * @param rootLength
     * @param amount
     */
    @Test(dataProvider = "prefix")
    public void testRandomMintPrefix(String prefix, boolean sansVowel, TokenType tokenType,
            int rootLength, int amount) {

        Set<Id> randomSet = testRandomMint(prefix, sansVowel, tokenType, rootLength, amount);

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
     * @param tokenType
     * @param rootLength
     * @param amount
     */
    @Test(dataProvider = "rootLength")
    public void testSequentialRootLength(String prefix, boolean sansVowel, TokenType tokenType,
            int rootLength, int amount) {
        AutoIdGenerator minter = new AutoIdGenerator(prefix, sansVowel, tokenType, rootLength);
        long total = minter.calculatePermutations();
        if (amount > total) {
            amount = (int) total;
        }
        Set<Id> sequentialSet = minter.sequentialMint(amount);

        // test sequential mint
        String prev = null;
        int nameLength = prefix.length() + rootLength;
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
     * @param tokenType
     * @param rootLength
     * @param amount
     */
    @Test(dataProvider = "rootLength")
    public void testRandomRootLength(String prefix, boolean sansVowel, TokenType tokenType,
            int rootLength, int amount) {

        AutoIdGenerator minter = new AutoIdGenerator(prefix, sansVowel, tokenType, rootLength);
        long total = minter.calculatePermutations();
        if (amount > total) {
            amount = (int) total;
        }
        Set<Id> randomSet = minter.randomMint(amount);

        // test random mint
        int nameLength = prefix.length() + rootLength;
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
        IdGenerator minter = new AutoIdGenerator("", true, TokenType.DIGIT, 5);
        long total = minter.calculatePermutations();

        Set<Id> sequentialSet = minter.sequentialMint(total);

        Assert.assertEquals(sequentialSet.size(), total);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testRandomMintMax() {
        IdGenerator minter = new AutoIdGenerator("", true, TokenType.DIGIT, 5);
        long total = minter.calculatePermutations();

        Set<Id> randomSet = minter.randomMint(total);

        Assert.assertEquals(randomSet.size(), total);
    }

    /**
     * missing javadoc
     */
    @Test(expectedExceptions = NotEnoughPermutationsException.class)
    public void testSequentialNotEnoughPermutationException() {
        IdGenerator minter = new AutoIdGenerator("", true, TokenType.DIGIT, 5);
        long total = minter.calculatePermutations();

        Set<Id> sequentialSet = minter.randomMint(total + 1);
    }

    /**
     * missing javadoc
     */
    @Test(expectedExceptions = NotEnoughPermutationsException.class)
    public void testRandomNotEnoughPermutationException() {
        IdGenerator minter = new AutoIdGenerator("", true, TokenType.DIGIT, 5);
        long total = minter.calculatePermutations();

        Set<Id> randomSet = minter.randomMint(total + 1);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testRandomMintNegativeAmount() {
        IdGenerator minter = new AutoIdGenerator("", true, TokenType.DIGIT, 5);

        Set<Id> randomSet = minter.randomMint(-1);
        Assert.assertEquals(randomSet.isEmpty(), true);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testSequentialMintNegativeAmount() {
        IdGenerator minter = new AutoIdGenerator("", true, TokenType.DIGIT, 5);
        long total = minter.calculatePermutations();

        Set<Id> sequentialSet = minter.sequentialMint(-1);
        Assert.assertEquals(sequentialSet.isEmpty(), true);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testRandomMintZeroAmount() {
        IdGenerator minter = new AutoIdGenerator("", true, TokenType.DIGIT, 5);

        Set<Id> randomSet = minter.randomMint(0);
        Assert.assertEquals(randomSet.isEmpty(), true);
    }

    /**
     * missing javadoc
     */
    @Test
    public void testSequentialMintZeroAmount() {
        IdGenerator minter = new AutoIdGenerator("", true, TokenType.DIGIT, 5);

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
    private Set<Id> testSequentialMint(String prefix, boolean sansVowel, TokenType tokenType,
            int rootLength, int amount) {
        IdGenerator generator = new AutoIdGenerator(prefix, sansVowel, tokenType, rootLength);
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
    private Set<Id> testRandomMint(String prefix, boolean sansVowel, TokenType tokenType,
            int rootLength, int amount) {
        IdGenerator generator = new AutoIdGenerator(prefix, sansVowel, tokenType, rootLength);
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
    private boolean containsCorrectCharacters(String prefix, String name, TokenType tokenType,
            boolean sansVowel) {
        String regex = retrieveRegex(tokenType, sansVowel);
        return name.matches(String.format("^%s%s$", prefix, regex));
    }

    /**
     * Returns an equivalent regular expression that'll map that maps to a
     * specific TokenType
     *
     * @param tokenType Designates what characters are contained in the id's
     * root
     * @param sansVowel
     * @return a regular expression
     */
    private String retrieveRegex(TokenType tokenType, boolean sansVowel) {

        switch (tokenType) {
            case DIGIT:
                return "(^[\\d]*$)";
            case LOWERCASE:
                return (sansVowel) ? "(^[^aeiouyA-Z\\W\\d]*$)" : "(^[a-z]*$)";
            case UPPERCASE:
                return (sansVowel) ? "(^[^a-zAEIOUY\\W\\d]*$)" : "(^[A-Z]*$)";
            case MIXEDCASE:
                return (sansVowel) ? "(^[^aeiouyAEIOUY\\W\\d]*$)" : "(^[a-zA-Z]*$)";
            case LOWER_EXTENDED:
                return (sansVowel) ? "(^[^aeiouyA-Z\\W]*$)" : "(^[a-z\\d]*$)";
            case UPPER_EXTENDED:
                return (sansVowel) ? "(^[^a-zAEIOUY\\W]*$)" : "(^[A-Z\\d]*$)";
            default:
                return (sansVowel) ? "(^[^aeiouyAEIOUY\\W]*$)" : "(^[a-zA-z\\d]*$)";
        }
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
