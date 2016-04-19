package com.hida.model;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * missing javadoc
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultSetting)) {
            return false;
        }
        final DefaultSetting paramId = (DefaultSetting) obj;

        if (!paramId.getPrepend().equals(this.getPrepend())) {
            return false;
        }
        if (!paramId.getPrefix().equals(this.getPrefix())) {
            return false;
        }
        if (!paramId.getCharMap().equals(this.getCharMap())) {
            return false;
        }
        if (paramId.getTokenType() != this.getTokenType()) {
            return false;
        }
        if (paramId.getRootLength() != this.getRootLength()) {
            return false;
        }
        if (paramId.Auto != this.Auto) {
            return false;
        }
        if (paramId.Random != this.Random) {
            return false;
        }
        return paramId.isSansVowels() == this.isSansVowels();

    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.Prepend);
        hash = 37 * hash + (this.Auto ? 1 : 0);
        hash = 37 * hash + (this.Random ? 1 : 0);
        return hash;
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
