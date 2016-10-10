package squidpony.epigon.data.specific;

import java.awt.Container;
import java.util.ArrayList;
import squidpony.epigon.data.blueprints.ItemBlueprint;
import squidpony.epigon.data.interfaces.Ammunition;
import squidpony.epigon.data.interfaces.Interactable;
import squidpony.epigon.data.interfaces.Liquid;
import squidpony.epigon.data.interfaces.Readable;
import squidpony.epigon.data.interfaces.Wearable;
import squidpony.epigon.data.interfaces.Wieldable;
import squidpony.epigon.data.interfaces.Zappable;

/**
 * Represents a specific object in the game world.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Item extends Physical {
    public ItemBlueprint parent;
    public ArrayList<Item> createdFrom;//only important items should track this
    public int fragility, maxCondition, currentCondition;
    public boolean large;
    //
    //possible interfaces
    public Ammunition ammunitionData = null;
    public Container containerData = null;
    public Interactable interactableData = null;
    public Liquid liquidData = null;
    public Readable readableData = null;
    public Wearable wearableData = null;
    public Wieldable wieldableData = null;
    public Zappable zappableData = null;

    boolean hasParent(ItemBlueprint blueprint) {
        return parent.hasParent(blueprint);
    }
    
}
