package com.hida.controller;

import com.hida.model.BadParameterException;
import com.hida.model.DefaultSetting;
import com.hida.model.NotEnoughPermutationsException;
import com.hida.model.Pid;
import com.hida.model.TokenType;
import com.hida.service.MinterService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.locks.ReentrantLock;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * A controller class that paths the user to the template folder in resources.
 *
 * @author lruffin
 */
@RestController
@RequestMapping("/Minter")
public class MinterController {

    /**
     * Default setting values stored in resources folder
     */
    private final String DEFAULT_SETTING_PATH = "src/main/resources/DefaultSetting.properties";

    /* 
     * Logger; logfile to be stored in resource folder    
     */
    private static final Logger Logger = LoggerFactory.getLogger(MinterController.class);

    /**
     * Creates a fair reentrant RequestLock to serialize each request
     * sequentially instead of concurrently.
     */
    private static final ReentrantLock RequestLock = new ReentrantLock(true);

    /**
     * create a database to be used to create and count number of ids
     */
    @Autowired
    private MinterService MinterService;

    /**
     * Redirects to the index after retrieving updated settings from the
     * administration panel.
     *
     * @param request HTTP request from the administration panel
     * @param response HTTP response that redirects to the administration panel
     * after updating the new settings.
     * @return The name of the page to redirect.
     * @throws Exception
     */
    @RequestMapping(value = {""}, method = {RequestMethod.POST})
    public ModelAndView handleForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            // prevents other clients from accessing the database whenever the form is submitted
            RequestLock.lock();
            DefaultSetting oldSetting = MinterService.getCurrentSetting(DEFAULT_SETTING_PATH);
            DefaultSetting newSetting;

            Logger.info("in handleForm");
            String prepend = request.getParameter("prepend");
            String prefix = request.getParameter("idprefix");
            String isAuto = request.getParameter("mintType");
            String isRandom = request.getParameter("mintOrder");
            String includeVowels = request.getParameter("vowels");
            String rootLength = request.getParameter("idlength");
            String digitToken;
            String lowerToken;
            String upperToken;
            String charMap;

            boolean auto = isAuto.equals("auto");
            boolean random = isRandom.equals("random");
            boolean sansVowel = includeVowels == null;

            // assign a non-null value to prepend, prefix, and rootLength
            if (prepend == null) {
                prepend = "";
            }
            if (prefix == null) {
                prefix = "";
            }
            if ((rootLength == null || rootLength.isEmpty()) && !auto) {
                rootLength = "1";
            }

            int length = Integer.parseInt(rootLength);

            // assign values based on which minter type was selected
            if (auto) {
                digitToken = request.getParameter("digits");
                lowerToken = request.getParameter("lowercase");
                upperToken = request.getParameter("uppercase");

                TokenType tokenType;

                // gets the token value
                if (digitToken != null && lowerToken == null && upperToken == null) {
                    tokenType = TokenType.DIGIT;
                }
                else if (digitToken == null && lowerToken != null && upperToken == null) {
                    tokenType = (sansVowel) ? TokenType.LOWER_CONSONANTS 
                            : TokenType.LOWER_ALPHABET;
                }
                else if (digitToken == null && lowerToken == null && upperToken != null) {
                    tokenType = (sansVowel) ? TokenType.UPPER_CONSONANTS 
                            : TokenType.UPPER_ALPHABET;
                }
                else if (digitToken == null && lowerToken != null && upperToken != null) {
                    tokenType = (sansVowel) ? TokenType.MIXED_CONSONANTS 
                            : TokenType.MIXED_ALPHABET;
                }
                else if (digitToken != null && lowerToken != null && upperToken == null) {
                    tokenType = (sansVowel) ? TokenType.LOWER_CONSONANTS_EXTENDED
                            : TokenType.LOWER_ALPHABET_EXTENDED;
                }
                else if (digitToken == null && lowerToken == null && upperToken != null) {
                    tokenType = (sansVowel) ? TokenType.UPPER_CONSONANTS_EXTENDED
                            : TokenType.UPPER_ALPHABET_EXTENDED;
                }
                else if (digitToken != null && lowerToken != null && upperToken != null) {
                    tokenType = (sansVowel) ? TokenType.MIXED_CONSONANTS_EXTENDED
                            : TokenType.MIXED_ALPHABET_EXTENDED;
                }
                else {
                    throw new BadParameterException();
                }

                // create new defaultsetting bject
                newSetting = new DefaultSetting(prepend,
                        prefix,
                        tokenType,
                        oldSetting.getCharMap(),
                        length,
                        sansVowel,
                        auto,
                        random);
            }
            else {
                charMap = request.getParameter("charmapping");
                if (charMap == null || charMap.isEmpty()) {
                    throw new BadParameterException();
                }
                newSetting = new DefaultSetting(prepend,
                        prefix,
                        oldSetting.getTokenType(),
                        charMap,
                        oldSetting.getRootLength(),
                        sansVowel,
                        auto,
                        random);
            }

