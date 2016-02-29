package com.hida.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;

/**
 * missing javadoc
 *
 * @author lruffin
 */
@Entity
@Table(name = "USED_SETTING")
public class UsedSetting extends Setting {

    @NotNull    
    @Column(name = "AMOUNT", nullable = false)
    @Type(type = "long")
    private long Amount;
    
    /**
     * Default constructor
     */
    public UsedSetting() {

    }

    /**
     * missing javadoc
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
    public UsedSetting(String Prepend, String Prefix, TokenType TokenType, String CharMap,
            int RootLength, boolean SansVowels, long RequestedAmount) {
        super(Prefix, TokenType, CharMap, RootLength, SansVowels);
        this.Amount = RequestedAmount;
        
    }
    
    

}
