package Database;

import javafx.util.Pair;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

//This class represents a "Data Transfer Object" containing pairs of <Label, Data>.

public class DTO implements Serializable {
    private Map<String, Object> Data;


    /**
     * constructor for the DTO
     *
     * @param items an array of pairs organized <Label, Data> to be held in the DTO.
     */
    public DTO(Pair<String,Object>... items)
    {
        Data = new HashMap<>();
        for (int i = 0; i < items.length; i++)
        {
                Data.put(items[i].getKey(), items[i].getValue());
        }
    }

    /**
     * get a specific piece of data held in the DTO
     *
     * @param name the label of the data piece you would like to get.
     *
     * @return the data piece you  requested as an Object.
     */
    public Object getData(String name)
    {
        if(Data == null)
            return null;
        return Data.get(name);
    }
}
