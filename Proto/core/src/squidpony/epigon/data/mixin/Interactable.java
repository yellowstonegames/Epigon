package squidpony.epigon.data.mixin;

import java.util.ArrayList;
import java.util.List;

import squidpony.epigon.actions.Action;

/**
 * Holds what to do when a bit of terrain is interacted with. This allows for doors, stairs, levers,
 * and such
 *
 * An interaction can cause permanent or temporary changes, both to itself and to surrounding things.
 */
public class Interactable {
    public String phrasing = "interact with"; // What to show the player when the interaction happens.

    public boolean bumpAction = false; // When true this action is what happens when a player bumps into it as default.

    public List<Action> actions = new ArrayList<>();
}
