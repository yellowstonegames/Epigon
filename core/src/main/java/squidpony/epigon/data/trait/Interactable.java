package squidpony.epigon.data.trait;

/**
 * Holds what to do when a Physical is interacted with. This allows for doors, stairs, levers,
 * and such in terrain as well as consumption of items.
 *
 * An interaction can cause permanent or temporary changes, both to itself and to surrounding things.
 */
public class Interactable {
    /**
     * The word or words for this kind of interaction, like "eat" or "apply directly to the forehead".
     */
    public String verb;
    /**
     * Whether this interaction uses up its Physical target.
     */
    public boolean consumes;
    /**
     * When true this action is what happens when a player bumps into it as default.
     */
    public boolean bumpAction;
    /**
     * Should probably be a lambda! A simple default is:
     * {@code (actor, target, level) -> "Nothing happens."}
     */
    public Interaction interaction;
    public Interactable()
    {
        this("interact with", false, false, (actor, target, level) -> "Nothing happens.");
    }
    public Interactable(String verb, boolean consumes, boolean bump, Interaction interaction)
    {
        this.verb = verb;
        this.consumes = consumes;
        bumpAction = bump;
        this.interaction = interaction;
    }
}
