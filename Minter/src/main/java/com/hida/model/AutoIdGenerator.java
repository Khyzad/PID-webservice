package com.hida.model;

import static com.hida.model.IdGenerator.Rng;
import static com.hida.model.IdGenerator.LOGGER;
import java.util.Set;
import java.util.TreeSet;

/**
 * An Id Generator that creates Pids. For each Pid, their entire name will be
 uniformly determined by the possible characters provided by a single
 Token.
 *
 *
 * @author lruffin
 */
public class AutoIdGenerator extends IdGenerator {

    /**
     * Designates what characters are contained in the id's root name.
     */
    private Token TokenType;

    /**
     * Designates the length of the id's root.
     */
    private int RootLength;

    /**
     * Default constructor. Aside from Token, there are no restrictions
 placed on the parameters and can be used however one sees fit.
     *
     * @param prefix A sequence of characters that appear in the beginning of
     * PIDs
     * @param tokenType An enum used to configure PIDS
     * @param rootLength Designates the length of the id's root
     */
    public AutoIdGenerator(String prefix, Token tokenType, int rootLength) {
        super(prefix);
        this.TokenType = tokenType;
        this.RootLength = rootLength;
    }

    /**
     * Creates Pids without regard to a natural order.
     *
     * @param amount The number of PIDs to be created
     * @return A set of Pids
     */
    @Override
    public Set<Pid> randomMint(long amount) {
        // checks to see if its possible to produce or add requested amount of
        long total = calculatePermutations();
        if (total < amount) {
            throw new NotEnoughPermutationsException(total, amount);
        }

        // store sequence of characters provided by Token
        String map = TokenType.getCharacters();

        // create a set to contain Pids
        Set<Pid> pidSet = new TreeSet<>();

        for (int i = 0; i < amount; i++) {
            // create a name based off the token and the randomly generated numbers
            String name = Prefix;
            for (int j = 0; j < RootLength; j++) {
                int randomNumber = Rng.nextInt(map.length());
                name += map.charAt(randomNumber);
            }

            // create pid and add it to the set
            Pid pid = new Pid(name);
            while (!pidSet.add(pid)) {
                this.incrementPid(pid);
            }

            LOGGER.trace("Generated Auto Random ID: {}", pid);
        }

        return pidSet;
    }

    /**
     * Creates Pids in ascending order
     *
     * @param amount The number of PIDs to be created
     * @return A set of Pids
     */
    @Override
    public Set<Pid> sequentialMint(long amount) {
        // checks to see if its possible to produce or add requested amount of
        long total = calculatePermutations();
        if (total < amount) {
            throw new NotEnoughPermutationsException(total, amount);
        }

        // create a set to contain Pids
        Set<Pid> pidSet = new TreeSet<>();

        // create a base Pid using the first character of the Token 
        char firstChar = TokenType.getCharacters().charAt(0);
        String baseName = String.format("%0" + RootLength + "d", 0).replace('0', firstChar);
        Pid basePid = new Pid(Prefix + baseName);

        for (int i = 0; i < amount; i++) {

            // copy the Name of basePid into a new Pid instance
            Pid pid = new Pid(basePid);

            // add the pid to the set
            pidSet.add(pid);

            // increment the base Pid
            this.incrementPid(basePid);
            LOGGER.trace("Generated Custom Sequential ID: {}", pid);
        }

        return pidSet;
    }

    /**
     * This method calculates and returns the total possible number of
     * permutations using the values given in the constructor.
     *
     * @return number of permutations
     */
    @Override
    public long calculatePermutations() {
        // get the base of each character
        int base = TokenType.getCharacters().length();

        // raise it to the power of how ever long the rootLength is
        return ((long) Math.pow(base, RootLength));
    }

    /**
     * Increments a value of a PID. If the maximum limit is reached the values
     * will wrap around.
     *
     * @param pid The pid to increment
     */
    @Override
    public void incrementPid(Pid pid) {
        String name = pid.getName();
        String map = TokenType.getCharacters();
        int lastIndex = name.length() - 1;
        int prefixLength = Prefix.length();
        char lastChar = map.charAt(map.length() - 1);
        char firstChar = map.charAt(0);
        boolean overflow = true;

        // continue until overflow is false or last value of the rootName is reached
        for (int i = 0; i <= lastIndex - prefixLength && overflow; i++) {
            int offset = lastIndex - i;
            char c = name.charAt(offset);

            if (c == lastChar) {
                pid.replace(offset, firstChar);
            }
            else {
                int position = map.indexOf(c);
                char nextChar = map.charAt(position + 1);

                pid.replace(offset, nextChar);
                overflow = false;
            }
        }
    }

    /* getters and setters */
    public Token getTokenType() {
        return TokenType;
    }

    public void setTokenType(Token TokenType) {
        this.TokenType = TokenType;
    }

    public int getRootLength() {
        return RootLength;
    }

    public void setRootLength(int RootLength) {
        this.RootLength = RootLength;
    }
}
