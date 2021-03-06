package squidpony.epigon.input;

/**
 * An enum that represents a class of actions that can be taken in the world.
 *
 * Tied to user input options.
 */
public enum Verb {
    ATTEMPT("Fiddle with an item or environment feature to try to figure out its function; luck-based, may backfire. \"What does this lever do?\""),
    CONSUME("Eat, drink, snort. Also includes rubbing oils, powders, and gels on yourself."),
    CONSUME_WEIRDLY("For items that may have different effects on different parts of the body. This is an option for"
        + " powders, gels, and oils where CONSUME applies them to your skin. Use this command if you want to eat the body butter like it's actual butter."),
    CONTEXT_PRIOR("Cycles the context area (lower right box on the Main View) one display state prior."),
    CONTEXT_NEXT("Cycles the context area (lower right box on the Main View) to the next display state."),
    WIELD("In map view, randomly wields a weapon from the inventory. In inventory view, manually picks an object to equip."),
    DROP("In map view, drops the items you currently hold in random adjacent squares. In inventory view, drops the selected item in your square."),
    EXAMINE("All five senses used to pay particular attention to an area; may trigger traps if you don't find them, which will not happen from just \"View\""), // If VIEW is changed this last bit needs to be changed as well
    FIRE("Using an equipped weapon with ammo."),
    GATHER("Get from all near tiles."),
    GET("Get from tile currently occupied."),
    HELP("Opens up the manual, key binding reference, and 'hints and tips.'"),
    INFO_PRIOR("Cycles the info area (upper right box on the Main View) one display state prior."),
    INFO_NEXT("Cycles the info area (upper right box on the Main View) to the next display state."),
    INTERACT("Generic interaction with world environment and items; may have a context menu to select a specific action."),
    EQUIPMENT("Open a screen to see your items and equip or use them."),
    MESSAGE_PRIOR("Cycles the message area (lower left box on the Main View) to show one earlier message, if any."),
    MESSAGE_NEXT("Cycles the message area (lower left box on the Main View) to show one later message, if any."),
    MOVE_DOWN,
    MOVE_DOWN_LEFT,
    MOVE_DOWN_RIGHT,
    MOVE_HIGHER("Up in 3D space; head-wards. Cycles to the previous page when in UI mode."),
    MOVE_LEFT,
    MOVE_RIGHT,
    MOVE_UP,
    MOVE_UP_LEFT,
    MOVE_UP_RIGHT,
    MOVE_LOWER("Down in 3D space; foot-wards. Cycles to the next page when in UI mode."),
    OPEN("For doors only, \"Interact\" should be used for containers."), // If INTERACT is changed this also needs to change
    PAUSE("Hold it a minute!"),
    QUIT("Leave the game."),
    TRY_AGAIN("Start the game again after ending."),
    REST("Let a turn pass without doing anything."), // Does this allow more healing than WAIT?
    SAVE("Save the game."),
    SHUT("For doors, \"Interact\" should be for container manipulation."), // If INTERACT is changed this also needs to change
    //THROW("If you have an item equipped, throw it at a target, otherwise grab a specific item or creature and attempt to throw it at a target."),
    USE_POWER("Use one of your available powers."),
    VIEW("Look at something without touching it; gives less info but is less risky and can be done at a distance."),
    WAIT("Stay in place and do nothing."),
    CLOSE_SCREEN("Closes the UI screen mode and returns to the previous mode.");

    public String name;
    public String description;

    private Verb() {
        name = name().toLowerCase().replace('_', ' ');
        description = " ... ";
    }

    private Verb(String description) {
        this();
        this.description = description;
    }

    /**
     * Returns true if this verb is an action that should cause the turn clock to advance when it is taken.
     *
     * @return
     */
    public boolean isAction() {

        switch (this) {
            case SHUT:
            case OPEN:
                return true;
            default:
                return false;
        }
    }
}
