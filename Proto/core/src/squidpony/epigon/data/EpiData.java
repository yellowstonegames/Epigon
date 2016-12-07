package squidpony.epigon.data;

import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Data class for information shared by all data objects.
 */
public abstract class EpiData implements Comparable<EpiData> {

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
    public int compareTo(EpiData other) {
        return internalName.compareTo(other.internalName);
    }
}
