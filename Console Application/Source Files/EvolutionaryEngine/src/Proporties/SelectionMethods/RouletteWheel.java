package Proporties.SelectionMethods;

import Data.Rule;
import Data.Solution;
import Proporties.Selection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RouletteWheel extends Selection implements Serializable {
    public RouletteWheel(int elitism, String conf) {
        super(elitism, conf);
    }

    public ArrayList<Solution> SelectParents(List<Solution> Population, Collection<Rule> allRules)
    {
        return null;
    }

    @Override
    public String toString() {
        return "RouletteWheel{" +
                "elitism=" + elitism +
                ", configuration='" + configuration + '\'' +
                '}';
    }
}
