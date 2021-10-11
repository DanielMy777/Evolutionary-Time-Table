package Printers;

import Database.DTO;
import Design.Table;
import Proporties.Crossover;
import Proporties.Mutation;
import Proporties.Selection;

import java.util.ArrayList;

//this class knows how to print an evolution system to the screen

public class EvoPrinter extends Printer{

    /**
     * Print the problem configuration to the screen
     *
     * @param data - the configuration as a DTO
     */
    @Override
    public void PrintProperties(DTO data) {

        System.out.println("Evolution Engine Properties:\n");

        Table PopulationTable = new Table("Population", "Property", "Value");
        PopulationTable.addRow("Population Size", data.getData("InitialPopulation").toString());

        Selection selectionMethod = (Selection) data.getData("SelectionMethod");
        Table SelectionTable = new Table("Selection method", "Type", "Configuration");
        SelectionTable.addRow(selectionMethod.getClass().getSimpleName(), selectionMethod.getConfiguration());

        Crossover crossoverMethod = (Crossover) data.getData("CrossoverMethod");
        Table CrossTable = new Table("Crossover method", "Type", "Cutting Points", "Configuration");
        CrossTable.addRow(crossoverMethod.getClass().getSimpleName(),
                String.valueOf(crossoverMethod.getCuttingPoints()),
                crossoverMethod.getConfiguration());

        int i=1;
        ArrayList<Mutation> mutations = (ArrayList<Mutation>) data.getData("MutationMethods");
        Table mutationTable = new Table("Mutation methods", "Serial", "Type", "Probability", "Configuration");
        for(Mutation m : mutations)
        {
            mutationTable.addRow("Mutation #" + i,
                    m.getClass().getSimpleName(),
                    String.valueOf(m.getProbability()),
                    m.getConfiguration());
                    i++;
        }

        PopulationTable.printTable();
        SelectionTable.printTable();
        CrossTable.printTable();
        mutationTable.printTable();
        System.out.println("\n");
    }

    @Override
    public void PrintSolution(DTO data) { //TODO FOR NEXT ASSIGNMENT

    }
}
