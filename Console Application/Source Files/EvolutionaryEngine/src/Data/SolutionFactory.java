package Data;

import java.util.ArrayList;

//This interface represents a factory - an object that can generate objects of type T

public interface SolutionFactory<T extends  Solution> {

    /**
     * Generates a random solution of type T
     *
     * @return The spoken Solution
     */
    public T generateSolution();

    /**
     * Generates a solution of type T, built by a given collection of properties.
     *
     * @param props An arrayList of properties to build from.
     *
     * @return The spoken Solution
     */
    public T createSolution(ArrayList<Property> props);

    /**
     * Generates a random Property for a solution of type T
     *
     * @return The spoken Property
     */
    public Property generateProperty();
}
