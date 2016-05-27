package com.hida.model;

import static com.hida.model.IdGenerator.Rng;
import static com.hida.model.IdGenerator.LOGGER;
import java.util.Set;
import java.util.TreeSet;

/**
 * An Id Generator that creates Pids primarily based on tokenType and
 * rootLength.
 *
 * @author lruffin
 */
public class AutoIdGenerator extends IdGenerator {

    /**
     * Designates what characters are contained in the id's root name.
     */
    private TokenType TokenType;

    /**
     * Designates the length of the id's root.
     */
    private int RootLength;

    /**
     * Default constructor. Aside from TokenType, there are no restrictions
     * placed on the parameters and can be used however one sees fit.
     *
     * @param prefix A sequence of characters that appear in the beginning of
     * PIDs
     * @param tokenType An enum used to configure PIDS
     * @param rootLength Designates the length of the id's root
     */
    public AutoIdGenerator(String prefix, TokenType tokenType, int rootLength) {
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

        // generate ids
        String map = TokenType.getCharacters();
        Set<Pid> pidSet = new TreeSet<>();

        for (int i = 0; i < amount; i++) {
            int[] tempIdBaseMap = new int[RootLength];
            for (int j = 0; j < RootLength; j++) {
                tempIdBaseMap[j] = Rng.nextInt(map.length());
            }
            Pid currentId = new Pid(tempIdBaseMap, Prefix);
            this.assignName(currentId);
            
            
            // increment the Pid until a unique value has been found
            while (!pidSet.add(currentId)) {                
                this.incrementPid(currentId);
            }
            LOGGER.trace("Generated Auto Random ID: {}", currentId);
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

        // declare an empty array to start from and a set to hold all Pids
        Set<Pid> pidSet = new TreeSet<>();
        int[] previousIdBaseMap = new int[RootLength];
        
        // generate Pids
        Pid currentId = new Pid(previousIdBaseMap, Prefix);
        this.assignName(currentId);
        for (int i = 0; i < amount; i++) {
            Pid nextId = new Pid(currentId);
            pidSet.add(currentId);
            LOGGER.trace("Generated Auto Sequential ID: {}", currentId);            
            this.incrementPid(nextId);
            currentId = new Pid(nextId);
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
        int tokenMapRange = TokenType.getCharacters().length() - 1;
        int baseMapRange = pid.getBaseMap().length - 1;
        boolean overflow = true;       
        
        // increment the values in a pid's basemap
        for (int k = 0; k <= baseMapRange && overflow; k++) {
            // record value of current index
            int value = pid.getBaseMap()[baseMapRange - k];

            // if the last value is reached then wrap around
            if (value == tokenMapRange) {
                pid.getBaseMap()[baseMapRange - k] = 0;
            }
            // otherwise increment the value at the current index and break the loop
            else {
                pid.getBaseMap()[baseMapRange - k]++;
                overflow = false;
            }
        }

        // assign a new name to the Pid based on its base map
        assignName(pid);
    }

    /**
     * Creates and sets a new name for a pid based on its indices contained in
     * the BaseMap and the characters in the TokenType. This should be called
     * whenever the values in a Pid's BaseMap has been changed.
     *
     * @param pid The pid that needs a new name.
     */
    @Override
    protected void assignName(Pid pid) {
        String Name = "";
        for (int i = 0; i < pid.getBaseMap().length; i++) {
            Name += TokenType.getCharacters().charAt(pid.getBaseMap()[i]);
        }
        pid.setName(this.getPrefix() + Name);
    }

    /* getters and setters */
    public TokenType getTokenType() {
        return TokenType;
    }

    public void setTokenType(TokenType TokenType) {
        this.TokenType = TokenType;
    }

    public int getRootLength() {
        return RootLength;
    }

    public void setRootLength(int RootLength) {
        this.RootLength = RootLength;
    }
}
