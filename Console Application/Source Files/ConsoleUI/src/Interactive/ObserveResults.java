package Interactive;

import Data.Rule;
import Database.DTO;
import Design.Table;
import Printers.Printer;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.util.Pair;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ObserveResults {

    /**
     * Show the user the current best solution in the current population. (after inputting the user preferences)
     */
    public static void viewBestSolution() throws Exception
    {
        if(Menu.problem == null)
            throw new Exception("Please start the evolution process before trying to use this command!");
        synchronized (Menu.problem) {
            if (Menu.bestSolution == null)
                throw new Exception("Best solution still not generated, please try again later!");

            String pick = showSolutionMenu();
            DTO data = new DTO(
                    new Pair<>("Configuration", pick),
                    new Pair<>("Solution", Menu.bestSolution)
            );
            if (pick.equals("QUIT"))
                return;

            Menu.solutionPrinter = (Printer) Menu.CURRENT_APP_SOLUTION_PRINTER_CLASS.newInstance();
            Menu.solutionPrinter.PrintSolution(data);

            Set<Rule> rulesSet = Menu.solutionLoader.getAllRules();
            Map<Rule, Boolean> rulesMap = Menu.solutionLoader.getAllRulesWithWeights();
            double hardSum = 0, softSum = 0, currScore;
            int hardCount = 0, softCount = 0;
            double hardAVG, softAVG;
            String type;

            DecimalFormat dFormat = new DecimalFormat("###.##");
            Table solutionFitness = new Table("Solution Data", "Fitness", "Generation");
            solutionFitness.addRow(String.valueOf(
                    dFormat.format(Menu.bestSolution.evaluateFitness(rulesSet))),
                    String.valueOf(Menu.bestSolutionGeneration));

            dFormat = new DecimalFormat("###.#");
            Table rulesTable = new Table("Rules Processed", "Name", "Hard / Soft", "Score");
            for (Rule r : rulesSet) {
                if (rulesMap.get(r)) {
                    type = "Hard";
                    hardSum += currScore = r.eval(Menu.bestSolution);
                    hardCount++;
                } else {
                    type = "Soft";
                    softSum += currScore = r.eval(Menu.bestSolution);
                    softCount++;
                }
                rulesTable.addRow(r.toString(), type, String.valueOf(dFormat.format(currScore)));
            }


            hardAVG = (double) hardSum / hardCount;
            softAVG = (double) softSum / softCount;
            Table rulesAvg = new Table("Statistics", "Property", "Value");
            rulesAvg.addRow("Hard Rules Weight",
                    String.valueOf((int) Menu.solutionLoader.getSystemProperties().getData("Hard Rules Weight")));
            rulesAvg.addRow("Hard Rules Average", String.valueOf(dFormat.format(hardAVG)));
            rulesAvg.addRow("Soft Rules Average", String.valueOf(dFormat.format(softAVG)));

            solutionFitness.printTable();
            rulesTable.printTable();
            rulesAvg.printTable();
        }

    }

    /**
     * Receive the way the user would like the solution to be printed
     */
    private static String showSolutionMenu()
    {
        Scanner scn = new Scanner(System.in);
        System.out.println("How would you like to present the solution?");
        System.out.println("(Your options are: RAW / TEACHER / CLASS)");
        String pick = scn.nextLine();
        while (!pick.equals("RAW") && !pick.equals("TEACHER") && !pick.equals("CLASS") && !pick.equals("QUIT"))
        {
            System.out.println("Unvalid pick - " + pick);
            System.out.println("(Your options are: RAW / TEACHER / CLASS)");
            System.out.println("Or type QUIT to quit");
            pick = scn.nextLine();
        }
        return pick;
    }

    /**
     * Show the progress of the evolution process (best fitness in generation)
     * through all generations (in the jumps that were asked)
     * if an evolution is in progress, last 10 generations will show.
     */
    public static void viewEvolutionProgress() throws Exception
    {
        if(Menu.bestSolution == null || Menu.evolution == null)
            throw new Exception("Please start the evolution process before trying to use this command!");

        DecimalFormat dFormat = new DecimalFormat("###.##");
        int size = Menu.evolution.size();
        int i=0;
        double oldFit = 0;
        double dif = 0;
        String increase;
        Table progress = new Table("Evolution Progress", "Generation", "Best Fitness", "Difference");
        for(Map.Entry<Integer, Double> e : Menu.evolution.entrySet())
        {
            dif = oldFit == 0 ? 0 : e.getValue() - oldFit;
            if(!Menu.isActive || i >= size - 10) {
                increase = dif < 0 ? "" : "+";
                increase += dFormat.format(dif);
                progress.addRow(
                        e.getKey().toString(),
                        dFormat.format(e.getValue()),
                        increase
                );
            }
            oldFit = e.getValue();
            i++;
        }
        progress.printTable();
    }
}
