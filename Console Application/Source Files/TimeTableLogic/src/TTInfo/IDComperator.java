package TTInfo;

import java.util.Comparator;

//This class can compare 2 objects that have IDs by their ID

public class IDComperator implements Comparator<HasID> {
    @Override
    public int compare(HasID o1, HasID o2)
    {
        return o1.getId() - o2.getId();
    }
}
