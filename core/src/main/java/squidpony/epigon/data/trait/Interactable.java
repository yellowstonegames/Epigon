package squidpony.epigon.data.trait;

import squidpony.epigon.Interaction;

/**
 * Holds what to do when a Physical is interacted with. This allows for doors, stairs, levers,
 * and such in terrain as well as consumption of items.
 *
 * An interaction can cause permanent or temporary changes, both to itself and to surrounding things.
 */
public class Interactable {
    public String phrasing; // What to show the player when the interaction happens.
    public boolean consumes; // Whether this interaction uses up its Physical target
    public boolean bumpAction; // When true this action is what happens when a player bumps into it as default.
    public Interaction interaction;
    public Interactable()
    {
        this("interact with", true, false, ((actor, target, level) -> "Nothing happens."));
    }
    public Interactable(String phrasing, boolean consumes, boolean bump, Interaction interaction)
    {
        this.phrasing = phrasing;
        this.consumes = consumes;
        bumpAction = bump;
        this.interaction = interaction;
    }
}
