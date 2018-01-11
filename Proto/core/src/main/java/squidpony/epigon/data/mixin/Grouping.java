package squidpony.epigon.data.mixin;

/**
 * Handles multiple identical things grouped together temporarily.
 *
 * May or may not be physically attached. Just in close proximity and treatable as a stack of things
 * counts as well if all members are functionally identical.
 *
 * Examples include bundles of arrows, pools of oil, sheets of paper
 */
public class Grouping {

    // TODO - add naming convention information

    public int quantity; // All quantities should be integers, if small amounts are desired than small units should be used

    public Grouping()
    {
    }

    public Grouping(int amount)
    {
        quantity = amount;
    }
}
