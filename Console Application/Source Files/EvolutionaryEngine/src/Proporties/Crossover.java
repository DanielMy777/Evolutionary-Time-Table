package Proporties;

import Data.Property;
import Data.Solution;
import Data.SolutionFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//This class represents a Crossover method

abstract public class Crossover implements Serializable {

    protected int cuttingPoints;
    protected String Configuration;

    public Crossover(int cuttingPoints, String configuration) {
        this.cuttingPoints = cuttingPoints;
        Configuration = configuration;
    }

    /**
     * Perform a crossover on a collection of parents (each 2 parents create 2 children)
     *
     * @param parents the initial collection of parents
     * @param fact a factory that can generate Solutions of the parent's types
     *
     * @return an ArrayList of the children - result of the crossover
     */
    abstract public ArrayList<Solution> crossParents(List<Solution> parents, SolutionFactory fact);

    /**
     * Perform a crossover on 2 parents, resulting in 2 children
     *
     * @param p1 the property array that the first parent solution is built of.
     * @param p2 the property array that the second parent solution is built of.
     * @param fact a factory that can generate Solutions of the parent's types
     *
     * @return an array of 2 Solutions - the two children.
     */
    public Solution[] makeChildren(ArrayList<Property> p1, ArrayList<Property> p2, SolutionFactory fact)
    {
        Random rand = new Random();
        int k;
        int size = Math.min(p1.size(), p2.size());
        int p1PropsCounted = 0, p2PropsCounted = 0, cut = 0;
        ArrayList<Property> child1Proporties = new ArrayList<>();
        ArrayList<Property> child2Proporties = new ArrayList<>();
        for(int j=0; j<cuttingPoints+1; j++)
        {
            if(j == cuttingPoints) //last iter
                cut = size;
            else
                cut = rand.nextInt(Math.max(((3*size)/4) , 1));
            for(k=0; k<cut; k++)
            {
                if(j%2 == 0)
                {
                    child1Proporties.add((Property) p1.get(p1PropsCounted++).clone());
                    child2Proporties.add((Property) p2.get(p2PropsCounted++).clone());
                }
                else
                {
                    child2Proporties.add((Property) p1.get(p1PropsCounted++).clone());
                    child1Proporties.add((Property) p2.get(p2PropsCounted++).clone());
                }
            }
            size -= cut;
        }
        Solution c1 = fact.createSolution(child1Proporties);
        Solution c2 = fact.createSolution(child2Proporties);
        return new Solution[]{c1,c2};
    }

    public String getConfiguration()
    {
        return Configuration;
    }

    public int getCuttingPoints()
    {
        return cuttingPoints;
    }

    @Override
    public String toString() {
        return "Crossover{" +
                "cuttingPoints=" + cuttingPoints +
                ", Configuration='" + Configuration + '\'' +
                '}';
    }
}
