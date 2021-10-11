package Data;

//This interface represents a Property - which is a building block of a solution.

public interface Property extends Cloneable  {

    /**
     * Find out weather this property is blank of does it contain data.
     *
     * @return true iff the property is blank
     */
    public boolean isBlank();

    /**
     * Components assemble each property.
     * Get the ID of a certain component in the property.
     *
     * @param place the serial placement of the component in the property.
     *
     * @return the ID of the wanted component. or -1 if doesnt exist.
     */
    public int getComponent(int place);

    /**
     * Components assemble each property.
     * Get the ID of a certain component in the property.
     *
     * @param ComponentName the name of the component in the property.
     *
     * @return the ID of the wanted component. or -1 if doesnt exist.
     */
    public int getComponent(String ComponentName);

    /**
     * Get the highest ID of a certain component in the property.
     *
     * @param ComponentName the name of the component in the property.
     *
     * @return the highest valid ID of the wanted component. or -1 if doesnt exist.
     */
    public int getComponentLimit(String ComponentName);

    /**
     * Randomly changes the ID of a component in the property
     *
     * @param ComponentName the name of the component in the property.
     */
    public void randomlyChangeComponent(String ComponentName) throws Exception;

    /**
     * Compare two properties
     *
     * @param me the object of the property.
     * @param other the object of the property to compare to
     * @param ComponentIDs array of component IDs to compare by, organized left to right by priority.
     *
     * @return 1 if me > other. -1 if me < other. 0 if me = other.
     */
    public static int CompareProps(Property me, Property other, int... ComponentIDs)
    {
        for(int i=0; i<ComponentIDs.length; i++)
        {
            int componentID = ComponentIDs[i];
            if(me.getComponent(componentID) > other.getComponent(componentID))
                return 1;
            else if (me.getComponent(componentID) < other.getComponent(componentID))
                return -1;
        }
        return 0;
    }

    public Object clone();
}
