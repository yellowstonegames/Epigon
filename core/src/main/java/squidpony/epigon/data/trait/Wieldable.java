package squidpony.epigon.data.trait;

import squidpony.epigon.data.Modification;
import squidpony.epigon.data.quality.Element;
import squidpony.squidmath.ProbabilityTable;

import java.util.ArrayList;
import java.util.List;

/**
 * An object that can be wielded, typically a weapon.
 */
public class Wieldable {
    public Wieldable parent;

    public List<Modification> changesWhenWielded = new ArrayList<>();
}