            MinterService.updateCurrentSetting(DEFAULT_SETTING_PATH, newSetting);
        }
        finally {
            // unlocks RequestLock and gives access to longest waiting thread            
            RequestLock.unlock();
            Logger.warn("Request to update default settings finished, UNLOCKING MINTER");
        }

        // redirect to the administration panel located at http://[domain]/
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:Minter");
        return mav;
    }

    /**
     * Creates a path to mint ids. If parameters aren't given then printPids
     * will resort to using the default values found in minter_config.properties
     *
     * @param requestedAmount requested number of ids to mint
     * @param mav serves as a holder for the model so that attributes can be
     * added.
     * @param parameters parameters given by user to instill variety in ids
     * @return paths user to mint.jsp
     * @throws Exception catches all sorts of exceptions that may be thrown by
     * any methods
     */
    @RequestMapping(value = {"/mint/{requestedAmount}"}, method = {RequestMethod.GET})
    public ModelAndView printPids(@PathVariable long requestedAmount, ModelAndView mav,
            @RequestParam Map<String, String> parameters) throws Exception {

        // ensure that only one thread access the minter at any given time
        RequestLock.lock();
        Logger.warn("Request to Minter made, LOCKING MINTER");

        // message variable to be sent to mint.jsp
        String message;
        try {
            // validate amount
            validateAmount(requestedAmount);

            // override default settings where applicable
            DefaultSetting tempSetting = overrideDefaultSetting(parameters,
                    MinterService.getCurrentSetting(DEFAULT_SETTING_PATH));

            // create the set of ids
            Set<Pid> idList = MinterService.mint(requestedAmount, tempSetting);

            // convert the set of ids into a json array
            message = convertListToJson(idList, tempSetting.getPrepend());
            Logger.info("Message from Minter: " + message);

            // print list of ids to screen
            Logger.debug(message);
            mav.addObject("message", message);

        }
        finally {
            // unlocks RequestLock and gives access to longest waiting thread            
            RequestLock.unlock();
            Logger.warn("Request to Minter Finished, UNLOCKING MINTER");
        }
        mav.setViewName("mint");

        // return to mint       
        return mav;
    }

    /**
     * Maps to the admin panel on the home page.
     *
     * @return name of the index page
     * @throws Exception
     */
    @RequestMapping(value = {""}, method = {RequestMethod.GET})
    public ModelAndView displayIndex() throws Exception {
        ModelAndView model = new ModelAndView();

        // retrieve default values stored in the database
        DefaultSetting defaultSetting = MinterService.getCurrentSetting(DEFAULT_SETTING_PATH);

        // add the values to the settings page so that they can be displayed 
        Logger.info("index page called");
        model.addObject("prepend", defaultSetting.getPrepend());
        model.addObject("prefix", defaultSetting.getPrefix());
        model.addObject("charMap", defaultSetting.getCharMap());
        model.addObject("tokenType", defaultSetting.getTokenType());
        model.addObject("rootLength", defaultSetting.getRootLength());
        model.addObject("isAuto", defaultSetting.isAuto());
        model.addObject("isRandom", defaultSetting.isRandom());
        model.addObject("sansVowel", defaultSetting.isSansVowels());
        model.setViewName("settings");

        return model;
    }

    /**
     * Returns a view that displays the error message of
     * NotEnoughPermutationsException.
     *
     * @param req The HTTP request.
     * @param exception NotEnoughPermutationsException.
     * @return The view of the error message in json format.
     */
    @ExceptionHandler(NotEnoughPermutationsException.class)
    public ModelAndView handlePermutationError(HttpServletRequest req, Exception exception) {
        Logger.error("Request: " + req.getRequestURL()
                + " raised " + exception
                + " with message " + exception.getMessage());

        ModelAndView mav = new ModelAndView();
        mav.addObject("status", 400);
        mav.addObject("exception", exception.getClass().getSimpleName());
        mav.addObject("message", exception.getMessage());
        mav.setViewName("error");
        return mav;

    }

    /**
     * Returns a view that displays the error message of BadParameterException.
     *
     * @param req The HTTP request.
     * @param exception BadParameterException.
     * @return The view of the error message in json format.
     */
    @ExceptionHandler(BadParameterException.class)
    public ModelAndView handleBadParameterError(HttpServletRequest req, Exception exception) {
        Logger.error("Request: " + req.getRequestURL() + " raised " + exception);
        ModelAndView mav = new ModelAndView();
        mav.addObject("status", 400);
        mav.addObject("exception", exception.getClass().getSimpleName());
        mav.addObject("message", exception.getMessage());
        Logger.error("Error with bad parameter: " + exception.getMessage());

        mav.setViewName("error");
        return mav;
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
     * Overrides the default value of cached value with values given in the
     * parameter. If the parameters do not contain any of the valid parameters,
     * the default values are maintained.
     *
     * @param parameters List of parameters given by the client.
     * @param entity
     * @return The settings used for the particular session it was called.
     * @throws BadParameterException
     */
    private DefaultSetting overrideDefaultSetting(final Map<String, String> parameters,
            final DefaultSetting entity) throws BadParameterException {

        String prepend = (parameters.containsKey("prepend"))
                ? parameters.get("prepend")
                : entity.getPrepend();

        String prefix = (parameters.containsKey("prefix"))
                ? validatePrefix(parameters.get("prefix"))
                : entity.getPrefix();

        int rootLength = (parameters.containsKey("rootLength"))
                ? Integer.parseInt(parameters.get("rootLength"))
                : entity.getRootLength();

        String charMap = (parameters.containsKey("charMap"))
                ? validateCharMap(parameters.get("charMap"))
                : entity.getCharMap();

        TokenType tokenType = (parameters.containsKey("tokenType"))
                ? TokenType.valueOf(parameters.get("tokenType"))
                : entity.getTokenType();        

        boolean isAuto = (parameters.containsKey("auto"))
                ? convertBoolean(parameters.get("auto"), "auto")
                : entity.isAuto();

        boolean isRandom = (parameters.containsKey("random"))
                ? convertBoolean(parameters.get("random"), "random")
                : entity.isRandom();

        boolean isSansVowels = (parameters.containsKey("sansVowels"))
                ? convertBoolean(parameters.get("sansVowels"), "sansVowels")
                : entity.isSansVowels();

        return new DefaultSetting(prepend,
                prefix,
                tokenType,
                charMap,
                rootLength,
                isSansVowels,
                isAuto,
                isRandom);
    }

    /**
     * This method is used to check to see whether or not the given parameter is
     * explicitly equivalent to "true" or "false" and returns them respectively.
     * The method provided by the Boolean wrapper class converts all Strings
     * that do no explictly contain true to false.
     *
     * @param parameter the given string to convert.
     * @param parameterType the type of the parameter.
     * @throws BadParameterException Thrown whenever a malformed parameter is
     * formed or passed
     * @return the equivalent version of true or false.
     */
    private boolean convertBoolean(String parameter, String parameterType)
            throws BadParameterException {
        if (parameter.equals("true")) {
            return true;
        }
        else if (parameter.equals("false")) {
            return false;
        }
        else {
            throw new BadParameterException(parameter, parameterType);
        }
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
    private String convertListToJson(Set<Pid> set, String prepend) throws IOException {

        // Jackson objects to format JSON strings
        String jsonString;
        ObjectMapper mapper = new ObjectMapper();
        Object formattedJson;

        // Javax objects to create JSON strings
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        JsonArray jsonArray;

        // convert the set of ids into a json array
        int counter = 0;
        for (Pid id : set) {
            arrayBuilder.add(factory.createObjectBuilder()
                    .add("id", counter)
                    .add("name", prepend + id.getName()));
            counter++;
        }
        jsonArray = arrayBuilder.build();

        // format json array
        formattedJson = mapper.readValue(jsonArray.toString(), Object.class);
        jsonString = mapper.writerWithDefaultPrettyPrinter().
                writeValueAsString(formattedJson);

        return jsonString;
    }

    /**
     * Checks to see if a given charMap is valid
     *
     * @param charMap A sequence of characters used to configure PIDs
     * @return Returns the given charMap if nothing wrong was detected
     * @throws BadParameterException Thrown whenever a bad parameter is
     * detected.
     */
    private String validateCharMap(String charMap) throws BadParameterException {
        if (!charMap.matches("[dlume]+")) {
            throw new BadParameterException(charMap, "charMap");
        }
        return charMap;
    }

    /**
     * Checks to see if the amount is valid
     *
     * @param amount The number of PIDs to be created
     * @throws BadParameterException Thrown whenever a bad parameter is
     * detected.
     */
    private void validateAmount(long amount) throws BadParameterException {
        if (amount < 0) {
            throw new BadParameterException(amount, "amount");
        }
    }

    /**
     * Checks to see if the prefix is valid
     *
     * @param prefix A sequence of characters that appear in the beginning of
     * PIDs
     * @return Returns the given prefix if nothing wrong was detected
     * @throws BadParameterException Thrown whenever a bad parameter is
     * detected.
     */
    private String validatePrefix(String prefix) throws BadParameterException {
        if (!prefix.matches("[a-zA-z0-9]*")) {
            throw new BadParameterException(prefix, "prefix");
        }
        return prefix;
    }
}
