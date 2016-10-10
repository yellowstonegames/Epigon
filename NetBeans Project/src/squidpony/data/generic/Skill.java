package squidpony.data.generic;

import squidpony.data.EpiData;
import squidpony.squidcolor.SColor;

/**
 * List of the possible skills.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class Skill extends EpiData {

    private Skill() {//For serialization purposes
    }

    public Skill(Skill parent, String internalName, String name, String plural, String description, String notes, SColor color) {
        setParent(parent);
        setInternalName(internalName);
        setDataName(name);
        setPlural(plural);
        setDescription(description);
        setNotes(notes);
        setColor(color);
    }
}
