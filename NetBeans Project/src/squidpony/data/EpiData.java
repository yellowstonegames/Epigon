package squidpony.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import squidpony.squidcolor.SColor;

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
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")//resolve object dependancies by refering to same object
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")//record class to deserialize polymorphic objects
@JsonAutoDetect(getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)//don't use getters, but do use fields. this is to enable null values passing to parents in the getters
public abstract class EpiData implements Comparable<EpiData>, Cloneable {

    public String internalName;
    public char symbol;
    public String name;
    public String plural;
    public String description;
    public String notes;
    @JsonIgnore
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
