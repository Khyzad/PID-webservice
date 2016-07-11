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
    private int id_;

    @NotNull
    @Column(name = "PID_PREFIX", nullable = false)
    private String prefix_;

    @Column(name = "TOKENTYPE")
    @Enumerated(EnumType.STRING)
    private Token tokenType_;

    @Column(name = "CHARMAP")
    private String charMap_;

    @Column(name = "ROOTLENGTH")
    private int rootLength_;

    @Column(name = "SANSVOWELS")
    private boolean sansVowels_;

    /**
     * Copy constructor.
     *
     * @param s the CachedSetting to copy
     */
    public Setting(Setting s) {
        prefix_ = s.getPrefix();
        tokenType_ = s.getTokenType();
        charMap_ = s.getCharMap();
        rootLength_ = s.getRootLength();
        sansVowels_ = s.isSansVowels();
    }

    /**
     * Constructor that represents the necessary values all settings should
     * contain
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
        this.prefix_ = Prefix;
        this.tokenType_ = TokenType;
        this.charMap_ = CharMap;
        this.rootLength_ = RootLength;
        this.sansVowels_ = SansVowels;
    }

    /**
     * Default constructor.
     */
    public Setting() {
    }

    // Typical getters and setters    
    public int getId() {
        return id_;
    }

    public void setId(int Id) {
        this.id_ = Id;
    }

    public String getPrefix() {
        return prefix_;
    }

    public void setPrefix(String Prefix) {
        this.prefix_ = Prefix;
    }

    public Token getTokenType() {
        return tokenType_;
    }

    public void setTokenType(Token TokenType) {
        this.tokenType_ = TokenType;
    }

    public String getCharMap() {
        return charMap_;
    }

    public void setCharMap(String CharMap) {
        this.charMap_ = CharMap;
    }

    public int getRootLength() {
        return rootLength_;
    }

    public void setRootLength(int RootLength) {
        this.rootLength_ = RootLength;
    }

    public boolean isSansVowels() {
        return sansVowels_;
    }

    public void setSansVowels(boolean SansVowels) {
        this.sansVowels_ = SansVowels;
    }

}
