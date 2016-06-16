package com.hida.model;

import java.util.Objects;
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
    private String prepend_;

    @Column(name = "ISAUTO")
    private boolean auto_;

    @Column(name = "ISRANDOM")
    private boolean random_;

    @Column(name = "CACHESIZE")
    private long cacheSize_;

    /**
     * Constructor used to create a DefaultSetting entity
     *
     * @param Prepend Primarily used to turn a Pid into a PURL
     * @param Prefix A sequence of characters that appear in the beginning of
     * PIDs
     * @param CacheSize The size of the cache that'll be generated whenever the
     * context is refreshed
     * @param TokenType An enum used to configure PIDS
     * @param CharMap A sequence of characters used to configure PIDs
     * @param RootLength Designates the length of the id's root
     * @param SansVowels Dictates whether or not vowels are allowed
     * @param Auto Determines which generator, either auto_ or Custom, will be
     * used
     * @param Random Determines if the PIDs are created randomly or sequentially
     */
    public DefaultSetting(String Prepend, String Prefix, long CacheSize, Token TokenType,
            String CharMap, int RootLength, boolean SansVowels, boolean Auto, boolean Random) {
        super(Prefix, TokenType, CharMap, RootLength, SansVowels);
        this.prepend_ = Prepend;
        this.cacheSize_ = CacheSize;
        this.auto_ = Auto;
        this.random_ = Random;

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.prepend_);
        hash = 43 * hash + Objects.hashCode(this.getCharMap());
        hash = 43 * hash + Objects.hashCode(this.getPrefix());
        hash = 43 * hash + Objects.hashCode(this.getTokenType());
        hash = 43 * hash + Objects.hashCode(this.getRootLength());
        hash = 43 * hash + Objects.hashCode(this.isSansVowels());
        hash = 43 * hash + (this.auto_ ? 1 : 0);
        hash = 43 * hash + (this.random_ ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultSetting other = (DefaultSetting) obj;
        
        return !(!Objects.equals(this.prepend_, other.prepend_)
                && !Objects.equals(this.getPrefix(), other.getPrefix())
                && !Objects.equals(this.getRootLength(), other.getRootLength())
                && !Objects.equals(this.getCharMap(), other.getCharMap())
                && !Objects.equals(this.getTokenType(), other.getTokenType())
                && !Objects.equals(this.isSansVowels(), other.isSansVowels())
                && this.auto_ != other.auto_
                && this.random_ != other.random_);
    }

    /**
     * No-arg constructor used by Hibernate
     */
    public DefaultSetting() {

    }

    public String getPrepend() {
        return this.prepend_;
    }

    public void setPrepend(String prepend) {
        this.prepend_ = prepend;
    }

    public boolean isAuto() {
        return auto_;
    }

    public void setAuto(boolean Auto) {
        this.auto_ = Auto;
    }

    public boolean isRandom() {
        return random_;
    }

    public void setRandom(boolean Random) {
        this.random_ = Random;
    }

    public long getCacheSize() {
        return cacheSize_;
    }

    public void setCacheSize(long CacheSize) {
        this.cacheSize_ = CacheSize;
    }
}
