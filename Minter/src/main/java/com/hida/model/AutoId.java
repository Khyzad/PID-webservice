package com.hida.model;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 *
 * @author lruffin
 */
@Entity
public class AutoId extends Pid {

    @Transient
    private String TokenMap;

    public AutoId() {

    }

    public AutoId(AutoId id) {
        super(id);
        this.TokenMap = id.getTokenMap();
    }

    public AutoId(String prefix, int[] baseMap, String tokenMap) {
        super(baseMap, prefix);
        this.TokenMap = tokenMap;
    }

    /**
     * missing javadoc
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

            if (value == TokenMap.length() - 1) {
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
     * missing javadoc
     *
     * @return
     */
    @Override
    public String getRootName() {
        String charId = "";
        for (int i = 0; i < this.getBaseMap().length; i++) {
            charId += TokenMap.charAt(this.getBaseMap()[i]);
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

    // getters and setters
    public String getTokenMap() {
        return TokenMap;
    }

    public void setTokenMap(String TokenMap) {
        this.TokenMap = TokenMap;
    }

}
