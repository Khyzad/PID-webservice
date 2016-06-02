package com.hida.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A type of Setting that references the default values that could be used to
 * create a set of Pids. These default values can be overridden and only one
 * DefaultSetting object is persisted in the database at any given time. 
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
    public DefaultSetting(String Prepend, String Prefix, Token TokenType, String CharMap,
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
