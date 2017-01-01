package squidpony.epigon.data.loot;

import java.util.List;
import java.util.stream.Collectors;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprints.PhysicalBlueprint;
import squidpony.squidmath.RNG;

/**
 * A loot table that contains weighted random entries.
 */
public class LootTable extends EpiData {

    public List<LootTableEntry> entries;

    public PhysicalBlueprint roll(RNG rng) {
        int totalWeight = entries.stream().collect(Collectors.summingInt(e -> e.weight));
        int choice = rng.between(1, totalWeight);
        EpiData winner = null;

        for (LootTableEntry entry : entries) {
            choice -= entry.weight;
            if (choice <= 0) {
                winner = entry.entry;
                break;
            }
        }

        if (winner instanceof LootTable) {
            return ((LootTable) winner).roll(rng);
        }

        return (PhysicalBlueprint)winner;
    }

    public boolean contains(PhysicalBlueprint item){
        for (LootTableEntry entry : entries) {
            if (entry.entry instanceof LootTable) {
                if (((LootTable)entry.entry).contains(item)) {
                    return true;
                }
            } else {
                if (entry.entry.equals(item)) {
                    return true;
                }
            }
        }

        // None found
        return false;
    }
}
