package com.hida.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A POJO representing a type of setting that records the amount of Pids that
 * were created using the values provided in the constructor.
 *
 * @author lruffin
 */
@Entity
@Table(name = "USED_SETTING")
public class UsedSetting extends Setting {

    @Column(name = "AMOUNT")
    private long Amount;

    /**
     * Constructor used to create a UsedSetting entity
     *
     * @param Prefix A sequence of characters that appear in the beginning of
     * PIDs
     * @param TokenType An enum used to configure PIDS
     * @param CharMap A sequence of characters used to configure PIDs
     * @param RootLength Designates the length of the id's root
     * @param SansVowels Dictates whether or not vowels are allowed
     * @param Amount The number of PIDs to be created
     */
    public UsedSetting(String Prefix, TokenType TokenType, String CharMap,
            int RootLength, boolean SansVowels, long Amount) {
        super(Prefix, TokenType, CharMap, RootLength, SansVowels);
        this.Amount = Amount;

    }

    /**
     * No-arg constructor used by Hibernate
     */
    public UsedSetting() {

    }

    public long getAmount() {
        return Amount;
    }

    public void setAmount(long Amount) {
        this.Amount = Amount;
    }

}
