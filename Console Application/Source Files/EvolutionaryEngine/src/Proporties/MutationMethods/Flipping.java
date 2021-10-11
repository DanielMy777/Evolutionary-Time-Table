package Proporties.MutationMethods;

import Data.Solution;
import Proporties.Mutation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

//This class represents the Mutation method - Flipping mutation

public class Flipping extends Mutation implements Serializable {
    public enum Component{
        D, H, C, T, S;
    }

    private int maxTupples;
    private Component ToChange;


    public Flipping(double probability, String configuration, int maxTupples, Component toChange) {
        super(probability, configuration);
        this.maxTupples = maxTupples;
        ToChange = toChange;
    }

    @Override
    public void MutateSolution(Solution sol) throws Exception
    {
        Random rand = new Random();
        int tupplesToChange;
        int numOfTupples = sol.getSolutionAsPropertyArray().size();
        if(rand.nextDouble() <= probability)
        {
            tupplesToChange = rand.nextInt(maxTupples);
            for(int i=0; i<tupplesToChange; i++) {
                sol.randomlyChangeProperty(rand.nextInt(numOfTupples), ToChange.name());
            }
        }
    }

    @Override
    public String toString() {
        return "FlippingMutation{" +
                "probability=" + probability +
                ", configuration='" + configuration + '\'' +
                ", maxTupples=" + maxTupples +
                ", ToChange=" + ToChange +
                '}';
    }
}
