package Proporties.SelectionMethods;

import Data.Rule;
import Data.Solution;
import Proporties.Selection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Tournament extends Selection implements Serializable {
    double PTE;

    public Tournament(int elitism, String conf, double PTE) {
        super(elitism, conf);
        this.PTE = PTE;
    }

    public ArrayList<Solution> SelectParents(List<Solution> Population, Collection<Rule> allRules)
    {
        return null;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "elitism=" + elitism +
                ", configuration='" + configuration + '\'' +
                ", PTE=" + PTE +
                '}';
    }
}
