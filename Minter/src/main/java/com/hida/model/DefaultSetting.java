package com.hida.model;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A POJO representing a type of setting that records the values of the
 * IdGenerators used to create Pids and the current default values of all the
 * parameters.
 *
 * @author lruffin
 */
@Entity
@Table(name = "DEFAULT_SETTING")
public class DefaultSetting extends Setting {

    @Column(name = "PREPEND", nullable = false)
    private String Prepend;

    @Column(name = "ISAUTO")
    private boolean Auto;

    @Column(name = "ISRANDOM")
    private boolean Random;

    /**
     * Constructor used to create a DefaultSetting entity
     *
     * @param Prepend Primarily used to turn a Pid into a PURL
     * @param Prefix A sequence of characters that appear in the beginning of
     * PIDs
     * @param TokenType An enum used to configure PIDS
     * @param CharMap A sequence of characters used to configure PIDs
     * @param RootLength Designates the length of the id's root
     * @param SansVowels Dictates whether or not vowels are allowed
     * @param Auto Determines which generator, either Auto or Custom, will be
     * used
     * @param Random Determines if the PIDs are created randomly or sequentially
     */
    public DefaultSetting(String Prepend, String Prefix, TokenType TokenType, String CharMap,
            int RootLength, boolean SansVowels, boolean Auto, boolean Random) {
        super(Prefix, TokenType, CharMap, RootLength, SansVowels);
        this.Prepend = Prepend;
        this.Auto = Auto;
        this.Random = Random;

    }     

    /**
     * No-arg constructor used by Hibernate
     */
    public DefaultSetting() {

    }

    public String getPrepend() {
        return this.Prepend;
    }

    public void setPrepend(String prepend) {
        this.Prepend = prepend;
    }

    public boolean isAuto() {
        return Auto;
    }

    public void setAuto(boolean Auto) {
        this.Auto = Auto;
    }

    public boolean isRandom() {
        return Random;
    }

    public void setRandom(boolean Random) {
        this.Random = Random;
    }

}
