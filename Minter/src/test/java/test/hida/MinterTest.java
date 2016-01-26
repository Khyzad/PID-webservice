package test.hida;

import com.hida.client.Minter;
import com.hida.util.TokenType;
import java.util.Comparator;
import java.util.Random;
import org.testng.annotations.DataProvider;


/**
 *
 * @author lruffin
 */
public class MinterTest implements Comparator<String> {

    /**
     * Name of the database
     */
    static String DbName = "testPID";

    /**
     * Creates a new Minter completely separate from the actual
 service.
     */
    Minter DatabaseManager = new Minter("", DbName);

    public MinterTest() {
    }

    /**
     * Used to test the functionality of the AutoMinter
     *
     * @return
     */
    @DataProvider(name = "autoMinter parameters")
    public static Object[][] AutoMinterParameters() {
        Random rng = new Random();
        return new Object[][]{
            {rng.nextInt(90) + 10, TokenType.DIGIT},
            {rng.nextInt(90) + 10, TokenType.LOWERCASE},
            {rng.nextInt(90) + 10, TokenType.UPPERCASE},
            {rng.nextInt(90) + 10, TokenType.MIXEDCASE},
            {rng.nextInt(90) + 10, TokenType.LOWER_EXTENDED},
            {rng.nextInt(90) + 10, TokenType.UPPER_EXTENDED},
            {rng.nextInt(90) + 10, TokenType.MIXED_EXTENDED}
        };
    }

    /**
     * Used to test functionality of CustomMinters
     *
     * @return
     */
    @DataProvider(name = "customMinter parameters")
    public static Object[][] CustomMinterParameters() {
        Random rng = new Random();
        return new Object[][]{
            {rng.nextInt(90) + 10, "ddddd", TokenType.DIGIT},
            {rng.nextInt(90) + 10, "lllll", TokenType.LOWERCASE},
            {rng.nextInt(90) + 10, "uuuuu", TokenType.UPPERCASE},
            {rng.nextInt(90) + 10, "lmmmu", TokenType.MIXEDCASE},
            {rng.nextInt(90) + 10, "lulul", TokenType.MIXEDCASE},
            {rng.nextInt(90) + 10, "ldldl", TokenType.LOWER_EXTENDED},
            {rng.nextInt(90) + 10, "ududu", TokenType.UPPER_EXTENDED},
            {rng.nextInt(90) + 10, "uedel", TokenType.MIXED_EXTENDED},
            {rng.nextInt(90) + 10, "ldmdu", TokenType.MIXED_EXTENDED},
            {rng.nextInt(90) + 10, "ldudl", TokenType.MIXED_EXTENDED}
        };
    }

    /**
     * Used to test whether or not NotEnoughPermutationsException is properly
     * thrown
     *
     * @return
     */
    @DataProvider(name = "overlap parameters")
    public static Object[][] FormatOverlapParameters() {
        return new Object[][]{
            {100, TokenType.DIGIT, true, 2},
            {900, TokenType.LOWER_EXTENDED, true, 2},
            {900, TokenType.UPPER_EXTENDED, true, 2},
            {2500, TokenType.MIXED_EXTENDED, true, 2}
        };
    }

    /**
     * Method that is used to test whether or not the format of an id is stored
     * in a database.
     *
     * @return a list of formats to test.
     */
    @DataProvider(name = "format parameters")
    public static Object[][] FormatParameters() {
        Random rng = new Random();
        return new Object[][]{
            {rng.nextInt(90) + 10, "1", TokenType.DIGIT, true, 5},
            {rng.nextInt(90) + 10, "2", TokenType.LOWER_EXTENDED, true, 5},
            {rng.nextInt(90) + 10, "3", TokenType.UPPER_EXTENDED, true, 5},
            {rng.nextInt(90) + 10, "4", TokenType.MIXED_EXTENDED, true, 5},
            {rng.nextInt(90) + 10, "5", TokenType.LOWERCASE, true, 5},
            {rng.nextInt(90) + 10, "6", TokenType.UPPERCASE, true, 5},
            {rng.nextInt(90) + 10, "7", TokenType.MIXEDCASE, true, 5},
            {rng.nextInt(90) + 10, "1", TokenType.DIGIT, false, 5},
            {rng.nextInt(90) + 10, "2", TokenType.LOWER_EXTENDED, false, 5},
            {rng.nextInt(90) + 10, "3", TokenType.UPPER_EXTENDED, false, 5},
            {rng.nextInt(90) + 10, "4", TokenType.MIXED_EXTENDED, false, 5},
            {rng.nextInt(90) + 10, "5", TokenType.LOWERCASE, false, 5},
            {rng.nextInt(90) + 10, "6", TokenType.UPPERCASE, false, 5},
            {rng.nextInt(90) + 10, "7", TokenType.MIXEDCASE, false, 5}
        };
    }
    
