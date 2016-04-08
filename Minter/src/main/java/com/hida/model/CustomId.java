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

    public CustomId() {

    }

    public CustomId(CustomId id) {
        super(id);
        this.TokenMapArray = Arrays.copyOf(id.getTokenMapArray(), id.getTokenMapArray().length);
        this.Name = id.getPrefix() + getRootName();
    }

    public CustomId(String prefix, int[] baseMap, String[] tokenMapArray) {
        super(baseMap, prefix);
        this.TokenMapArray = Arrays.copyOf(tokenMapArray, tokenMapArray.length);
        this.Name = prefix + getRootName();
    }

    /**
     * Created and used by CustomMinters
     *
     * @return
     */
    @Override
    public boolean incrementId() {
        boolean overflow = true;
        
        int lastIndex = BaseMap.length - 1;     
        for (int i = lastIndex; overflow && i >= 0; i--) {
                if(BaseMap[i] == TokenMapArray[i].length() - 1){                         
                    BaseMap[i] = 0;
                }else{
                    
                    BaseMap[i]++;
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
    protected final String getRootName() {
        String charId = "";

        for (int i = 0; i < this.getBaseMap().length; i++) {
            charId += TokenMapArray[i].charAt(this.getBaseMap()[i]);
        }
        return charId;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public String toString() {
        return Name;
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
