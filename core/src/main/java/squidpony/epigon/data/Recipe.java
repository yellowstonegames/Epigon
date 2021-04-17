package squidpony.epigon.data;

import squidpony.squidmath.OrderedMap;

/**
 * A specific in-game recipe which the player (or other creatures, including the Dungeon Master) may
 * acquire and use to create items.
 *
 * A specific recipe will always produce a specific result when successfully used, although the
 * quality of that result may be dependent on the skill level of the creator.
 */
public class Recipe extends EpiData {

    public OrderedMap<Physical, Integer> consumed = new OrderedMap<>();
    public OrderedMap<Physical, Integer> catalyst = new OrderedMap<>();
    public OrderedMap<Physical, Integer> result = new OrderedMap<>();
}
