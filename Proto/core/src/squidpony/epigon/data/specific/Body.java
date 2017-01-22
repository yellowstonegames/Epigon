package squidpony.epigon.data.specific;

import java.util.List;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprints.PhysicalBlueprint;
import squidpony.squidmath.OrderedMap;

/**
 * This class represents a specific creature's body.
 *
 * The condition of each part is tracked, along with the clothing, jewelry, and equipped items.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Body extends EpiData {

    public OrderedMap<BodyPart, List<Physical>> clothingSlots;
    public OrderedMap<BodyPart, List<Physical>> jewelrySlots;
    public OrderedMap<BodyPart, List<Physical>> equipSlots;
    public OrderedMap<BodyPart, Integer> condition; // current health for each part

    public int quantityOf(PhysicalBlueprint blueprint) {
        return (int) (clothingSlots.values().stream().flatMap(m -> m.parallelStream())
            .filter(p -> p.hasParent(blueprint))
            .count()
            + jewelrySlots.values().stream().flatMap(m -> m.parallelStream())
                .filter(p -> p.hasParent(blueprint))
                .count()
            + equipSlots.values().stream().flatMap(m -> m.parallelStream())
                .filter(p -> p.hasParent(blueprint))
                .count());
    }

    public boolean has(PhysicalBlueprint blueprint) {
        return clothingSlots.values().stream().flatMap(m -> m.parallelStream())
            .anyMatch(p -> p.hasParent(blueprint))
            || jewelrySlots.values().stream().flatMap(m -> m.parallelStream())
                .anyMatch(p -> p.hasParent(blueprint))
            || equipSlots.values().stream().flatMap(m -> m.parallelStream())
                .anyMatch(p -> p.hasParent(blueprint));
    }
}
