package com.hida.controller;

import com.hida.model.BadParameterException;
import com.hida.model.DefaultSetting;
import com.hida.model.IdGenerator;
import com.hida.model.Pid;
import com.hida.model.TokenType;
import com.hida.service.MinterService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
import java.util.Set;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * A Controller that handles the requests and responses between the user and the
 * Minter.
 *
 * @author lruffin
 */
@RestController
@RequestMapping("/Minter")
public class MinterController {

    /**
     * Default setting values stored in resources folder
     */
    private final String DEFAULT_SETTING_PATH = "DefaultSetting.properties";

    /* 
     * Logger; logfile to be stored in resource folder    
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MinterController.class);

    /**
     * A fair lock used to synchronize access to the minter service.
     */
    private static final ReentrantLock RequestLock = new ReentrantLock(true);

    /**
     * The service to use to mint IDs.
     */
    @Autowired
    private MinterService MinterService;

    /**
     * Using values sent from the /administration end point, this method updates
     * the DefaultSetting object, database, and the properties file. The names
     * of the requested parameters are given by the name attributes in the
     * administration panel.
     *
     * @param prepend String of characters that determine the domain of each Pid
     * @param idprefix String of characters that prefixes each Pid
     * @param mintType Auto Generator is used if true, Custom Generator if false
     * @param mintOrder Pids are generated randomly if true, ordered if false
     * @param sansvowel Pids will contain vowels if true, false othwerise
     * @param idlength the length of the pid's root name
     * @param charmapping the mapping used when Custom Generator is selected
     * @param digits digits is included if selected
     * @param lowercase lowercase is included if selected
     * @param uppercase uppercase is included if selected
     * @param request HTTP request from the administration panel
     * @param response HTTP response that redirects to the administration panel
     * after updating the new settings.
     * @throws Exception
     */
    @RequestMapping(value = {"/administration"}, method = {RequestMethod.POST})
    public void handleForm(@RequestParam(required = false) String prepend,
            @RequestParam(required = false) String idprefix,
            @RequestParam String mintType,
            @RequestParam String mintOrder,
            @RequestParam(defaultValue = "true") boolean sansvowel,
            @RequestParam(required = false, defaultValue = "-1") int idlength,
            @RequestParam(required = false) String charmapping,
            @RequestParam(required = false) boolean digits,
            @RequestParam(required = false) boolean lowercase,
            @RequestParam(required = false) boolean uppercase,
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {

        // prevents other clients from accessing the database whenever the form is submitted            
        RequestLock.lock();
        try {

            DefaultSetting oldSetting = MinterService.getCurrentSetting(DEFAULT_SETTING_PATH);
            DefaultSetting newSetting;

            LOGGER.info("in handleForm");
            boolean auto = mintType.equals("auto");
            boolean random = mintOrder.equals("random");

            // assign values based on which minter type was selected
            if (auto) {
                // gets the token value
                TokenType tokenType;
                if (digits && !lowercase && !uppercase) {
                    tokenType = TokenType.DIGIT;
                }
                else if (!digits && lowercase && !uppercase) {
                    tokenType = (sansvowel) ? TokenType.LOWER_CONSONANTS
                            : TokenType.LOWER_ALPHABET;
                }
                else if (!digits && !lowercase && uppercase) {
                    tokenType = (sansvowel) ? TokenType.UPPER_CONSONANTS
                            : TokenType.UPPER_ALPHABET;
                }
                else if (!digits && lowercase && uppercase) {
                    tokenType = (sansvowel) ? TokenType.MIXED_CONSONANTS
                            : TokenType.MIXED_ALPHABET;
                }
                else if (digits && lowercase && !uppercase) {
                    tokenType = (sansvowel) ? TokenType.LOWER_CONSONANTS_EXTENDED
                            : TokenType.LOWER_ALPHABET_EXTENDED;
                }
                else if (digits && !lowercase && uppercase) {
                    tokenType = (sansvowel) ? TokenType.UPPER_CONSONANTS_EXTENDED
                            : TokenType.UPPER_ALPHABET_EXTENDED;
                }
                else if (digits && lowercase && uppercase) {
                    tokenType = (sansvowel) ? TokenType.MIXED_CONSONANTS_EXTENDED
                            : TokenType.MIXED_ALPHABET_EXTENDED;
                }
                else {
                    throw new BadParameterException();
                }

                // create new defaultsetting object
                newSetting = new DefaultSetting(prepend,
                        idprefix,
                        tokenType,
                        oldSetting.getCharMap(),
                        idlength,
                        sansvowel,
                        auto,
                        random);
            }
            else {
                // validate charmapping
                if (charmapping == null || charmapping.isEmpty()) {
                    throw new BadParameterException();
                }

                // create new defaultsetting object
                newSetting = new DefaultSetting(prepend,
                        idprefix,
                        oldSetting.getTokenType(),
                        charmapping,
                        oldSetting.getRootLength(),
                        sansvowel,
                        auto,
                        random);
            }

            MinterService.updateCurrentSetting(DEFAULT_SETTING_PATH, newSetting);
        }
        finally {
            // unlocks RequestLock and gives access to longest waiting thread            
            RequestLock.unlock();
            LOGGER.warn("Request to update default settings finished, UNLOCKING MINTER");
        }

        // redirect to the administration panel      
        response.sendRedirect("administration");
    }

    /**
     * Creates a path to mint ids. If parameters aren't given then mintPids will
     * resort to using the default values found in DefaultSetting.properties
     *
     * @param requestedAmount requested number of ids to mint
     * @param parameters parameters given by user to instill variety in ids
     * @return paths user to mint.jsp
     * @throws Exception catches all sorts of exceptions that may be thrown by
     * any methods
     */
    @ResponseStatus(code = HttpStatus.CREATED)
    @RequestMapping(value = {"/mint/{requestedAmount}"},
            method = {RequestMethod.GET},
            produces = "application/json")
    public Set<Pid> mintPids(@PathVariable long requestedAmount,
            @RequestParam Map<String, String> parameters) throws Exception {

        // ensure that only one thread access the minter at any given time
        RequestLock.lock();

        Set<Pid> pidSet;
        try {
            LOGGER.info("Request to Minter made, LOCKING MINTER");

            // validate amount
            validateAmount(requestedAmount);

            // override default settings where applicable
            DefaultSetting tempSetting = overrideDefaultSetting(parameters,
                    MinterService.getCurrentSetting(DEFAULT_SETTING_PATH));

            // create the set of ids
            pidSet = MinterService.mint(requestedAmount, tempSetting);
        }
        finally {
            // unlocks RequestLock and gives access to longest waiting thread            
            RequestLock.unlock();
            LOGGER.info("Request to Minter Finished, UNLOCKING MINTER");
        }

        // return to mint       
        return pidSet;
    }

    /**
     * Maps to the administration panel on the administration path.
     *
     * @return name of the index page
     * @throws Exception
     */
    @RequestMapping(value = {"/administration"}, method = {RequestMethod.GET})
    public ModelAndView displayAdministrationPanel() throws Exception {
        ModelAndView model = new ModelAndView();

        // retrieve default values stored in the database
        DefaultSetting defaultSetting = MinterService.getCurrentSetting(DEFAULT_SETTING_PATH);

        // add the values to the settings page so that they can be displayed 
        LOGGER.info("index page called");
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
     * Maps to the root of the application
     *
     * @return
     */
    @RequestMapping(value = {""}, method = {RequestMethod.GET})
    public String displayIndex() {
        return "";
    }

    /**
     * Handles any exception that may be caught within the program
     *
     * @param req the HTTP request
     * @param exception the caught exception
     * @return The view of the error message
     */
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneralError(HttpServletRequest req, Exception exception) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("status", 500);
        mav.addObject("exception", exception.getClass().getSimpleName());
        mav.addObject("message", exception.getMessage());
        LOGGER.error("General Error: " + exception.getMessage());

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
                ? validateRootLength(Integer.parseInt(parameters.get("rootLength")))
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
     *
     * @param parameter the given string to convert.
     * @param parameterType the type of the parameter.
     * @throws BadParameterException Thrown whenever parameter is neither "true"
     * nor "false"
     * @return true if the parameter is "true", false if the parameter is
     * "false"
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
     * Checks to see if a given charMap is valid. A valid CharMap follows the
     * regular expression [dlume]*
     *
     * @param charMap A sequence of characters used to configure PIDs
     * @return Returns the given charMap if nothing wrong was detected
     * @throws BadParameterException Thrown whenever a bad parameter is
     * detected.
     */
    private String validateCharMap(String charMap) throws BadParameterException {
        if (!IdGenerator.isValidCharMap(charMap)) {
            throw new BadParameterException(charMap, "charMap");
        }
        return charMap;
    }

    /**
     * Checks to see if the amount is valid. A valid amount is greater than or
     * equal to 0.
     *
     * @param amount The number of PIDs to be created
     * @throws BadParameterException Thrown whenever a bad parameter is
     * detected.
     */
    private void validateAmount(long amount) throws BadParameterException {
        if (!IdGenerator.isValidAmount(amount)) {
            throw new BadParameterException(amount, "amount");
        }
    }

    /**
     * Checks to see if the rootLength is valid. A valid amount is greater than
     * 0 and less than 11. 
     * @param rootLength
     * @return
     * @throws BadParameterException 
     */
    private int validateRootLength(int rootLength) throws BadParameterException {
        if (!IdGenerator.isValidRootLength(rootLength)) {
            throw new BadParameterException(rootLength, "rootLength");
        }
        return rootLength;
    }

    /**
     * Checks to see if the prefix is valid. A valid prefix follows the regular
     * expression [a-zA-z0-9]*
     *
     * @param prefix A sequence of characters that appear in the beginning of
     * PIDs
     * @return Returns the given prefix if nothing wrong was detected
     * @throws BadParameterException Thrown whenever a bad parameter is
     * detected.
     */
    private String validatePrefix(String prefix) throws BadParameterException {
        if (!IdGenerator.isValidPrefix(prefix)) {
            throw new BadParameterException(prefix, "prefix");
        }
        return prefix;
    }
}
