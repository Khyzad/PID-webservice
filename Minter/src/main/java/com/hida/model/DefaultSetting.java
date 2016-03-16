package com.hida.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * missing javadoc
 * @author lruffin
 */
@Entity
@Table(name = "DEFAULT_SETTING")
public class DefaultSetting extends Setting {

    @Column(name = "PREPEND", nullable = false)
    private String Prepend;
    
    @Column(name="ISAUTO")
    private boolean Auto;
    
    @Column(name="ISRANDOM")
    private boolean Random;


    /**
     * Constructor; missing javadoc
     *
     * @param Prepend
     * @param Prefix
     * @param TokenType
     * @param CharMap
     * @param RootLength
     * @param SansVowels
     * @param Auto
     * @param Random
     */
    public DefaultSetting(String Prepend, String Prefix, TokenType TokenType, String CharMap,
            int RootLength, boolean SansVowels, boolean Auto, boolean Random) {
        super(Prefix, TokenType, CharMap, RootLength, SansVowels);
        this.Prepend = Prepend;
        this.Auto = Auto;
        this.Random = Random;
                
    }

    /**
     * Default constructor
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
