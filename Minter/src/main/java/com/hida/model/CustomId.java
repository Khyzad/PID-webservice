/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hida.model;

import java.util.Arrays;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Created and used by CustomMinters
 *
 * @author lruffin
 */
@Entity
public class CustomId extends Pid {

    @Transient
    private String[] TokenMapArray;

    public CustomId() {

    }

    public CustomId(CustomId id) {
        super(id);
        this.TokenMapArray = Arrays.copyOf(id.getTokenMapArray(), id.getTokenMapArray().length);
    }

    public CustomId(String prefix, int[] baseMap, String[] tokenMapArray) {
        super(baseMap, prefix);
        this.TokenMapArray = Arrays.copyOf(tokenMapArray, tokenMapArray.length);
    }

    /**
     * Created and used by CustomMinters
     *
     * @return
     */
    @Override
    public boolean incrementId() {
        int range = this.getBaseMap().length - 1;

        boolean overflow = true;
        for (int k = 0; k < this.getBaseMap().length && overflow; k++) {
            // record value of current index
            int value = this.getBaseMap()[range - k];

            if (value == TokenMapArray[k].length() - 1) {
                this.getBaseMap()[range - k] = 0;
            }
            else {
                this.getBaseMap()[range - k]++;
                overflow = false;
            }
        }
        return !overflow;
    }

    /**
     * Converts the BaseMap into a String representation of this id's name.
     *
     * There is a one-to-one mapping of BaseMap, dependent on a given
     * DatabaseManager, to every possible name an Id can have.
     *
     * @return - the name of an Id.
     */
    @Override
    public String getRootName() {
        String charId = "";

        for (int i = 0; i < this.getBaseMap().length; i++) {
            charId += TokenMapArray[i].charAt(this.getBaseMap()[i]);
        }
        return charId;
    }

    @Override
    public String getName() {
        Name = Prefix + this.getRootName();
        return Name;
    }

    @Override
    public String toString() {
        return Prefix + this.getRootName();
    }

    /**
     * Retrieves the TokenMapArray assigned to this id
     *
     * @return The array assigned to this id.
     */
    public String[] getTokenMapArray() {
        return TokenMapArray;
    }

    /**
     * Sets the TokenMapArray to this id. Note that the the length of the array
     * must be of the same length id, otherwise index out of bounds exceptions
     * will be thrown.
     *
     * @param TokenMapArray The array to be assigned.
     */
    public void setTokenMapArray(String[] TokenMapArray) {
        this.TokenMapArray = TokenMapArray;
    }
}
