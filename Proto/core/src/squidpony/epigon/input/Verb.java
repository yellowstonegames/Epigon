package squidpony.epigon.input;

/**
 * An enum that represents a class of actions that can be taken in the world.
 *
 * Tied to user input options.
 */
public enum Verb {
    //maybe remove ANOINT and make ointments used with CONSUME or used strangely (i.e. eating the poultice) with CONSUME_DIFFERENTLY
    //ANOINT, // for powders, gels, liquids, or anything that can be smeared onto something (the actor is doing this to themselves, if targeting other then use throw verb action)
    ATTEMPT, // Fiddle with an item or environment feature to try to figure out its function; luck-based, may backfire. "What does this button do?"
    CONSUME, // eat, drink, snort
    CONSUME_DIFFERENTLY, // for items that may have different effects on different parts of the body; this probably should be an option for
                         // powders/gels, with CONSUME being normal rubbing-on-skin, if you want to eat the body butter like it's actual butter
    EQUIP, // for picking up an object and immediately wielding/using it, instead of putting it in your inventory to use from there
    EXAMINE, // all five senses used to pay particular attention to an area; may trigger traps if you don't find them, unlike LOOK
    FIRE, // Using an equipped weapon with ammo
    GATHER, // Get from all near tiles
    GET, // Get from tile currently occupied
    HELP, // manual, key bindings, hints and tips
    INTERACT, // Generic interaction with world environment, catch-all for anything not covered by specific verb basically
    INVENTORY, // open a screen to see your items and equip or use them
    MOVE_DOWN,
    MOVE_DOWN_LEFT,
    MOVE_DOWN_RIGHT,
    MOVE_HIGHER, // Up in 3D space, but "up" is "north" so "higher"; head-wards
    MOVE_LEFT,
    MOVE_RIGHT,
    MOVE_UP,
    MOVE_UP_LEFT,
    MOVE_UP_RIGHT,
    MOVE_LOWER, // Down in 3D space; foot-wards
    OPEN, // For doors only, interact should be used for containers
    QUIT,
    REST,
    SAVE,
    SHUT, // for doors, Interact should be for container manipulation
    THROW, // if you have an item equipped, throw it at a target, otherwise grab a specific item or creature and attempt to throw it at a target
    USE_ABILITY,
    VIEW, // look at something without touching it; gives less info but is less risky and can be done at a distance
    WAIT, // stay in place and do nothing
    // next two covered by either EQUIP or INVENTORY
    //WEAR, // includes armor, clothing, and jewelry
    //WIELD // includes weapons, magic items that must be held to work, shields

    // TODO - submenu interactions
}
