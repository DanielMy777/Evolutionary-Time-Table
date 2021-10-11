package Data;

import java.util.ArrayList;
import java.util.Collection;

//This interface represents a valid solution to the evolutionary algorithm problem

public interface Solution extends Comparable{

    /**
     * Get the up to date fitness of the solution
     *
     * @return The fitness score this solution has gotten due to its last evaluation.
     */
    public double getFitness() throws Exception;

    /**
     * Evaluate the fitness of a solution considering a collection of rules.
     *
     * @param rules the rules to check.
     *
     * @return The fitness score this solution has over these rule.
     */
    public double evaluateFitness(Collection<Rule> rules);

    /**
     * Get the solution as an array of properties building it.
     *
     * @return The spoken Property array.
     */
    public ArrayList<Property> getSolutionAsPropertyArray();

    /**
     * Randomly change a given property in the solution
     *
     * @param propertyIDX the index of the property in the solution (index in the property array)
     * @param ComponentName the component to randomly change.
     */
    public void randomlyChangeProperty(int propertyIDX, String ComponentName) throws Exception;

    /**
     * Adds a random property to the solution
     *
     * @return true iff a property was added.
     */
    public boolean addRandomProperty();

    /**
     * Removes a random property to the solution
     *
     * @return true iff a property was removed.
     */
    public boolean removeRandomProperty();

    /**
     * NOT IN USE - for personal purposes reasons only
     * Prints the solution as a property array.
     */
    public void printSolution(Object... params);

    @Override
    int compareTo(Object o);

}


