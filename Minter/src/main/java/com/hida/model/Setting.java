package com.hida.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;

/**
 * missing javadoc
 *
 * @author lruffin
 */
@MappedSuperclass
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Size(min = 0, max = 10)
    @Column(name = "PID_PREFIX", nullable = false)
    @Type(type = "java.lang.String")
    private String Prefix;

    @Column(name = "TOKENTYPE")
    @Type(type = "com.hida.model.TokenType")
    private TokenType TokenType;

    @Column(name = "CHARMAP")
    @Type(type = "java.lang.String")
    private String CharMap;

    @Type(type = "short")
    @Column(name = "ROOTLENGTH")
    private int RootLength;

    @NotNull
    @Column(name = "SANSVOWELS")
    @Type(type = "boolean")
    private boolean SansVowels;

    /**
     * Copy constructor.
     *
     * @param s the CachedSetting to copy
     */
    public Setting(Setting s) {
        Prefix = s.getPrefix();
        TokenType = s.getTokenType();
        CharMap = s.getCharMap();
        RootLength = s.getRootLength();
        SansVowels = s.isSansVowels();
    }

    /**
     * Constructor; missing javadoc
     *
     * @param Prefix
     * @param TokenType
     * @param CharMap
     * @param RootLength
     * @param SansVowels
     */
    public Setting(String Prefix, TokenType TokenType, String CharMap, int RootLength,
            boolean SansVowels) {
        this.Prefix = Prefix;
        this.TokenType = TokenType;
        this.CharMap = CharMap;
        this.RootLength = RootLength;
        this.SansVowels = SansVowels;
    }

    /**
     * Default constructor.
     */
    public Setting() {
    }

    // Typical getters and setters    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrefix() {
        return Prefix;
    }

    public void setPrefix(String Prefix) {
        this.Prefix = Prefix;
    }

    public TokenType getTokenType() {
        return TokenType;
    }

    public void setTokenType(TokenType TokenType) {
        this.TokenType = TokenType;
    }

    public String getCharMap() {
        return CharMap;
    }

    public void setCharMap(String CharMap) {
        this.CharMap = CharMap;
    }

    public int getRootLength() {
        return RootLength;
    }

    public void setRootLength(int RootLength) {
        this.RootLength = RootLength;
    }

    public boolean isSansVowels() {
        return SansVowels;
    }

    public void setSansVowels(boolean SansVowels) {
        this.SansVowels = SansVowels;
    }

}
