package Proporties;

import Data.Rule;
import Data.Solution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//This class represents a Selection method

abstract public class Selection implements Serializable {
    protected int elitism;
    protected String configuration;

    public Selection(int elitism, String conf) {
        this.elitism = elitism;
        this.configuration = conf;
    }

    /**
     * Perform a Selection on a collection of solutions.
     * pick the top (*some lies) parents to build the next generation.
     *
     * @param Population the initial collection of parents
     * @param allRules the rules to evaluate the parents by.
     *
     * @return an ArrayList of the parents that made it through the selection.
     */
    abstract public ArrayList<Solution> SelectParents(List<Solution> Population, Collection<Rule> allRules);

    public String getConfiguration()
    {
        return  configuration;
    }

    @Override
    public String toString() {
        return "Selection{" +
                "elitism=" + elitism +
                ", configuration='" + configuration + '\'' +
                '}';
    }
}
