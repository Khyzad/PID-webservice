package com.hida.model;

import java.util.Arrays;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * An object used to model every Pid. By definition, each Pid will have a unique
 * name associated with it. However, to determine uniqueness, each newly created
 * Pid must be compared to previously existing Ids.
 *
 * Comparisons will be made by using Sets collection. Depending on which set is
 * used, the Comparable interface and an overridden equals and hashCode methods
 * were overridden to accommodate.
 *
 * @author lruffin
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "PIDS")
public abstract class Pid implements Comparable<Pid> {

    @Id
    @Column(name = "NAME", updatable = false, nullable = false)
    protected String Name;

    @Transient
    protected int[] BaseMap;

    @Transient
    protected String Prefix;

    /**
     * A no-arg constructor to be used by Hibernate
     */
    public Pid() {
    }

    /**
     * Copy constructor; primarily used to copy values of the BaseMap from one
     * Id to another.
     *
     * @param id The Id to copy from.
     */
    public Pid(Pid id) {
        this.Prefix = id.Prefix;
        this.BaseMap = Arrays.copyOf(id.getBaseMap(), id.getBaseMap().length);
    }

    /**
     * Recommended, default constructor
     *
     * @param baseMap An array of integers that contain the indices described by
     * tokenMap
     * @param Prefix A sequence of characters that appear in the beginning of
     * PIDs
     */
    public Pid(int[] baseMap, String Prefix) {
        this.BaseMap = Arrays.copyOf(baseMap, baseMap.length);
        this.Prefix = Prefix;
    }        

    public abstract boolean incrementId();

    @Override
    public int hashCode() {
        // arbitrarily chosen prime numbers
        final int prime1 = 37;
        final int prime2 = 257;

        int hash = prime1 * prime2 + Arrays.hashCode(this.BaseMap);
        return hash;
    }

    /**
     * Overridden so that id's can be identified solely by its baseName.
     *
     * @param obj the Object this id is being compared to
     * @return true if the two Objects are the same.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Pid)) {
            return false;
        }
        final Pid paramId = (Pid) obj;

        return Arrays.equals(this.BaseMap, paramId.BaseMap);
    }

    /**
     * Used to define the natural ordering of how id's should be listed. When
     * invoked, the two id's will be compared by their Names.
     *
     * @param t second Pid being compared.
     * @return ordering is as follows: [0-9] < [a-z] < [A-Z] 
     */
    @Override
    public int compareTo(Pid t) {
        String name1 = this.Name;
        String name2 = t.Name;
        if (name1.length() < name2.length()) {
                return -1;
            }
            else if (name1.length() > name2.length()) {
                return 1;
            }
            else {
                for (int i = 0; i < name1.length(); i++) {
                    char c1 = name1.charAt(i);
                    char c2 = name2.charAt(i);
                    if (Character.isDigit(c1) && Character.isLetter(c2)
                            || Character.isLowerCase(c1) && Character.isUpperCase(c2)
                            || c1 < c2) {
                        return -1;
                    }
                    else if ((Character.isLetter(c1) && Character.isDigit(c2))
                            || Character.isUpperCase(c1) && Character.isLowerCase(c2)
                            || c1 > c2) {
                        return 1;
                    }
                }
                return 0;
            }        
    }

    /* getters and setters */
    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * The BaseMap is the numerical representation of this Pid's name. Used in
     * conjunction with TokenMap to return the string representation of this
     * id's name in its toString method.
     *
     * @return The array used to create the name
     */
    public int[] getBaseMap() {
        return BaseMap;
    }

    /**
     * The BaseMap is the numerical representation of this Pid's name. Used in
     * conjunction with TokenMap to return the string representation of this
     * id's name in its toString method.
     *
     * Be warned that the array must have a unique address for it to work with
     * Sets. The length of the array must be equal to TokenMap, otherwise an
     * IndexOutOfBounds error will be thrown in the getRootName method.
     *
     * @param baseMap The new array to replace the name.
     */
    public void setBaseMap(int[] baseMap) {
        this.BaseMap = baseMap;
    }      
    
    public String getPrefix() {
        return Prefix;
    }

    public void setPrefix(String Prefix) {
        this.Prefix = Prefix;
    }
}
