package com.hida.controller;

import com.hida.model.Purl;
import com.hida.service.ResolverService;
import java.util.Map;
import org.json.JSONObject;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

/**
 * Tests the functionality of ResolverController to see if it is capable of
 * performing REST calls.
 *
 * @author lruffin
 */
public class ResolverControllerTest {

    @Mock
    private ResolverService Service;

    @InjectMocks
    private ResolverController Controller;

    @BeforeClass
    public void setUpClass() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test the retrieve REST call
     *
     * @throws Exception
     */
    @Test
    public void testRetrieve() throws Exception {
        Purl entity = getSamplePurl();
        when(Service.retrieveModel(any(String.class))).thenReturn(entity);

        // test to see that the correct view is returned
        ModelAndView mav = Controller.retrieve("");
        Assert.assertEquals("message", mav.getViewName());

        // test to see that Service at least makes a call to get a Purl object
        verify(Service, atLeastOnce()).retrieveModel(any(String.class));

        // test to see that Json is formated properly
        Map<String, Object> map = mav.getModel();
        String jsonObject = (String) map.get("purl");
        testJsonObject(jsonObject, entity);
    }

    /**
     * Test the edit REST call
     *
     * @throws Exception
     */
    @Test
    public void testEdit() throws Exception {
        // create purl entity to reflect the change that happens after calling edit
        Purl entity = getSamplePurl();

        // pretend the entity was edited and return new entity
        when(Service.editURL(any(String.class), any(String.class))).thenReturn(true);
        when(Service.retrieveModel(any(String.class))).thenReturn(entity);

        // test to see that the correct view is returned
        ModelAndView mav = Controller.edit("", "");
        Assert.assertEquals("message", mav.getViewName());

        // test to see that Json is formated properly
        Map<String, Object> map = mav.getModel();
        String jsonObject = (String) map.get("purl");
        testJsonObject(jsonObject, entity);
    }

    /**
     * Tests the insert REST call
     *
     * @throws Exception
     */
    @Test
    public void testInsert() throws Exception {
        // create purl entites to reflect the change that happens after calling edit
        Purl entity = getSamplePurl();

        // pretend the entity was instered 
        when(Service.insertPURL(any(Purl.class))).thenReturn(true);

        // call insert
        ModelAndView mav = Controller.insert(entity.getIdentifier(),
                entity.getURL(),
                entity.getERC(),
                entity.getWho(),
                entity.getWhat(),
                entity.getDate());

        // test to see that the correct view is returned
        Assert.assertEquals("message", mav.getViewName());

        // test to see that Json is formated properly
        Map<String, Object> map = mav.getModel();
        String jsonObject = (String) map.get("purl");
        testJsonObject(jsonObject, entity);
    }

    /**
     * Tests the delete REST call
     *
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        // pretend the entity was deleted
        when(Service.deletePURL(any(String.class))).thenReturn(true);

        // call delete
        ModelAndView mav = Controller.delete("");

        // test to see that the correct view is returned
        Assert.assertEquals("deleted", mav.getViewName());

        // test to see that Json is formated properly
        Map<String, Object> map = mav.getModel();
        String result = (String) map.get("deleteSuccess");

        Assert.assertEquals("{\"result\":\"deleted\"}", result);
    }   

    /**
     * Tests the given jsonObject to ensure that it matches the given entity
     *
     * @param jsonObject Json object to test
     * @param entity Entity to check against
     */
    private void testJsonObject(String jsonObject, Purl entity) {
        JSONObject testObject = new JSONObject(jsonObject);

        String pid = testObject.getString("pid");
        String url = testObject.getString("url");
        String erc = testObject.getString("erc");
        String who = testObject.getString("who");
        String what = testObject.getString("what");
        String time = testObject.getString("date");

        Assert.assertEquals(entity.getIdentifier(), pid);
        Assert.assertEquals(entity.getURL(), url);
        Assert.assertEquals(entity.getERC(), erc);
        Assert.assertEquals(entity.getWho(), who);
        Assert.assertEquals(entity.getWhat(), what);
        Assert.assertEquals(entity.getDate(), time);

    }

    /**
     * Gets a sample Purl object for testing
     *
     * @return sample Purl object
     */
    private Purl getSamplePurl() {
        Purl purl = new Purl("samplePid",
                "sampleURL",
                "sampleERC",
                "sampleWho",
                "sampleWhat",
                "sampleTime");

        return purl;
    }
}
