package com.hida.controller;

import com.hida.model.AutoIdGenerator;
import com.hida.model.BadParameterException;
import com.hida.model.CustomIdGenerator;
import com.hida.model.DefaultSetting;
import com.hida.model.IdGenerator;
import com.hida.model.Pid;
import com.hida.model.TokenType;
import com.hida.service.MinterServiceImpl;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import junit.framework.Assert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.ui.ModelMap;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.json.JSONArray;
import org.json.JSONObject;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.AfterTest;
import org.testng.annotations.DataProvider;

/**
 *
 * @author lruffin
 */
public class MinterControllerTest {

    @Mock
    MinterServiceImpl MinterServiceDao;

    @InjectMocks
    MinterController Controller;

    @Spy
    ModelMap ModelMap;

    private final String PREPEND = "http://digitalarchives.hawaii.gov/70111/";
    private final int AMOUNT = 5;

    private static final Logger Logger = LoggerFactory.getLogger(MinterControllerTest.class);

    @BeforeClass
    public void setUpClass() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider(name = "request parameters")
    private Object[][] requestParameters() {
        return new Object[][]{
            {PREPEND, "xyz", "auto", "random", "true", "1", "true", null, null, "mmm"},
            {PREPEND, "xyz", "false", "false", null, "1", "true", "true", null, "mmm"},
            {PREPEND, "xyz", "false", "false", null, "1", "true", "true", "true", "mmm"},
            {PREPEND, "xyz", "false", "false", null, "1", null, "true", null, "mmm"},
            {PREPEND, "xyz", "false", "false", null, "1", null, "true", "true", "mmm"},
            {PREPEND, "xyz", "false", "false", null, "1", null, null, "true", "mmm"},
            {PREPEND, "xyz", "false", "false", null, "1", "true", null, null, "mmm"},
            
        };
    }

    @DataProvider(name = "parameters")
    private Object[][] parameters() {
        return new Object[][]{
            {PREPEND, "abc", TokenType.DIGIT, "d", 1, true, true, true},
            {PREPEND, "abc", TokenType.LOWERCASE, "d", 1, true, true, true},
            {PREPEND, "abc", TokenType.UPPERCASE, "d", 1, true, true, true},
            {PREPEND, "abc", TokenType.MIXEDCASE, "d", 1, true, true, true},
            {PREPEND, "abc", TokenType.LOWER_EXTENDED, "d", 1, true, true, true},
            {PREPEND, "abc", TokenType.UPPER_EXTENDED, "d", 1, true, true, true},
            {PREPEND, "abc", TokenType.MIXED_EXTENDED, "d", 1, true, true, true},
            {PREPEND, "abc", TokenType.DIGIT, "d", 1, true, true, false},
            {PREPEND, "abc", TokenType.LOWERCASE, "d", 1, true, true, false},
            {PREPEND, "abc", TokenType.UPPERCASE, "d", 1, true, true, false},
            {PREPEND, "abc", TokenType.MIXEDCASE, "d", 1, true, true, false},
            {PREPEND, "abc", TokenType.LOWER_EXTENDED, "d", 1, true, true, false},
            {PREPEND, "abc", TokenType.UPPER_EXTENDED, "d", 1, true, true, false},
            {PREPEND, "abc", TokenType.MIXED_EXTENDED, "d", 1, true, true, false},
            {PREPEND, "abc", TokenType.DIGIT, "d", 1, false, true, false},
            {PREPEND, "abc", TokenType.DIGIT, "l", 1, false, true, false},
            {PREPEND, "abc", TokenType.DIGIT, "u", 1, false, true, false},
            {PREPEND, "abc", TokenType.DIGIT, "m", 1, false, true, false},
            {PREPEND, "abc", TokenType.DIGIT, "e", 1, false, true, false},};
    }

    @DataProvider(name = "bad booleans")
    private Object[][] badBooleans() {
        return new Object[][]{
            {"auto", "t"},
            {"random", "t"},
            {"sansVowels", "t"},};
    }

    @Test(dataProvider = "request parameters")
    public void testDisplayIndex(String prepend, String idprefix, String mintType,
            String mintOrder, String vowels, String idLength, String digits,
            String lowercase, String uppercase, String charMapping) throws Exception {
        DefaultSetting originalSetting = getSampleDefaultSetting();
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);                

