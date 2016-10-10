package squidpony.messaging;

import java.awt.Point;
import squidpony.compiler.intermediate.data.ElementTemplate;
import squidpony.compiler.intermediate.data.PhysicalObjectTemplate;
import squidpony.data.generic.Skill;
import squidpony.data.Stat;

/**
 *
 * @author Eben
 */
public class ActionResultDetail {
    public PhysicalObjectTemplate source;
    public PhysicalObjectTemplate target;

    public int amount;//whatever happened, this is how much it happened
    public Point direction;//if it was movment, how much x,y movement
    public Stat stat;//if a stat was changed, which one
    public Skill skill;//if a skill was changed, which one
    //public Perk perk;//if a perk was added/removed, which one//TODO -- implement when Perks are added
    public ElementTemplate element;//if an element was involved, which one
}
