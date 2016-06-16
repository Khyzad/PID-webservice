package com.hida.model;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A Pid (persistent identifier) is an object that is used to hold a unique
 * sequence of characters to reference other objects in a given domain.
 *
 * @author lruffin
 */
@Entity
@Table(name = "PIDS")
public class Pid implements Comparable<Pid> {

    @Id
    @Column(name = "NAME", updatable = false, nullable = false)
    private String name_;

    /**
     * A no-arg constructor to be used by Hibernate
     */
    public Pid() {
    }

    public Pid(String Name) {
        this.name_ = Name;
    }

    /**
     * Copy constructor; primarily used to copy values of the BaseMap from one
     * Id to another.
     *
     * @param id The Id to copy from.
     */
    public Pid(Pid id) {
        this.name_ = id.name_;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pid other = (Pid) obj;
        return Objects.equals(this.name_, other.name_);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.name_);
        return hash;
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
        String name1 = this.name_;
        String name2 = t.name_;
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
                if ((Character.isDigit(c1) && Character.isLetter(c2))
                        || (Character.isLowerCase(c1) && Character.isUpperCase(c2))) {
                    return -1;
                }
                else if ((Character.isLetter(c1) && Character.isDigit(c2))
                        || (Character.isUpperCase(c1) && Character.isLowerCase(c2))) {
                    return 1;
                }
                else if (c1 < c2) {
                    return -1;
                }
                else if (c1 > c2) {
                    return 1;
                }
            }
            return 0;
        }
    }

    /**
     * Updates this Pid's name by replacing a character located at index i with
     * character c.
     *
     * @param i index
     * @param c character
     */
    public void replace(int i, char c) {
        String newName = "";

        for (int j = 0; j < name_.length(); j++) {
            if (j == i) {
                newName += c;
            }
            else {
                newName += name_.charAt(j);
            }
        }

        name_ = newName;
    }

    @Override
    public String toString() {
        return this.name_;
    }

    /* getters and setters */
    public String getName() {
        return name_;
    }

    public void setName(String Name) {
        this.name_ = Name;
    }
}
