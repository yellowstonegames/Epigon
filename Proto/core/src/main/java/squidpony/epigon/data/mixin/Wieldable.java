package squidpony.epigon.data.mixin;

import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.universe.Element;

import java.util.ArrayList;
import java.util.List;

import static squidpony.epigon.Epigon.chaos;

/**
 * The specific information for a wieldable object.
 *
 * @author Eben
 */
public class Wieldable {

    public Wieldable parent;
    public List<ConditionBlueprint> causes = new ArrayList<>(); // conditions imparted by a successful hit

    public List<Modification> changes = new ArrayList<>();

    public int hitChance;
    public int damage;
    public int reachDistance;
    public List<Element> elements = new ArrayList<>();
    public static final Wieldable UNARMED = new Wieldable();
    static {
        UNARMED.reachDistance = 0;
        UNARMED.hitChance = 95;
        UNARMED.damage = 1;
        UNARMED.elements.add(Element.BLUNT);
    }
    public Element rollElement()
    {
        int r = chaos.nextIntHasty(elements.size());
        return elements.get(r);
    }
}
