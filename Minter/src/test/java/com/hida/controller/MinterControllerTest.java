package com.hida.controller;

import com.hida.model.AutoId;
import com.hida.model.AutoIdGenerator;
import com.hida.model.CustomIdGenerator;
import com.hida.model.DefaultSetting;
import com.hida.model.IdGenerator;
import com.hida.model.Pid;
import com.hida.model.TokenType;
import com.hida.service.MinterService;
import com.hida.service.MinterServiceImpl;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Test
    public void testMintPrepend() throws Exception {
        DefaultSetting setting = getSampleDefaultSetting();
        setting.setPrepend(PREPEND);

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
            Assert.assertEquals(name.startsWith(PREPEND), true);
        }

    }
    
    @Test
    public void testBadParameterExceptionBoolean() {
        Assert.fail("unimplemented");
    }

    @Test
    public void testBadParameterExceptionTokenType() {
        Assert.fail("unimplemented");
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
        if(setting.isAuto()){
            generator = new AutoIdGenerator(setting.getPrefix(),
                    setting.isSansVowels(),
                    setting.getTokenType(),
                    setting.getRootLength());
        }else{
            generator = new CustomIdGenerator(setting.getPrefix(), 
                    setting.isSansVowels(), 
                    setting.getCharMap());
            
        }
        
        Set<Pid> set;        
        if(setting.isRandom()){
            set = generator.randomMint(AMOUNT);
        }else{
            set = generator.sequentialMint(AMOUNT);
        }        

        return set;
    }

}
