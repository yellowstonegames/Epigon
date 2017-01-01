package squidpony.epigon.data;

import squidpony.epigon.data.specific.Name;

/**
 * Data class for information shared by all data objects.
 *
 * Objects of this type that contain only an internalName are meant to be placeholders for later
 * run-time attachment to the full definitions. This allows for JSON-compatible string lookup for
 * connecting data and run-time direct connections for efficiency without requiring a serialized
 * data only class.
 */
public abstract class EpiData {

    public String internalName;
    public Name name;
    public String description;
    public String notes;

}
