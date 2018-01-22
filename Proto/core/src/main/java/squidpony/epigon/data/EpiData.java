package squidpony.epigon.data;

import squidpony.squidmath.SquidID;

/**
 * Data class for information shared by all data objects.
 */
public abstract class EpiData {

    public String name;
    public String description;
    //public String notes; // NOTE - don't need to copy this into created objects <-- no way to eliminate the field

    private final String id;
    private final int idHash;

    static {
        SquidID.stabilize();
    }

    public EpiData() {
        final SquidID sid = SquidID.randomUUID();
        id = sid.toString();
        idHash = sid.hashCode();
    }

    @Override
    public String toString() {
        return name == null ? "EpiData_" + id : name;
    }

    @Override
    public int hashCode() {
        return idHash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EpiData other = (EpiData) obj;
        return id.equals(other.id);
    }

}
