package squidpony.data.specific;

import java.util.ArrayList;
import squidpony.data.blueprints.ItemBlueprint;
import squidpony.data.interfaces.*;
import squidpony.data.interfaces.Readable;

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
