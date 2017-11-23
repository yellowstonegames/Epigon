package squidpony.epigon.data;

import squidpony.squidmath.OrderedMap;

import java.util.*;

/**
 * Holds all the loaded game data.
 */
public class DataMaster {

    private final Set<EpiData> known = new HashSet<>(); // All the game objects
    private final Set<EpiData> resolved = new HashSet<>(); // The items that have been fully resolved to their references
    private final Set<EpiData> unresolved = new HashSet<>(); // The items that have some partial unresolved references
    private final Map<String, Set<String>> desired = new HashMap<>(); // The names of items keyed with the list of references needed to fulfill

    private final OrderedMap<EpiData, Set<EpiData>> children = new OrderedMap<>(); // All the known children of the provided object

    {
        children.defaultReturnValue(Collections.EMPTY_SET);
    }

    public void add(EpiData data) {
        boolean added = known.add(data);

        if (!added) {
            throw new IllegalArgumentException("Element already exists in known data set: " + data.name);
        }

        // TODO - check data for type and then attempt to link everything relevant
    }

    public Set<EpiData> getKnown(){
        return known;
    }

    public Set<EpiData> children(EpiData parent) {
        Set<EpiData> found;
        Set<EpiData> returning = new HashSet<>();

        found = children.get(parent);

        while (found != null && !found.isEmpty()) {
            found.removeAll(returning);
            returning.addAll(found);
            found = found.stream().map(e -> children.get(e)).collect(HashSet::new, Set::addAll, Set::addAll);
        }

        return returning;
    }
}
