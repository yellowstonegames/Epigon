package squidpony.data.blueprints;

import java.util.ArrayList;
import java.util.TreeMap;
import squidpony.data.EpiData;

/**
 * Holds for constructing a Dungeon out of rooms and terrain. A dungeon is any
 * group of areas that are thematically related in some way, it is not
 * restricted to underground lairs and such.
 *
 * The mappings are a list of objects that could be used, along with the odds
 * (out of 100) that they will be used.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class DungeonBlueprint extends EpiData {

    TreeMap<Character, TreeMap<ItemBlueprint, Integer>> mappings;
    ArrayList<RoomBlueprint> rooms;
}
