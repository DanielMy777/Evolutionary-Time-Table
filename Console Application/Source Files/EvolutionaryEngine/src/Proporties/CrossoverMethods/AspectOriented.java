package Proporties.CrossoverMethods;

import Data.Property;
import Data.Solution;
import Data.SolutionFactory;
import Proporties.Crossover;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//This class represents the crossover method - Aspect Oriented Crossover

public class AspectOriented extends Crossover implements Serializable {
    private String Orientation;

    public AspectOriented(int cuttingPoints, String configuration, String orientation) {
        super(cuttingPoints, configuration);
        Orientation = orientation;
    }

    /**
     * During aspect oriented crossover, Properties can drop low.
     * After each crossover, we complete them with randomized Properties if needed.
     * Acts as a compensation.
     *
     * @param arr a property array building the solution.
     * @param maxToComplete maximum tupples (properties) to comlete
     * @param fact factory that can generate new properties.
     */
    public void Complete(ArrayList<Property> arr, int maxToComplete, SolutionFactory fact)
    {
        Random rand = new Random();
        int ToComplete = rand.nextInt(maxToComplete);
        for(int i=0; i<ToComplete; i++)
        {
            arr.add(fact.generateProperty());
        }
    }

    @Override
    public ArrayList<Solution> crossParents(List<Solution> parents, SolutionFactory fact)
    {
        int toComplete;
        ArrayList<Solution> res = new ArrayList<>();
        ArrayList<Property> Parent1Properties;
        ArrayList<Property> Parent2Properties;
        ArrayList<Property> Parent1CurrentProperties;
        ArrayList<Property> Parent2CurrentProperties;
        ArrayList<Property> Child1FinalProperties;
        ArrayList<Property> Child2FinalProperties;
        String component = Orientation.substring(0,1);
        Property dummyProperty = parents.get(0).getSolutionAsPropertyArray().get(0);
        for(int i=0; i<parents.size()-1; i+=2)
        {
            Child1FinalProperties = new ArrayList<>();
            Child2FinalProperties = new ArrayList<>();
            Parent1Properties = parents.get(i).getSolutionAsPropertyArray();
            Parent2Properties = parents.get(i+1).getSolutionAsPropertyArray();
            toComplete = Math.min(Parent1Properties.size(), Parent2Properties.size());
            for(int j=1; j<= dummyProperty.getComponentLimit(component); j++) {
                int finalJ = j;
                Parent1CurrentProperties = new ArrayList<>();
                Parent2CurrentProperties = new ArrayList<>();
                Parent1Properties.stream().filter((p)-> p.getComponent(component) == finalJ).forEachOrdered(
                        Parent1CurrentProperties::add);
                Parent2Properties.stream().filter((p)-> p.getComponent(component) == finalJ).forEachOrdered(
                        Parent2CurrentProperties::add);
                Solution[] children = makeChildren(Parent1CurrentProperties, Parent2CurrentProperties, fact);
                Child1FinalProperties.addAll(children[0].getSolutionAsPropertyArray());
                Child2FinalProperties.addAll(children[1].getSolutionAsPropertyArray());
            }
            Complete(Child1FinalProperties, 2, fact);
            Complete(Child2FinalProperties, 2, fact);
            res.add(fact.createSolution(Child1FinalProperties));
            res.add(fact.createSolution(Child2FinalProperties));
        }
        return res;
    }

    @Override
    public String toString() {
        return "AspectOriented{" +
                "cuttingPoints=" + cuttingPoints +
                ", Configuration='" + Configuration + '\'' +
                ", Orientation='" + Orientation + '\'' +
                '}';
    }
}
