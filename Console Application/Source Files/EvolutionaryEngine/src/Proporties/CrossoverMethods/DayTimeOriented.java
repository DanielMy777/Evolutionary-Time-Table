package Proporties.CrossoverMethods;

import Data.Property;
import Data.Solution;
import Data.SolutionFactory;
import Proporties.Crossover;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//This class represents the crossover method - Day Time Oriented Crossover

public class DayTimeOriented extends Crossover implements Serializable {

    public DayTimeOriented(int cuttingPoints, String configuration) {
        super(cuttingPoints, configuration);
    }

    @Override
    public ArrayList<Solution> crossParents(List<Solution> parents, SolutionFactory fact)
    {
        ArrayList<Solution> res = new ArrayList<>();
        ArrayList<Property> Parent1Properties;
        ArrayList<Property> Parent2Properties;
        for(int i=0; i<parents.size()-1; i+=2)
        {
            Parent1Properties = parents.get(i).getSolutionAsPropertyArray();
            Parent2Properties = parents.get(i+1).getSolutionAsPropertyArray();
            Solution[] children = makeChildren(Parent1Properties, Parent2Properties, fact);
            res.add(children[0]);
            res.add(children[1]);
        }
        return res;
    }

    @Override
    public String toString() {
        return "DayTimeOriented{" +
                "cuttingPoints=" + cuttingPoints +
                ", Configuration='" + Configuration + '\'' +
                '}';
    }
}
