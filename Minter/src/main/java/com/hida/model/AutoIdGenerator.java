package com.hida.model;

import static com.hida.model.IdGenerator.Rng;
import static com.hida.model.IdGenerator.LOGGER;
import java.util.Set;
import java.util.TreeSet;

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
    private Token TokenType;

    /**
     * Designates the length of the id's root.
     */
    private int RootLength;

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
        this.TokenType = tokenType;
        this.RootLength = rootLength;
        this.MaxPermutation = calculatePermutations();
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

        // create a set to contain Pids
        Set<Pid> pidSet = new TreeSet<>();

        // randomly generate pids using a random number generator
        for (int i = 0; i < amount; i++) {
            long value = Math.abs(Rng.nextLong()) % total;
            Pid pid = this.longToPid(value);

            // create pid and add it to the set
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

        long ordinal = 0;
        Pid basePid = this.longToPid(ordinal);
        for (int i = 0; i < amount; i++) {

            // copy the Name of basePid into a new Pid instance
            Pid pid = new Pid(basePid.getName());

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
    final public long calculatePermutations() {
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
        long next = (this.PidToLong(pid) + 1) % this.MaxPermutation;       
        pid.setName(this.longToPid(next).getName());       
    }

    /**
     * Translates an ordinal number into a Pid.
     *
     * @param ordinal The nth position of a permutation
     * @return The Pid at the nth position
     */
    @Override
    protected Pid longToPid(long ordinal) {
        StringBuilder name = new StringBuilder("");
        String map = TokenType.getCharacters();
        int radix = map.length();
        int fullNameLength = RootLength + Prefix.length();

        long remainder = ordinal;
        for (int i = fullNameLength - 1; i >= Prefix.length(); i--) {
            name.insert(0, map.charAt((int) remainder % radix));

            remainder /= radix;
        }

        return new Pid(Prefix + name.toString());
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
        String map = TokenType.getCharacters();
        int radix = map.length();
        int fullNameLength = RootLength + Prefix.length();

        long ordinal = 0;
        for (int i = Prefix.length(); i < fullNameLength; i++) {
            int mapIndex = map.indexOf(name.charAt(i));

            ordinal += (long) Math.pow(radix, name.length() - i - 1) * mapIndex;
        }

        return ordinal;
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
