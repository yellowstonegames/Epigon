package squidpony.epigon.data.generic;

import squidpony.epigon.data.EpiData;

/**
 * List of the possible skills such as Woodchopping, Archery, etc.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Skill extends EpiData {

    public Skill parent;

    public Skill(String name) {
        this.name = name;
    }

    public Skill(String name, Skill parent) {
        this.name = name;
        this.parent = parent;
    }
}
