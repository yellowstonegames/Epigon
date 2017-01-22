package squidpony.epigon.actions;

import java.util.TreeMap;
import squidpony.epigon.universe.Element;

/**
 * Represents an attempted attack.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class AttackAction implements Action {

    TreeMap<Element, Integer> elements = new TreeMap<>();//how much of each element in the group
    int power;
}
