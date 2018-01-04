package squidpony.epigon.data.mixin;

import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.universe.Element;
import squidpony.squidmath.ProbabilityTable;

import java.util.ArrayList;
import java.util.List;

/**
 * The specific information for a wieldable object.
 *
 * @author Eben
 */
public class Wieldable {

    public Wieldable parent;
    public List<ConditionBlueprint> causes = new ArrayList<>(); // conditions imparted by a successful hit

    public List<Modification> changes = new ArrayList<>();

    public double hitChance;
    public int damage;
    public int range;
    public ProbabilityTable<Element> elements = new ProbabilityTable<>();
    public static final Wieldable UNARMED = new Wieldable();
    static {
        UNARMED.range = 0;
        UNARMED.hitChance = 0.7;
        UNARMED.damage = 1;
        UNARMED.elements.add(Element.BLUNT, 1);
    }
    public Element rollElement()
    {
        return elements.random();
    }
}
