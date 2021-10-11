package Proporties.MutationMethods;

import Data.Solution;
import Proporties.Mutation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

//This class represents the Mutation method - Sizer mutation

public class Sizer extends Mutation implements Serializable {

    private int totalTupples;

    public Sizer(double probability, String configuration, int totalTupples) {
        super(probability, configuration);
        this.totalTupples = totalTupples;
    }

    @Override
    public void MutateSolution(Solution sol) throws Exception
    {
        Random rand = new Random();
        if(rand.nextDouble() <= probability) {
            int tupplesToChange;
            int maxTupplesToChange = Math.abs(totalTupples);
            tupplesToChange = rand.nextInt(maxTupplesToChange) + 1;
            if (totalTupples < 0) {
                for (int i = 0; i < tupplesToChange; i++) {
                    sol.removeRandomProperty();
                }
            }
            else {
                for (int i = 0; i < tupplesToChange; i++) {
                    sol.addRandomProperty();
                }
            }
        }
    }

    @Override
    public String toString() {
        return "SizerMutation{" +
                "probability=" + probability +
                ", configuration='" + configuration + '\'' +
                ", totalTupples=" + totalTupples +
                '}';
    }
}