     /**
     * Returns a set of parameters for AutoMinter that should throw a
     * BadParameterException
     *
     * @return
     */
    @DataProvider(name = "bad parameter auto")
    public static Object[][] BadParametersAutoMinter() {
        return new Object[][]{
            {-10, "prefix", TokenType.DIGIT, 10, true},
            {100, "!prefix", TokenType.DIGIT, 10, true},
            {100, String.format("%21s", ""), TokenType.DIGIT, 10, true},
            {100, "prefix", TokenType.DIGIT, -10, true},
            {100, "prefix", TokenType.DIGIT, 11, true}
        };
    }

    /**
     * Returns a set of parameters for AutoMinter that should throw a
     * BadParameterException
     *
     * @return
     */
    @DataProvider(name = "bad parameter custom")
    public static Object[][] BadParametersCustomMinter() {
        return new Object[][]{
            {-110, "prefix", "dlume", true},
            {100, "&prefix", "dlume", true},
            {100, String.format("%21s", ""), "dlume", true},
            {100, "prefix", "dlumea", true}

        };
    }


    
    

    /**
     * This tests to see if NotEnoughPermutationsException is properly thrown
     * whenever appropriate.
     *
     * <pre>
     * The exception is thrown whenever:
     * - the amount requested exceeds the number of possible permutations.
     * - the amount of unique ids produced does not meet the amount requested .
     * </pre>
     *
     * If another exception is thrown the test fails.
     *
     * @param expectedAmount The amount of ids requested.
     * @param tokenType Designates what characters are contained in the id's
     * root.
     * @param sansVowel Designates whether or not the id's root contains vowels.
     * @param rootLength Designates the length of the id's root.
     
    @Test(dataProvider = "overlap parameters",
            expectedExceptions = NotEnoughPermutationsException.class,
            dependsOnMethods = "populateAutoDigitFormat")
    public void formatOverlap(int expectedAmount, TokenType tokenType, boolean sansVowel,
            int rootLength) {
        System.out.println("inside formatOverlap");
        try {

            String prefix = "";

            DatabaseManager.getPermutations(prefix, tokenType, rootLength, sansVowel);
            Minter AutoMinter
                    = new Minter(DatabaseManager, prefix, tokenType, rootLength, prefix, sansVowel);

            AutoMinter.genIdAutoRandom(expectedAmount);

            DatabaseManager.printFormat();

        } catch (SQLException | BadParameterException | IOException exception) {
            Assert.fail(exception.getMessage(), exception);
        }
    }
*/
    /**
     * Tests the service to see if a format corresponding to a mint request is
     * stored in the format table.
     *
     * @param expectedAmount The amount of ids requested.
     * @param prefix The string that will be at the front of every id.
     * @param tokenType Designates what characters are contained in the id's
     * root.
     * @param sansVowel Designates whether or not the id's root contains vowels.
     * @param rootLength Designates the length of the id's root.
     
    @Test(dataProvider = "format parameters")
    public void testFormat(int expectedAmount, String prefix, TokenType tokenType,
            boolean sansVowel, int rootLength) {
        try {
            DatabaseManager.getPermutations(prefix, tokenType, rootLength, sansVowel);
            Minter AutoMinter
                    = new Minter(DatabaseManager, "", tokenType, rootLength, prefix, sansVowel);

            AutoMinter.genIdAutoRandom(expectedAmount);

            if (!DatabaseManager.formatExists(prefix, tokenType, sansVowel, rootLength)) {
                Assert.fail("format does not exist");
            }

        } catch (Exception exception) {
            Assert.fail(exception.getMessage(), exception);
        }
    }
*/
   
    /**
     *
     * @param amount The amount of ids requested.
     * @param prefix The string that will be at the front of every id.
     * @param tokenType Designates what characters are contained in the id's
     * root.
     * @param rootLength Designates the length of the id's root.
     * @param sansVowel Designates whether or not the id's root contains vowels.
     * @throws Exception
     
    @Test(dataProvider = "bad parameter auto", expectedExceptions = BadParameterException.class)
    public void testBadParameterExceptionAutoMinter(long amount, String prefix, 
            TokenType tokenType, int rootLength, boolean sansVowel) throws Exception {
        Minter AutoMinter
                = new Minter(DatabaseManager, "", tokenType, rootLength, prefix, sansVowel);
        if (!AutoMinter.isValidAmount(amount)) {
            throw new BadParameterException(amount, "Requested Amount");
        }
    }
*/
    /**
     *
     * @param amount The amount of ids requested.
     * @param prefix The string that will be at the front of every id.
     * @param charMap The mapping used to describe range of possible characters
     * at each of the id's root's digits.
     * @param sansVowel Designates whether or not the id's root contains vowels.
     
    @Test(dataProvider = "bad parameter custom", expectedExceptions = BadParameterException.class)
    public void testBadParameterExceptionCustomMinter(long amount, String prefix, String charMap,
            boolean sansVowel) throws Exception {
        Minter CustomMinter
                = new Minter(DatabaseManager, "", charMap, prefix, sansVowel);        
        if (!CustomMinter.isValidAmount(amount)) {
            throw new BadParameterException(amount, "Requested Amount");
        }
    }
*/
    

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
