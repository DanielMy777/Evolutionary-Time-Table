package Data;

//This interface represents the definition for a problem to be solved by an evolutionary algorithm

import java.util.Collection;

public interface Problem<T extends Solution>{

    /**
     * get the fitness of the best solution currently in the population.
     *
     * @return the fitness as a double
     */
    public double findFitnessOfBestSolution() throws Exception;

    /**
     * get the best solution currently in the population.
     *
     * @return the Solution you requested
     */
    public Solution sortAndFindBestSolution();

    /**
     * Sends every solution in the population to fitness evaluation and updates it.
     */
    public void evaluateAllPopulation();

    /**
     * Activate a round of the evolutionary algorithm, creating the next generation. Updates current population.
     */
    public void createNextGeneration();

    /**
     * Generates the initial population for the evolutionary algorithm. Updates current population.
     *
     * @param fact A 'SolutionFactory' object that has methods of generating new Solutions of type T
     */
    public void generateInitialPopulation(SolutionFactory<T> fact);
}
