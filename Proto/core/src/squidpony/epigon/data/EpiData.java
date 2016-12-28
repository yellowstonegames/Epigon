package squidpony.epigon.data;

import squidpony.epigon.data.specific.Name;

/**
 * Data class for information shared by all data objects.
 */
public abstract class EpiData {

    public String internalName;
    public Name name;
    public String description;
    public String notes;

}
