package squidpony.epigon.data;

import squidpony.StringKit;
import squidpony.squidmath.PintRNG;

/**
 * Data class for information shared by all data objects.
 */
public abstract class EpiData {

    public String name;
    public String description;
    //public String notes; // NOTE - don't need to copy this into created objects <-- no way to eliminate the field

    private String id;
    private final int idHash;
    
    public static final PintRNG uniqueIntGen = new PintRNG(12345);

    static {
    }

    public EpiData() {
        idHash = uniqueIntGen.nextInt();
    }

    @Override
    public String toString() {
        return name == null ? id == null ? (id = "EpiData_" + StringKit.hex(idHash)) : id : name;
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
        return idHash == other.idHash;
    }

}
