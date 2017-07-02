package squidpony.epigon.data;

import java.util.Objects;
import java.util.UUID;

/**
 * Data class for information shared by all data objects.
 */
public abstract class EpiData {

    public String name;
    public String description;
    public String notes;

    private final String id;

    public EpiData(){
        id = UUID.randomUUID().toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(id);
        return hash;
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
        if (!Objects.equals(id, other.id)) {
            return false;
        }
        return true;
    }

}
