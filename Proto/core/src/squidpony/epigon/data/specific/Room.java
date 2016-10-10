package squidpony.epigon.data.specific;

import java.util.TreeMap;
import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprints.RoomBlueprint;

/**
 * A specific in-game room.
 *
 * This consists of a thematically created room controlled by it's parent
 * dungeon. The Room is responsible only for initial layout in the dungeon and
 * may be used multiple times to create copies during dungeon creation.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Room extends EpiData{

    public RoomBlueprint parent;
    public TreeMap<Character, Item> features;
}
