package Proporties.SelectionMethods;

import Data.Rule;
import Data.Solution;
import Proporties.Selection;

import java.io.Serializable;
import java.util.*;

//This class represents the Selection method - Truncation Selection

public class Truncation extends Selection implements Serializable {

    int topPercent;

    public Truncation(int elitism, String conf, int topPercent) {
        super(elitism, conf);
        this.topPercent = topPercent;
    }

    @Override
    public ArrayList<Solution> SelectParents(List<Solution> Population, Collection<Rule> allRules)
    {
        Random rand = new Random();
        double chanceToSurvive = 0.4;
        int fromTop = 0;
        ArrayList<Solution> Parents = new ArrayList<>();

        int numberOfParents = (Population.size() * topPercent) / 100;
        for(int i=0; i<numberOfParents; i++)
        {
            if(rand.nextDouble() < chanceToSurvive)
                Parents.add(Population.get(rand.nextInt(Population.size())));
            else
                Parents.add(Population.get(fromTop++));
        }
        return Parents;
    }

    @Override
    public String toString() {
        return "Truncation{" +
                "elitism=" + elitism +
                ", configuration='" + configuration + '\'' +
                ", topPercent=" + topPercent +
                '}';
    }
}
