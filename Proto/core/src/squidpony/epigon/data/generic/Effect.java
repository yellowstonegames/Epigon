package squidpony.epigon.data.generic;

import java.util.List;

/**
 * Describes what series of things happens to create a change in the world
 */
public class Effect {

    public Formula successFormula;

    public List<Modification> sourceModifications;
    public List<Modification> targetModifications;

    public List<Effect> followUpOnSuccess;
    public List<Effect> followUpOnFailure;

}