        request.setParameter("prepend", prepend);
        request.setParameter("idprefix", idprefix);
        request.setParameter("mintType", mintType);
        request.setParameter("mintOrder", mintOrder);
        request.setParameter("vowels", vowels);
        request.setParameter("idlength", idLength);
        request.setParameter("digits", digits);
        request.setParameter("lowercase", lowercase);
        request.setParameter("uppercase", uppercase);
        request.setParameter("charmapping", charMapping);      
        
        when(MinterServiceDao.getCurrentSetting()).thenReturn(originalSetting);
        doNothing().when(MinterServiceDao).updateCurrentSetting(originalSetting);

        Assert.assertEquals("redirect:", Controller.handleForm(request, response));
    }

    @Test
    public void testRequestedParameters() throws Exception {
        //ModelMap.clear();
        Map<String, String> map = getSampleMap();
        DefaultSetting originalSetting = getSampleDefaultSetting();
        DefaultSetting tempSetting = getSampleDefaultSetting();

        tempSetting.setPrepend(map.get("prepend"));
        tempSetting.setPrefix(map.get("prefix"));
        tempSetting.setRootLength(Integer.parseInt(map.get("rootLength")));
        tempSetting.setCharMap(map.get("charMap"));
        tempSetting.setTokenType(Controller.getValidTokenType(map.get("tokenType")));
        tempSetting.setAuto(Boolean.getBoolean(map.get("auto")));
        tempSetting.setRandom(Boolean.getBoolean(map.get("random")));
        tempSetting.setSansVowels(Boolean.getBoolean(map.get("sansVowels")));

        when(MinterServiceDao.getCurrentSetting()).thenReturn(originalSetting);
        when(MinterServiceDao.mint(anyInt(), any(DefaultSetting.class))).
                thenReturn(getSampleSet(tempSetting));

        String jspName = Controller.printPids(AMOUNT, ModelMap, map);
        Assert.assertEquals("mint", jspName);

        String message = (String) ModelMap.get("message");
        Logger.debug(message);
        JSONArray testJsonArray = new JSONArray(message);

        for (int i = 0; i < AMOUNT; i++) {
            JSONObject object = testJsonArray.getJSONObject(i);
            int id = object.getInt("id");
            String name = object.getString("name");
            Assert.assertEquals(id, i);
            testPid(name, tempSetting);
        }
    }

    @Test(dataProvider = "parameters")
    public void testMintPersistedParameters(String prepend, String prefix, TokenType tokenType,
            String charMap, int rootLength, boolean isAuto, boolean isRandom, boolean sansVowel)
            throws Exception {

        DefaultSetting setting = new DefaultSetting(prepend, prefix, tokenType, charMap,
                rootLength, isAuto, isRandom, sansVowel);

        when(MinterServiceDao.getCurrentSetting()).thenReturn(setting);
        when(MinterServiceDao.mint(anyInt(), any(DefaultSetting.class))).
                thenReturn(getSampleSet(setting));

        String jspName = Controller.printPids(AMOUNT, ModelMap, new HashMap<String, String>());
        Assert.assertEquals("mint", jspName);

        String message = (String) ModelMap.get("message");
        Logger.debug(message);
        JSONArray testJsonArray = new JSONArray(message);

        for (int i = 0; i < testJsonArray.length(); i++) {
            JSONObject object = testJsonArray.getJSONObject(i);
            int id = object.getInt("id");
            String name = object.getString("name");
            Assert.assertEquals(id, i);
            testPid(name, setting);
        }
    }

    @Test(expectedExceptions = BadParameterException.class, dataProvider = "bad booleans")
    public void testBadParameterExceptionBoolean(String key, String value) throws Exception {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(key, value);

        DefaultSetting setting = this.getSampleDefaultSetting();

        when(MinterServiceDao.getCurrentSetting()).thenReturn(setting);
        Controller.printPids(AMOUNT, ModelMap, parameters);
    }

    @Test(expectedExceptions = BadParameterException.class)
    public void testBadParameterExceptionTokenType() throws Exception {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("tokenType", "digits");

        DefaultSetting setting = this.getSampleDefaultSetting();

        when(MinterServiceDao.getCurrentSetting()).thenReturn(setting);
        Controller.printPids(AMOUNT, ModelMap, parameters);
    }

    @Test(expectedExceptions = BadParameterException.class)
    public void testBadParameterExceptionCharMap() throws Exception {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("charMap", "dlumea");

        DefaultSetting setting = this.getSampleDefaultSetting();

        when(MinterServiceDao.getCurrentSetting()).thenReturn(setting);
        Controller.printPids(AMOUNT, ModelMap, parameters);
    }

    @Test(expectedExceptions = BadParameterException.class)
    public void testBadParameterExceptionAmount() throws Exception {
        Map<String, String> parameters = new HashMap<>();

        DefaultSetting setting = this.getSampleDefaultSetting();

        when(MinterServiceDao.getCurrentSetting()).thenReturn(setting);
        Controller.printPids(-1, ModelMap, parameters);
    }
    
    @Test(expectedExceptions = BadParameterException.class)
    public void testBadParameterExceptionPrefix() throws Exception {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("prefix", " ");

        DefaultSetting setting = this.getSampleDefaultSetting();

        when(MinterServiceDao.getCurrentSetting()).thenReturn(setting);
        Controller.printPids(AMOUNT, ModelMap, parameters);
    }

    private void testPid(String name, DefaultSetting setting) {
        testPidPrepend(name, setting);
        testPidPrefix(name, setting);

        if (setting.isAuto()) {
            testPidRootLength(name, setting);
            testPidTokenType(name, setting);
        }
        else {
            testPidCharMap(name, setting);
        }

    }

    private void testPidPrepend(String name, DefaultSetting setting) {
        String prepend = setting.getPrepend();

        Assert.assertTrue(name + " testing prepend", name.startsWith(prepend));
    }

    private void testPidPrefix(String name, DefaultSetting setting) {
        String prepend = setting.getPrepend();
        String prefix = setting.getPrefix();

        Assert.assertTrue(name + " testing prepend", name.startsWith(prepend + prefix));
    }

    private void testPidTokenType(String name, DefaultSetting setting) {
        String prepend = setting.getPrepend();
        String prefix = setting.getPrefix();
        TokenType tokenType = setting.getTokenType();
        boolean sansVowel = setting.isSansVowels();

        name = name.replace(prepend, "");

        boolean matchesToken = containsCorrectCharacters(prefix, name, tokenType, sansVowel);
        Assert.assertEquals(name + " testing tokenType", true, matchesToken);
    }

    private void testPidRootLength(String name, DefaultSetting setting) {
        String prepend = setting.getPrepend();
        String prefix = setting.getPrefix();

        name = name.replace(prepend + prefix, "");
        Assert.assertEquals(name + " testing rootLength", name.length(), setting.getRootLength());
    }

    private void testPidCharMap(String name, DefaultSetting setting) {
        String prepend = setting.getPrepend();
        String prefix = setting.getPrefix();
        String charMap = setting.getCharMap();
        boolean sansVowel = setting.isSansVowels();

        name = name.replace(prepend, "");

        boolean matchesToken = containsCorrectCharacters(prefix, name, sansVowel, charMap);
        Assert.assertEquals(name + " testing charMap", true, matchesToken);

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
        return name.matches(String.format("^(%s)%s$", prefix, regex));
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
        return name.matches(String.format("^(%s)%s$", prefix, regex));
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
     * @param sansVowel
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

    private DefaultSetting getSampleDefaultSetting() {
        DefaultSetting setting = new DefaultSetting("", // prepend
                "", // prefix
                TokenType.MIXEDCASE, // tokentype
                "mmm", // charmap
                3, // rootlength
                true, // sansvowel
                true, // auto
                true);  // random

        return setting;
    }

    private Set<Pid> getSampleSet(DefaultSetting setting) {
        IdGenerator generator;
        if (setting.isAuto()) {
            generator = new AutoIdGenerator(setting.getPrefix(),
                    setting.isSansVowels(),
                    setting.getTokenType(),
                    setting.getRootLength());
        }
        else {
            generator = new CustomIdGenerator(setting.getPrefix(),
                    setting.isSansVowels(),
                    setting.getCharMap());

        }

        Set<Pid> set;
        if (setting.isRandom()) {
            set = generator.randomMint(AMOUNT);
        }
        else {
            set = generator.sequentialMint(AMOUNT);
        }

        return set;
    }

    private Map<String, String> getSampleMap() {
        Map<String, String> map = new HashMap<>();

        map.put("prepend", PREPEND);
        map.put("prefix", "xyz");
        map.put("tokenType", "lowercase");
        map.put("charMap", "m");
        map.put("rootLength", "2");
        map.put("auto", "false");
        map.put("random", "false");
        map.put("sansVowel", "false");

        return map;
    }
}
