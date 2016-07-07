/*
 * Copyright 2016 Lawrence Ruffin, Leland Lopez, Brittany Cruz, Stephen Anspach
 *
 * Developed in collaboration with the Hawaii State Digital Archives.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.hida.model;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

/**
 * An object that references a list of values that are used to create a Pid.
 *
 * @author lruffin
 */
@MappedSuperclass
public class Setting {

    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    @NotNull
    @Column(name = "PID_PREFIX", nullable = false)
    private String Prefix;

    @Column(name = "TOKENTYPE")
    @Enumerated(EnumType.STRING)
    private Token TokenType;

    @Column(name = "CHARMAP")
    private String CharMap;

    @Column(name = "ROOTLENGTH")
    private int RootLength;

    @Column(name = "SANSVOWELS")
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
     * Constructor that represents the necessary values all settings should contain
     *
     * @param Prefix A sequence of characters that appear in the beginning of
     * PIDs
     * @param TokenType An enum used to configure PIDS
     * @param CharMap A sequence of characters used to configure PIDs
     * @param RootLength Designates the length of the id's root
     * @param SansVowels Dictates whether or not vowels are allowed
     */
    public Setting(String Prefix, Token TokenType, String CharMap, int RootLength,
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
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getPrefix() {
        return Prefix;
    }

    public void setPrefix(String Prefix) {
        this.Prefix = Prefix;
    }

    public Token getTokenType() {
        return TokenType;
    }

    public void setTokenType(Token TokenType) {
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
