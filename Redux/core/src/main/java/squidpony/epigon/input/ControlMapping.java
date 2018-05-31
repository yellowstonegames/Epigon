package squidpony.epigon.input;

import squidpony.Maker;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.UnorderedSet;

import static squidpony.epigon.input.Verb.*;
import static squidpony.squidgrid.gui.gdx.SquidInput.*;

/**
 * The set of keyboard inputs.
 */
public class ControlMapping {

    private static final int CAPS = 0x40000, CTRL = 0x20000;
    public static final OrderedMap<Integer, Verb> allMappings = OrderedMap.makeMap(
            (int) 'A' | CAPS, ATTEMPT,
            (int) 'c', CONSUME,
            (int) 'c' | CTRL, CONSUME_WEIRDLY, // ctrl-c to eat the delicious soap
            (int) 'e', EQUIPMENT,
            (int) 'f', FIRE,
            (int) 'G' | CAPS, GET,
            (int) 'g', GATHER,
            (int) '?' | CAPS, HELP,
            (int) F1, HELP,
            (int) 'i', INTERACT,
            (int) 'd', DRAW,
            (int) 'D' | CAPS, DROP,
            (int) 'r', REST,
            (int) 'p', USE_POWER,
            (int) 'v', VIEW,
            (int) 'x', EXAMINE,
            (int) UP_ARROW, MOVE_UP,
            (int) DOWN_ARROW, MOVE_DOWN,
            (int) LEFT_ARROW, MOVE_LEFT,
            (int) RIGHT_ARROW, MOVE_RIGHT,
            (int) UP_LEFT_ARROW, MOVE_UP_LEFT,
            (int) UP_RIGHT_ARROW, MOVE_UP_RIGHT,
            (int) DOWN_LEFT_ARROW, MOVE_DOWN_LEFT,
            (int) DOWN_RIGHT_ARROW, MOVE_DOWN_RIGHT,
            (int) PAGE_UP, MESSAGE_PRIOR,
            (int) PAGE_DOWN, MESSAGE_NEXT,
            (int) 'h', MOVE_LEFT,
            (int) 'j', MOVE_DOWN,
            (int) 'k', MOVE_UP,
            (int) 'l', MOVE_RIGHT,
            (int) 'y', MOVE_UP_LEFT,
            (int) 'u', MOVE_UP_RIGHT,
            (int) 'b', MOVE_DOWN_LEFT,
            (int) 'n', MOVE_DOWN_RIGHT,
            (int) '.', WAIT,
            (int) CENTER_ARROW, WAIT,
            (int) '<' | CAPS, MOVE_HIGHER,
            (int) '>' | CAPS, MOVE_LOWER,
            (int) '[', CONTEXT_PRIOR,
            (int) ']', CONTEXT_NEXT,
            (int) '{' | CAPS, INFO_PRIOR,
            (int) '}' | CAPS, INFO_NEXT,
            (int) 'o', OPEN,
            (int) 's', SHUT,
            (int) ' ', PAUSE,
            (int) 'S' | CAPS, SAVE,
            (int) 'S' | CTRL, SAVE,
            (int) 'T' | CAPS, TRY_AGAIN,
            (int) 't', TRY_AGAIN,
            (int) 'Q' | CAPS, QUIT,
            (int) 'q' | CTRL, QUIT,
            (int) ESCAPE, CLOSE_SCREEN
    );


    public static final UnorderedSet<Verb> defaultMapViewMapping = Maker.makeUOS(
            ATTEMPT,
            CONSUME,
            CONSUME_WEIRDLY, // ctrl-c to eat the delicious soap
            EQUIPMENT,
            FIRE,
            GET,
            GATHER,
            HELP,
            INTERACT,
            DRAW,
            DROP,
            REST,
            USE_POWER,
            VIEW,
            EXAMINE,
            MOVE_UP,
            MOVE_DOWN,
            MOVE_LEFT,
            MOVE_RIGHT,
            MOVE_UP_LEFT,
            MOVE_UP_RIGHT,
            MOVE_DOWN_LEFT,
            MOVE_DOWN_RIGHT,
            WAIT,
            MOVE_HIGHER,
            MOVE_LOWER,
            MESSAGE_PRIOR,
            MESSAGE_NEXT,
            CONTEXT_PRIOR,
            CONTEXT_NEXT,
            INFO_PRIOR,
            INFO_NEXT,
            OPEN,
            SHUT,
            SAVE,
            QUIT,
            CLOSE_SCREEN
    );

    public static final UnorderedSet<Verb> defaultEquipmentViewMapping = Maker.makeUOS(
            ATTEMPT,
            CONSUME,
            CONSUME_WEIRDLY, // ctrl-c to eat the delicious soap
            EQUIPMENT, // acts the same as closing the window while open
            FIRE,
            HELP,
            INTERACT,
            DRAW,
            DROP,
            EXAMINE,
            MOVE_UP,
            MOVE_DOWN,
            MOVE_LEFT,
            MOVE_RIGHT,
            MOVE_LEFT,
            MOVE_DOWN,
            MOVE_UP,
            MOVE_RIGHT,
            MOVE_HIGHER,
            MOVE_LOWER,
            MESSAGE_PRIOR,
            MESSAGE_NEXT,
            CONTEXT_PRIOR,
            CONTEXT_NEXT,
            INFO_PRIOR,
            INFO_NEXT,
            QUIT,
            CLOSE_SCREEN
    );
    public static final UnorderedSet<Verb> defaultHelpViewMapping = Maker.makeUOS(
            MOVE_UP,
            MOVE_DOWN,
            MOVE_LEFT,
            MOVE_RIGHT,
            MOVE_LEFT,
            MOVE_DOWN,
            MOVE_UP,
            MOVE_RIGHT,
            MESSAGE_PRIOR,
            MESSAGE_NEXT,
            CONTEXT_PRIOR,
            CONTEXT_NEXT,
            INFO_PRIOR,
            INFO_NEXT,
            EQUIPMENT,
            MOVE_HIGHER,
            MOVE_LOWER,
            QUIT,
            CLOSE_SCREEN
    );
    public static final UnorderedSet<Verb> defaultFallingViewMapping = Maker.makeUOS(
            MOVE_UP,
            MOVE_DOWN,
            MOVE_LEFT,
            MOVE_RIGHT,
            MOVE_LEFT,
            MOVE_DOWN,
            MOVE_UP,
            MOVE_RIGHT,
            PAUSE,
            SAVE,
            SAVE,
            QUIT
    );
    public static final UnorderedSet<Verb> defaultFallingViewGameOverMapping = Maker.makeUOS(
            TRY_AGAIN,
            QUIT
    );
}
