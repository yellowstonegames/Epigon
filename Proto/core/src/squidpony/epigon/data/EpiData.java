package squidpony.epigon.data;

import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Data class for information shared by all data objects.
 *
 * Because all object might have their properties inherited from a parent object
 * and such properties should be updated when the parent is update, requests for
 * any property that does not have a value is passed to the parent. If there is
 * no parent with the property defined (or no parent defined) then a null is
 * returned.
 *
 * The isFooDefined() methods return true if the property is not passed to the
 * object's parent.
 *
 * @author Eben Howard - http://squidpony.com
 */
public abstract class EpiData implements Comparable<EpiData>, Cloneable {

    public String internalName;
    public char symbol;
    public String name;
    public String plural;
    public String description;
    public String notes;
    public SColor color;
    public int rgb;

    public EpiData() {
    }

    public EpiData(String internalName, String name, String plural, String description, String notes, SColor color) {
        this.internalName = internalName;
        this.name = name;
        this.plural = plural;
        this.description = description;
        this.notes = notes;
        this.color = color;
    }

    @Override
    public String toString() {
        return "Internal Name: " + internalName + "\nParent: "
                + "\nName: " + name + "\nPlural: " + plural + "\nDescriptions: " + description + "\nNotes: " + notes;
    }

    @Override
    public EpiData clone() {
        try {
            return (EpiData) super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    @Override
    public int compareTo(EpiData other) {
        return internalName.compareTo(other.internalName);
    }
}
