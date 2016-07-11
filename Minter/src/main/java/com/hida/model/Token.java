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

/**
 * A group of objects that uniquely identifies a sequence of possible and valid
 * characters_ that can be found in the name of a Pid.
 *
 * @author lruffin
 */
public enum Token {

    DIGIT("0123456789"),
    LOWER_ALPHABET("abcdefghijklmnopqrstuvwxyz"),
    UPPER_ALPHABET("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    MIXED_ALPHABET("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    LOWER_ALPHABET_EXTENDED("0123456789abcdefghijklmnopqrstuvwxyz"),
    UPPER_ALPHABET_EXTENDED("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    MIXED_ALPHABET_EXTENDED("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    LOWER_CONSONANTS("bcdfghjklmnpqrstvwxz"),
    UPPER_CONSONANTS("BCDFGHJKLMNPQRSTVWXZ"),
    MIXED_CONSONANTS("bcdfghjklmnpqrstvwxzBCDFGHJKLMNPQRSTVWXZ"),
    LOWER_CONSONANTS_EXTENDED("0123456789bcdfghjklmnpqrstvwxz"),
    UPPER_CONSONANTS_EXTENDED("0123456789BCDFGHJKLMNPQRSTVWXZ"),
    MIXED_CONSONANTS_EXTENDED("0123456789bcdfghjklmnpqrstvwxzBCDFGHJKLMNPQRSTVWXZ");

    private final String characters_;

    Token(final String characters) {
        this.characters_ = characters;
    }

    public String getCharacters() {
        return characters_;
    }
}
