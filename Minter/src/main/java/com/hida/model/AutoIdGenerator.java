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
 * An Id Generator that creates Pids. For each Pid, their entire name will be
 * uniformly determined by the possible characters provided by a single Token.
 *
 *
 * @author lruffin
 */
public class AutoIdGenerator extends IdGenerator {

    /**
     * Designates what characters are contained in the id's root name.
     */
    private Token tokenType_;

    /**
     * Designates the length of the id's root.
     */
    private int rootLength_;

    /**
     * Default constructor. Aside from Token, there are no restrictions placed
     * on the parameters and can be used however one sees fit.
     *
     * @param prefix A sequence of characters that appear in the beginning of
     * PIDs
     * @param tokenType An enum used to configure PIDS
     * @param rootLength Designates the length of the id's root
     */
    public AutoIdGenerator(String prefix, Token tokenType, int rootLength) {
        super(prefix);
        this.tokenType_ = tokenType;
        this.rootLength_ = rootLength;
        this.maxPermutation_ = getMaxPermutation();
    }

    /**
     * This method calculates and returns the total possible number of
     * permutations using the values given in the constructor.
     *
     * @return number of permutations
     */
    @Override
    final public long getMaxPermutation() {
        // get the base of each character
        int base = tokenType_.getCharacters().length();

        // raise it to the power of how ever long the rootLength is
        return ((long) Math.pow(base, rootLength_));
    }

    /**
     * Increments a value of a PID. If the maximum limit is reached the values
     * will wrap around.
     *
     * @param pid The pid to increment
     */
    @Override
    public void incrementPid(Pid pid) {
        long next = (this.PidToLong(pid) + 1) % this.maxPermutation_;
        pid.setName(this.longToName(next));
    }

    /**
     * Translates an ordinal number into a sequence of characters with a radix
     * based on tokenType_.
     *
     * @param ordinal The nth position of a permutation
     * @return The sequence at the nth position
     */
    @Override
    protected String longToName(long ordinal) {
        StringBuilder name = new StringBuilder("");
        String map = tokenType_.getCharacters();
        int radix = map.length();
        int fullNameLength = rootLength_ + prefix_.length();

        long remainder = ordinal;
        for (int i = fullNameLength - 1; i >= prefix_.length(); i--) {
            name.insert(0, map.charAt((int) (remainder % radix)));

            remainder /= radix;
        }

        return prefix_ + name.toString();
    }

    /**
     * Translates a Pid into an ordinal number.
     *
     * @param pid A persistent identifier
     * @return The ordinal number of the Pid
     */
    @Override
    protected long PidToLong(Pid pid) {
        String name = pid.getName();
        String map = tokenType_.getCharacters();
        int radix = map.length();
        int fullNameLength = rootLength_ + prefix_.length();

        long ordinal = 0;
        for (int i = prefix_.length(); i < fullNameLength; i++) {
            int mapIndex = map.indexOf(name.charAt(i));

            ordinal += (long) Math.pow(radix, name.length() - i - 1) * mapIndex;
        }

        return ordinal;
    }

    /* getters and setters */
    public Token getTokenType() {
        return tokenType_;
    }

    public void setTokenType(Token TokenType) {
        this.tokenType_ = TokenType;
    }

    public int getRootLength() {
        return rootLength_;
    }

    public void setRootLength(int RootLength) {
        this.rootLength_ = RootLength;
    }

}
