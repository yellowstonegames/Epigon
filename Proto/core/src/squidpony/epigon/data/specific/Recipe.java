package squidpony.epigon.data.specific;

import squidpony.squidmath.OrderedMap;

import squidpony.epigon.data.EpiData;

/**
 * A specific in-game recipe which the player (or other creatures, including the Dungeon Master) may
 * aquire and use to create items.
 *
 * A specific recipe will always produce a specific result when successfully used, although the
 * quality of that result may be dependant on the skill level of the creator.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Recipe extends EpiData {

    public OrderedMap<Physical, Integer> consumed = new OrderedMap<>();
    public OrderedMap<Physical, Integer> catalyst = new OrderedMap<>();
    public OrderedMap<Physical, Integer> result = new OrderedMap<>();
}
