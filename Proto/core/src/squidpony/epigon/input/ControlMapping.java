package squidpony.epigon.input;

import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidmath.OrderedMap;

/**
 * The set of keyboard inputs.
 */
public class ControlMapping {

    public static final OrderedMap<Character, Verb> defaultMapping;

    static {
        defaultMapping = new OrderedMap<>();
        defaultMapping.put('a', Verb.ANOINT);
        defaultMapping.put('c', Verb.CLOSE);
        defaultMapping.put('C', Verb.CONSUME);
        defaultMapping.put('x', Verb.EXAMINE);
        defaultMapping.put('f', Verb.FIRE);
        defaultMapping.put('g', Verb.GET);
        defaultMapping.put('G', Verb.GET_AOE);
        defaultMapping.put('h', Verb.HELP);
        defaultMapping.put('?', Verb.HELP);
        defaultMapping.put(SquidInput.F1, Verb.HELP);
        defaultMapping.put('i', Verb.INTERACT);
        defaultMapping.put('I', Verb.INVENTORY);
        defaultMapping.put('l', Verb.LOOK);
        defaultMapping.put(SquidInput.DOWN_ARROW, Verb.MOVE_DOWN);
        defaultMapping.put(SquidInput.DOWN_LEFT_ARROW, Verb.MOVE_DOWN_LEFT);
        defaultMapping.put(SquidInput.DOWN_RIGHT_ARROW, Verb.MOVE_DOWN_RIGHT);
        defaultMapping.put('>', Verb.MOVE_HIGHER);
        defaultMapping.put('w', Verb.WEAR);
        defaultMapping.put('W', Verb.WIELD);
    }

}
