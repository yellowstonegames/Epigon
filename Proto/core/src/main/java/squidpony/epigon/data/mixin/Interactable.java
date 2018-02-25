package squidpony.epigon.data.mixin;

import squidpony.epigon.data.generic.Modification;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds what to do when a Physical is interacted with. This allows for doors, stairs, levers,
 * and such in terrain as well as consumption of items.
 *
 * An interaction can cause permanent or temporary changes, both to itself and to surrounding things.
 */
public class Interactable {
    public String phrasing = "interact with"; // What to show the player when the interaction happens.

    public boolean bumpAction = false; // When true this action is what happens when a player bumps into it as default.
    public boolean consumes = false; // Whether this interaction uses up its Physical target

    public List<Modification> actorModifications = new ArrayList<>();
    public List<Modification> targetModifications = new ArrayList<>();

}
