package squidpony.epigon.input;

import squidpony.Maker;
import squidpony.squidmath.OrderedMap;

import static squidpony.squidgrid.gui.gdx.SquidInput.*;

/**
 * The set of keyboard inputs.
 */
public class ControlMapping {

    private static final int CAPS = 0x40000, CTRL = 0x20000;
    public static final OrderedMap<Integer, Verb> defaultMapping =
            Maker.<Integer, Verb>makeOM( // ah yes, ugly syntax to ensure the type is right.
                'A'|CAPS, Verb.ATTEMPT,
                'c', Verb.CONSUME,
                'c'|CTRL, Verb.CONSUME_DIFFERENTLY, // ctrl-c to eat the delicious soap
                'e', Verb.EQUIP,
                'f', Verb.FIRE,
                'g', Verb.GET,
                'G'|CAPS, Verb.GATHER,
                'h', Verb.HELP,
                '?'|CAPS, Verb.HELP,
                F1, Verb.HELP,
                'i', Verb.INTERACT,
                'I'|CAPS, Verb.INVENTORY,
                'r', Verb.REST,
                'u', Verb.USE_ABILITY,
                'v', Verb.VIEW,
                'x', Verb.EXAMINE,

                UP_ARROW, Verb.MOVE_UP,
                DOWN_ARROW, Verb.MOVE_DOWN,
                LEFT_ARROW, Verb.MOVE_LEFT,
                RIGHT_ARROW, Verb.MOVE_RIGHT,
                UP_LEFT_ARROW, Verb.MOVE_UP_LEFT,
                UP_RIGHT_ARROW, Verb.MOVE_RIGHT,
                DOWN_LEFT_ARROW, Verb.MOVE_DOWN_LEFT,
                DOWN_RIGHT_ARROW, Verb.MOVE_DOWN_RIGHT,
                'w', Verb.WAIT,
                '>'|CAPS, Verb.MOVE_LOWER,
                '<'|CAPS, Verb.MOVE_HIGHER,
                'o', Verb.OPEN,
                's', Verb.SHUT,
                'S'|CAPS, Verb.SAVE,
                'Q'|CAPS, Verb.QUIT,
                'q'|CTRL, Verb.QUIT,
                ESCAPE, Verb.QUIT
        );
}
