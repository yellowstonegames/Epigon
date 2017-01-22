package squidpony.epigon.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Holds all the loaded game data.
 */
public class DataMaster {

    private final Set<EpiData> known = new HashSet<>(); // All the game objects
    private final Set<EpiData> resolved = new HashSet<>(); // The items that have been fully resolved to their references
    private final Set<EpiData> unresolved = new HashSet<>(); // The items that have some partial unresolved references
    private final Map<String, Set<String>> desired = new HashMap<>(); // The names of items keyed with the list of references needed to fufill

    public void add(EpiData data) {
        boolean added = known.add(data);

        if (!added) {
            throw new IllegalArgumentException("Element already exists in known data set: " + data.name);
        }

        // TODO - check data for type and then attempt to link everything relevant
    }
}
