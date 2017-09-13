package squidpony.epigon.input;

/**
 * An enum that represents a class of actions that can be taken in the world.
 *
 * Tied to user input options.
 */
public enum Verb {
    WEAR, // includes armor, clothing, and jewelry
    WIELD, // includes weaponse, magic items that must be held to work, shields
    ANOINT, // for powders, gels, liquids, or anything that can be smeared onto something (the actor is doing this to themselves, if targetting other then use throw verb action)
    CONSUME, // eat, drink, snort
    EXAMINE, // all five senses used to pay particular attention to an area
    USE_SKILL,
    USE_ABILITY,
    MOVE_UP,
    MOVE_UP_RIGHT,
    MOVE_RIGHT,
    MOVE_DOWN_RIGHT,
    MOVE_DOWN,
    MOVE_DOWN_LEFT,
    MOVE_LEFT,
    MOVE_UP_LEFT,
    // Moving vertically should be based on interacting with a stairs object
    REST,
    THROW,
    FIRE, // Using an equipped weapon with ammo
    GET, // Get from tile currently occupied
    GET_AOE, // Get from all near tiles
    INVENTORY,
    // TODO - submenu interactions

    SAVE,
    QUIT,
    LOOK,
    HELP // manual, key bindings, hints n tips
}
