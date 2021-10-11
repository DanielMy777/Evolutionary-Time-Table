package Database;

import Data.Problem;
import Data.Rule;
import Data.Solution;
import Data.SolutionFactory;
import Management.EEException;
import Proporties.Crossover;
import Proporties.Mutation;
import Proporties.Selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

//This class represents a definition of a problem - to be solved by an evolutionary algorithm.

public class EvoAlgorithem<T extends Solution> implements Problem<T> {

    private int initialPopulation;
    private SolutionFactory<T> factory;
    private List<Solution> Population;
    private Collection<Rule> allRules;
    private Crossover crossoverMethod;
    private Selection selectionMethod;
    private List<Mutation> mutationMethods;

    public EvoAlgorithem(Collection<Rule> Rules) throws EEException {
        if(!EvoLoader.databaseIsLoaded)
            throw new EEException("XML error - Please load the data from XML before starting the algorithem");
        initialPopulation = EvoLoader.initialPopulation;
        Population = new ArrayList<>();
        crossoverMethod = EvoLoader.CrossoverMethod;
        selectionMethod = EvoLoader.SelectionMethod;
        mutationMethods = EvoLoader.MutationMethods;
        allRules = Rules;
    }

    /**
     * Fills up the population to "initialPopulation" count.
     * Takes a random member of the population and inserts it again until reaching "initialPopulation"
     */
    private void duplicateParents()
    {
        Random rand = new Random();
        int parentsCount = Population.size();
        Solution lastParent = null;
        int TRY = 0;
        for(int i=initialPopulation; i> parentsCount; i--)
        {
            TRY = 0;
            Solution temp = Population.get(rand.nextInt(parentsCount));
            while(temp.equals(lastParent))
            {
                temp = Population.get(rand.nextInt(parentsCount));
                TRY++;
                if(TRY % 4 == 3)
                    temp = factory.generateSolution();
            }
            Population.add(temp);
            lastParent = temp;
        }
    }

    /**
     * NOT IN USE - personal reasons only
     * prints all fitnesses of the population
     */
    public void printPopulationFitness()
    {
        for(Solution obj : Population)
        {
            System.out.println(obj.evaluateFitness(allRules));
        }
    }

    public Collection<Rule> getAllRules() {
        return allRules;
    }

    @Override
    public void evaluateAllPopulation()
    {
        for(Solution s : Population)
        {
            s.evaluateFitness(allRules);
        }
    }

    @Override
    public void generateInitialPopulation(SolutionFactory<T> fact)
    {
        factory = fact;
        for(int i=0; i<initialPopulation; i++)
        {
            Population.add(factory.generateSolution());
        }

    }

    @Override
    public void createNextGeneration()
    {
        sortAndFindBestSolution();
        Population = selectionMethod.SelectParents(Population, allRules);
        duplicateParents();

        Population = crossoverMethod.crossParents(Population, factory);

        for(Solution s : Population)
        {
            for(Mutation m : mutationMethods)
            {
                try {
                    m.MutateSolution(s);
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    @Override
    public double findFitnessOfBestSolution() throws Exception {
        return sortAndFindBestSolution().getFitness();
    }

    @Override
    public Solution sortAndFindBestSolution(){
        evaluateAllPopulation();

        Population.sort((t1,t2)-> {
            try {
                if(t2.getFitness() > t1.getFitness()) return 1;
                if(t2.getFitness() < t1.getFitness()) return -1;
                return 0;
            }
            catch (Exception e) {return -1;}

        });
        return Population.get(0);
    }


}
