package com.hida.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.hida.model.Purl;
import com.hida.service.ResolverService;
import java.io.IOException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * A controller class that paths the user to all jsp files in WEB_INF/jsp.
 *
 * @author leland lopez
 * @author lruffin
 */
@Controller
@RequestMapping("/")
public class ResolverController {

    /* 
     * Logger; logfile to be stored in resource folder    
     */
    private static final org.slf4j.Logger Logger = 
            LoggerFactory.getLogger(ResolverController.class);
    
    @Autowired
    private ResolverService ResolverService;
    
    /**
     * Maps to the home page.
     *
     * @return view to the home page
     */
    @RequestMapping(value = {""}, method = {RequestMethod.GET})
    public ModelAndView displayIndex() {
        ModelAndView model = new ModelAndView();       
        model.setViewName("home");

        return model;
    }

    /**
     * matches url: /PURL/retrieve retrieves corresponding purl row of provided
     * purlid returns model - purl and view : retrieve if successful returns
     * model - null if not
     *
     * @param purlid purlid of desired retrieved row
     * @return ModelAndView
     * @throws IOException throws if connection could not be made
     */
    @RequestMapping("/retrieve")
    public ModelAndView retrieve(@RequestParam(value = "purlid", required = true) String purlid)
            throws IOException {
        if (Logger.isInfoEnabled()) {
            Logger.info("Retrieve was Called");
        }
        
        Purl purl = ResolverService.retrieveModel(purlid);	//retrieve purl object
        
        if (purl != null) {
            //show retrieve view, attach purl object.  converted to json at view.
            ModelAndView mv = new ModelAndView("retrieve", "purl", purl);
            Logger.info("Retrieve returned: " + purl.toJSON());
            return mv;
        }
        else {
            ModelAndView mv = new ModelAndView("null");
            Logger.info("insert returned: " + null);
            return mv;
        }
    }

    /**
     * matches url: /PURL/edit edit purlid row url, with provided url returns
     * model : purl and view : edit if successful returns model : null if not
     *
     * @param purlid purlid of desired edited row
     * @param url url that desired row url will be changed to
     * @return ModelAndView
     * @throws IOException throws if connection could not be made
     */
    @RequestMapping("/edit")
    public ModelAndView edit(@RequestParam(value = "purlid", required = true) String purlid,
            @RequestParam(value = "url", required = true) String url) throws IOException {
        if (Logger.isInfoEnabled()) {
            Logger.info("Edit was Called");
        }
        ResolverService.editURL(purlid, url);
        Purl purl = ResolverService.retrieveModel(purlid);
                
        if (purl != null) {
            //show edit view, attach purl object.  converted to json at view.
            ModelAndView mv = new ModelAndView("edit", "purl", purl);
            Logger.info("Edit returned: " + purl.toJSON());
            return mv;
        }
        else {
            ModelAndView mv = new ModelAndView("null");
            Logger.info("Edit returned: " + null);
            return mv;
        }
    }

    /**
     * matches url: /PURL/insert inserts purlid, url, erc, who, what, when to
     * new row of table returns model : purl and view : insert if successful
     * returns model : null if not
     *
     * @param purlid purlid to be inserted
     * @param url url to be inserted
     * @param erc erc to be inserted
     * @param who who to be inserted
     * @param what what to be inserted
     * @param when when to be insertd
     * @return ModelAndView
     * @throws IOException throws if db conn not successful
     */
    @RequestMapping("/insert")
    public ModelAndView insert(@RequestParam(value = "purlid", required = true) String purlid,
            @RequestParam(value = "url", required = true) String url,
            @RequestParam(value = "erc", required = true) String erc,
            @RequestParam(value = "who", required = true) String who,
            @RequestParam(value = "what", required = true) String what,
            @RequestParam(value = "when", required = true) String when
    ) throws IOException {
        if (Logger.isInfoEnabled()) {
            Logger.info("Insert was Called");
        }
        
        if (ResolverService.insertPURL(purlid, url, erc, who, what, when)) {
            Purl purl = ResolverService.retrieveModel(purlid);
                        
            //show edit view, attach purl object.  converted to json at view.
            ModelAndView mv = new ModelAndView("insert", "purl", purl);
            Logger.info("insert returned: " + purl.toJSON());
            return mv;
        }
        else {
            //show edit view, attach purl object.  converted to json at view.
            ModelAndView mv = new ModelAndView("null");
            Logger.info("insert returned: " + null);
            return mv;
        }
    }

    /**
     * matches url: /PURL/delete deletes row of table with corresponding purlid
     * returns view : deleted if successful returns model : null if not
     *
     * @param purlid purlid of desired deleted row
     * @return ModelAndView
     * @throws IOException throws if dbConn is not successful
     */
    @RequestMapping("/delete")
    public ModelAndView delete(@RequestParam(value = "purlid", required = true) String purlid)
            throws IOException {
        if (Logger.isInfoEnabled()) {
            Logger.info("Insert was Called");
        }

        if (ResolverService.deletePURL(purlid)) {           
            //show edit view, attach purl object.  converted to json at view.
            ModelAndView mv = new ModelAndView("deleted");

            Logger.info("{\"result\":\"success\"}");
            return mv;
        }
        else {
            //show edit view, attach purl object.  converted to json at view.
            ModelAndView mv = new ModelAndView("null");
            Logger.info("insert returned: " + null);
            return mv;
        }
    }
}
