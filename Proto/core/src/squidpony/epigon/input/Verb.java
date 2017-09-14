package squidpony.epigon.input;

/**
 * An enum that represents a class of actions that can be taken in the world.
 *
 * Tied to user input options.
 */
public enum Verb {
    ANOINT, // for powders, gels, liquids, or anything that can be smeared onto something (the actor is doing this to themselves, if targetting other then use throw verb action)
    CLOSE, // for doors, Interact should be for container manipulation
    CONSUME, // eat, drink, snort
    EXAMINE, // all five senses used to pay particular attention to an area
    FIRE, // Using an equipped weapon with ammo
    GET, // Get from tile currently occupied
    GET_AOE, // Get from all near tiles
    HELP, // manual, key bindings, hints n tips
    INTERACT, // Generic interaction with world environment, catch-all for anything not covered by specific verb basically
    INVENTORY,
    LOOK,
    MOVE_DOWN,
    MOVE_DOWN_LEFT,
    MOVE_DOWN_RIGHT,
    MOVE_HIGHER, // Up in 3D space, but "up" is "north" so "higher".... for North to always be actual North would be weird at the poles
    MOVE_LEFT,
    MOVE_RIGHT,
    MOVE_UP,
    MOVE_UP_LEFT,
    MOVE_UP_RIGHT,
    MOVE_LOWER, // Down in 3D space
    OPEN, // For doors only, interact should be used for containers
    QUIT,
    REST,
    SAVE,
    THROW,
    USE_ABILITY,
    WEAR, // includes armor, clothing, and jewelry
    WIELD // includes weaponse, magic items that must be held to work, shields

    // TODO - submenu interactions
}
