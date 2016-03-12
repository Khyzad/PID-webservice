package com.hida.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;

/**
 *
 * @author lruffin
 */
@Entity
@Table(name = "USED_SETTING")
public class UsedSetting extends Setting{
    
    
    @NotNull
    @Column(name="AMOUNT")
    @Type(type="long")
    private long Amount;


    /**
     * Constructor; missing javadoc
     *
     * @param Prefix
     * @param TokenType
     * @param CharMap
     * @param RootLength
     * @param SansVowels
     * @param Amount
     */
    public UsedSetting(String Prefix, TokenType TokenType, String CharMap,
            int RootLength, boolean SansVowels, long Amount) {
        super(Prefix, TokenType, CharMap, RootLength, SansVowels);
        this.Amount = Amount;
                
    }

    /**
     * Default constructor
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
