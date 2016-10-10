package squidpony.data.specific;

import java.util.TreeMap;
import squidpony.data.EpiData;
import squidpony.data.blueprints.RoomBlueprint;

/**
 * A specific in-game room.
 *
 * This consists of a thematically created room controlled by it's parent
 * dungeon. The Room is responsible only for initial layout in the dungeon and
 * may be used multiple times to create copies during dungeon creation.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class Room extends EpiData{

    public RoomBlueprint parent;
    public TreeMap<Character, Item> features;
}
