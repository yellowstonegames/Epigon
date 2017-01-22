package squidpony.epigon.data.blueprints;

import java.util.Map;
import java.util.Set;

import squidpony.epigon.data.EpiData;
import squidpony.squidmath.OrderedMap;

/**
 * Holds for constructing a Dungeon out of rooms and terrain. A dungeon is any group of areas that
 * are thematically related in some way, it is not restricted to underground lairs and such.
 *
 * The mappings are a list of objects that could be used, along with the odds (out of 100) that they
 * will be used.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class DungeonBlueprint extends EpiData {

    Map<Character, OrderedMap<PhysicalBlueprint, Integer>> mappings;
    Set<RoomBlueprint> rooms;
}
