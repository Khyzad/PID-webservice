package com.hida.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.hida.model.Purl;
import com.hida.service.ResolverService;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
    private static final org.slf4j.Logger Logger
            = LoggerFactory.getLogger(ResolverController.class);

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
            ModelAndView mv = new ModelAndView("retrieve", "purl", this.convertListToJson(purl));            
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
            ModelAndView mv = new ModelAndView("edit", "purl", this.convertListToJson(purl));
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
        Purl purl = new Purl(purlid, url, erc, who, what, when);

        if (ResolverService.insertPURL(purl)) {
            //show edit view, attach purl object.  converted to json at view.
            ModelAndView mv = new ModelAndView("insert", "purl", this.convertListToJson(purl));
            Logger.info("insert returned: " + this.convertListToJson(purl));
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
            ModelAndView mv = 
                    new ModelAndView("deleted","deleteSuccess", "{\"result\":\"deleted\"}");
            

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

    /**
     * Throws any exception that may be caught within the program
     *
     * @param req the HTTP request
     * @param exception the caught exception
     * @return The view of the error message
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneralError(HttpServletRequest req, Exception exception) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("status", 500);
        mav.addObject("exception", exception.getClass().getSimpleName());
        mav.addObject("message", exception.getMessage());
        Logger.error("General Error: " + exception.getMessage());

        StackTraceElement[] trace = exception.getStackTrace();
        String error = "";
        for (StackTraceElement element : trace) {
            error += element.toString() + "\n";
        }

        mav.addObject("stacktrace", error);

        mav.setViewName("error");
        return mav;
    }

    /**
     * Creates a Json object based off a set of ids given in the parameter
     *
     * @param set A set of ids to display into JSON
     * @param prepend A value to attach to the beginning of every id. Typically
     * used to determine the format of the id. For example, ARK or DOI.
     * @return A reference to a String that contains Json set of ids
     * @throws IOException thrown whenever a file could not be found
     */
    private String convertListToJson(Purl purl) throws IOException {

        // Jackson objects to format JSON strings
        String jsonString;
        ObjectMapper mapper = new ObjectMapper();
        Object formattedJson;

        // create json object
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("pid", purl.getIdentifier())
                .add("url", purl.getURL())
                .add("erc", purl.getERC())
                .add("who", purl.getWho())
                .add("what", purl.getWhat())
                .add("date", purl.getDate())
                .build();

        // format json array
        formattedJson = mapper.readValue(jsonObject.toString(), Object.class);
        jsonString = mapper.writerWithDefaultPrettyPrinter().
                writeValueAsString(formattedJson);

        return jsonString;
    }
}
