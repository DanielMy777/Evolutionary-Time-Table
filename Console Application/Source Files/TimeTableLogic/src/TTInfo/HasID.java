package TTInfo;

import java.io.Serializable;
import java.util.Objects;

//This class represents an object that has an ID (serializable)

abstract public class HasID implements Serializable {
    public final int id;

    public HasID(int id)
    {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HasID hasID = (HasID) o;
        return id == hasID.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
