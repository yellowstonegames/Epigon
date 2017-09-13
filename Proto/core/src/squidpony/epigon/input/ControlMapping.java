package squidpony.epigon.input;

import squidpony.squidmath.OrderedMap;

/**
 * The set of keyboard inputs.
 */
public class ControlMapping {

    public static final OrderedMap<Verb, Character> defaultMapping;

    static {
        defaultMapping = new OrderedMap<>();
    }

}
