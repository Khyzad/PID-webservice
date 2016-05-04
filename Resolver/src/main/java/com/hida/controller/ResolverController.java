package com.hida.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.hida.model.Citation;
import com.hida.service.ResolverService;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller class that paths the user to template folder in resources.
 *
 * @author leland lopez
 * @author lruffin
 */
@RestController
@RequestMapping("/Resolver")
public class ResolverController {

    /* 
     * Logger; logfile to be stored in resource folder    
     */
    private static final org.slf4j.Logger Logger
            = LoggerFactory.getLogger(ResolverController.class);

    private ResolverService ResolverService;

    /**
     * Maps to the home page.
     *
     * @return view to the home page
     */
    @RequestMapping(value = {""}, method = {RequestMethod.GET})
    public String displayIndex() {        
        return "";
    }

    /**
     * matches url: /PURL/retrieve retrieves corresponding citation row of
     * provided citation returns model - purl and view : retrieve if successful
     * returns model - null if not
     *
     * @param purl purl of desired retrieved row
     * @return ModelAndView Holds resulting Model and view information
     * @throws IOException Thrown by Jackson library
     */
    @RequestMapping("/retrieve")
    public String retrieve(@RequestParam(value = "purl", required = true) String purl)
            throws IOException {
        if (Logger.isInfoEnabled()) {
            Logger.info("Retrieve was Called");
        }
        // retrieve citation jsonString
        Citation citation = ResolverService.retrieveCitation(purl);

        // show retrieve view, attach citation jsonString.  converted to json at view.
        String jsonString = this.convertCitationToJson(citation);

        Logger.info("Retrieve returned: " + null);
        return jsonString;
    }

    /**
     * matches url: /PURL/edit edit Citation row url, with provided url returns
     * model : Citation and view : edit if successful returns model : null if
     * not
     *
     * @param purl purl of desired edited row
     * @param url The url that the desired Citation will have
     * @return ModelAndView Holds resulting Model and view information
     * @throws IOException Thrown by Jackson library
     */
    @RequestMapping("/edit")
    public String edit(@RequestParam(value = "purl", required = true) String purl,
            @RequestParam(value = "url", required = true) String url) throws IOException {
        if (Logger.isInfoEnabled()) {
            Logger.info("Edit was Called");
        }
        // edit the purl and then retrieve its entire contents
        ResolverService.editUrl(purl, url);
        Citation citation = ResolverService.retrieveCitation(purl);

        // show edit view, attach purl jsonString.  converted to json at view.
        String jsonString = this.convertCitationToJson(citation);

        Logger.info("Edit returned: " + jsonString);
        return jsonString;

    }

    /**
     * matches url: /PURL/insert inserts Citation url, erc, who, what, when to
     * new row of table returns model : Citation and view : insert if successful
     * returns model : null if not
     *
     * @param purl Citation to be inserted
     * @param url url to be inserted
     * @param erc erc to be inserted
     * @param who who to be inserted
     * @param what what to be inserted
     * @param when when to be insertd
     * @return ModelAndView Holds resulting Model and view information
     * @throws IOException Thrown by Jackson library
     */
    @RequestMapping("/insert")
    public String insert(@RequestParam(value = "purl", required = true) String purl,
            @RequestParam(value = "url", required = true) String url,
            @RequestParam(value = "erc", required = true) String erc,
            @RequestParam(value = "who", required = true) String who,
            @RequestParam(value = "what", required = true) String what,
            @RequestParam(value = "when", required = true) String when
    ) throws IOException {
        if (Logger.isInfoEnabled()) {
            Logger.info("Insert was Called");
        }
        // create purl jsonString to store information
        Citation citation = new Citation(purl, url, erc, who, what, when);

        // insert purl
        ResolverService.insertCitation(citation);

        //show edit view, attach purl jsonString.  converted to json at view.
        String jsonString = this.convertCitationToJson(citation);

        Logger.info("insert returned: " + null);
        return jsonString;

    }

    /**
     * matches url: /PURL/delete deletes row of table with corresponding purl
     * returns view : deleted if successful returns model : null if not
     *
     * @param purl purl of desired deleted row
     * @return ModelAndView Holds resulting Model and view information
     * @throws IOException Thrown by Jackson library
     */
    @RequestMapping("/delete")
    public ModelAndView delete(@RequestParam(value = "purl", required = true) String purl)
            throws IOException {
        if (Logger.isInfoEnabled()) {
            Logger.info("Insert was Called");
        }
        // create json jsonString that designates success
        final String resultJson = "{\"result\":\"deleted\"}";

        // delete Citation
        ResolverService.deleteCitation(purl);

        //show edit view, attach Citation jsonString.  converted to json at view.
        ModelAndView mv = new ModelAndView("result", "message", resultJson);
        Logger.info("insert returned: " + resultJson);

        mv.addObject("message", resultJson);
        return mv;
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

        mav.setViewName("error");
        return mav;
    }

    /**
     * Creates a Json jsonString based off of a citation given in the parameter
     *
     * @param citation Entity to convert the jsonString into
     * @return A reference to a String that contains Json set of ids
     * @throws IOException Thrown by Jackson's IO framework
     */
    private String convertCitationToJson(Citation citation) throws IOException {

        // Jackson objects to format JSON strings
        String jsonString;
        ObjectMapper mapper = new ObjectMapper();
        Object formattedJson;

        // create json jsonString
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("pid", citation.getPurl())
                .add("url", citation.getUrl())
                .add("erc", citation.getErc())
                .add("who", citation.getWho())
                .add("what", citation.getWhat())
                .add("date", citation.getDate())
                .build();

        // format json object
        formattedJson = mapper.readValue(jsonObject.toString(), Object.class);
        jsonString = mapper.writerWithDefaultPrettyPrinter().
                writeValueAsString(formattedJson);

        return jsonString;
    }
}
