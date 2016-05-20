package com.hida.model;

/**
 * An Enum created to hold define the tokens used by the Minter class. Enums are
 * inherently serializable and, as such, do not need to implement Serializable.
 * All Enums will have the have a serialVersionUID of 0L and will therefore not
 * need to be specified.
 *
 * @author lruffin
 */
public enum TokenType {

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

    private final String characters;

    TokenType(final String characters) {
        this.characters = characters;
    }

    public String getCharacters() {
        return characters;
    }
}
