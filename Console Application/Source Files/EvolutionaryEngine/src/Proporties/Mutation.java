package Proporties;

import Data.Solution;

import java.io.Serializable;

//This class represents a Mutation method

abstract public class Mutation implements Serializable {

    protected double probability;
    protected String configuration;

    public Mutation(double probability, String configuration) {
        this.probability = probability;
        this.configuration = configuration;
    }

    /**
     * Perform a mutation of a certain solution
     *
     * @param sol the original solution - to be mutated at the end of the method.
     */
    abstract public void MutateSolution(Solution sol) throws Exception;

    public String getConfiguration()
    {
        return  configuration;
    }

    public double getProbability()
    {
        return probability;
    }

    @Override
    public String toString() {
        return "Mutation{" +
                "probability=" + probability +
                ", configuration='" + configuration + '\'' +
                '}';
    }
}
