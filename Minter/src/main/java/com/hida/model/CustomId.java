package com.hida.model;

import java.util.Arrays;
import javax.persistence.Entity;
import javax.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created and used by CustomMinters
 *
 * @author lruffin
 */
@Entity
public class CustomId extends Pid {

    protected static final Logger Logger = LoggerFactory.getLogger(CustomId.class);

    @Transient
    private String[] TokenMapArray;

    /**
     * No-arg constructor used by Hibernate
     */
    public CustomId() {

    }

    /**
     * Copy constructor
     *
     * @param id An object with values to copy
     */
    public CustomId(CustomId id) {
        super(id);
        this.TokenMapArray = Arrays.copyOf(id.getTokenMapArray(), id.getTokenMapArray().length);
    }

    /**
     * Recommended constructor used to create new Pids
     *
     * @param prefix A sequence of characters that appear in the beginning of
     * PIDs
     * @param baseMap An array of integers that contain the indices described by
     * tokenMap
     * @param tokenMapArray An array of a sequence of characters all possible
     * characters a PID can contain
     */
    public CustomId(String prefix, int[] baseMap, String[] tokenMapArray) {
        super(baseMap, prefix);
        this.TokenMapArray = Arrays.copyOf(tokenMapArray, tokenMapArray.length);
    }

    /**
     * Increments a value of a PID. If the maximum limit is reached the values
     * will wrap around.
     *
     * @return true if the id has been successfully incremented
     */
    @Override
    public boolean incrementId() {
        boolean overflow = true;

        int lastIndex = BaseMap.length - 1;
        for (int i = lastIndex; overflow && i >= 0; i--) {
            if (BaseMap[i] == TokenMapArray[i].length() - 1) {
                BaseMap[i] = 0;
            }
            else {

                BaseMap[i]++;
                overflow = false;
            }
        }

        return !overflow;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Creates the name of the id based on the indices contained in the BaseMap
     * and the characters in the TokenMap
     *
     * @return The Name of the Pid
     */
    @Override
    public String getName() {
        Name = "";

        for (int i = 0; i < this.getBaseMap().length; i++) {
            Name += TokenMapArray[i].charAt(this.getBaseMap()[i]);
        }
        return this.getPrefix() + Name;
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
