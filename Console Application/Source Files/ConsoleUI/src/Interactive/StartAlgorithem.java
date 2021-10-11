package Interactive;

import Data.Problem;
import Data.Solution;
import Data.SolutionFactory;
import Database.EvoAlgorithem;
import Database.EvoLoader;
import Design.Table;
import Threads.AlgThread;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class StartAlgorithem {

    private static int numOfGenerations;
    private static int fitnessToStop;
    private static boolean wantsToStopAtGenerations;
    private static boolean wantsToStopAtFitness;
    private static int onceInGenerations;

    /**
     * Start the evolution process, after loading system configurations
     */
    public static void StartEvolutionProcess() throws Exception
    {
        if(Menu.solutionLoader == null || Menu.evoLoader == null)
        {
            throw new Exception("Please load data from XML before trying to use this command!");
        }
        wantsToStopAtFitness = false;
        wantsToStopAtGenerations = false;
        if(getPreferencesFromUser() == 0)
            return;

        Menu.isActive = true;
        (Menu.runEvoThread = new AlgThread()).start(); //executes method PerformEvolutionProcess
    }

    /**
     * Perform the evolution process on current system configurations
     */
    public static void PerformEvolutionProcess() throws Exception
    {
        double maxFitness = 0;
        double bestFit = -1;
        DecimalFormat dFormat = new DecimalFormat("###.##");
        Menu.evolution = new ConcurrentSkipListMap<Integer, Double>();
        Menu.solutionFactory = (SolutionFactory<? extends Solution>) Menu.CURRENT_APP_SOLUTION_FACTORY_CLASS.newInstance();
        Menu.problem = new EvoAlgorithem<>(Menu.solutionLoader.getAllRules());
        Menu.problem.generateInitialPopulation(Menu.solutionFactory);
        //for loop condition was a logical analysis of the wanted stop condition
        for(int i=0; (!wantsToStopAtGenerations || i<=numOfGenerations) && (!wantsToStopAtFitness || bestFit < fitnessToStop) && !Thread.currentThread().isInterrupted(); i++)
        {
            bestFit = Menu.problem.findFitnessOfBestSolution();
            if(bestFit > maxFitness)
            {
                synchronized (Menu.problem) {
                    maxFitness = bestFit;
                    Menu.bestSolution = Menu.problem.sortAndFindBestSolution();
                    Menu.bestSolutionGeneration = i;
                }
            }
            if(i % onceInGenerations == 0)
            {
                Table dataTable = new Table("Report for generation #" + i, "Best Fitness");
                Menu.evolution.put(i, bestFit);
                dataTable.addRow(String.valueOf(dFormat.format(bestFit)));
                //dataTable.printTable();
            }
            Menu.problem.createNextGeneration();
        }

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        System.out.println("\n");
        Table dataTable = new Table("Run ended - Summary", "Generation", "Best Fitness");
        dataTable.addRow("Final", String.valueOf(dFormat.format(bestFit)));
        dataTable.addRow("Overall", String.valueOf(dFormat.format(maxFitness)));
        dataTable.printTable();
        System.out.println("This is only information - Please continue whatever you were doing");

        Menu.isActive = false;
    }

    /**
     * Receive preferences of 'how to run' the evolution from the user
     *
     * @return 0 iff the user wants to quit and abort.
     */
    private static int getPreferencesFromUser()
    {
        Scanner scn = new Scanner( System.in );
        char pick;
        if(Menu.evolution != null)
        {
            System.out.println("There are evolution results in the program, override? (Y / N)");
            pick = scn.nextLine().charAt(0);
            while(pick != 'Y' && pick != 'N') {
                System.out.println("Y - for yes\nN - for no");
                pick = scn.nextLine().charAt(0);
            }
            if(pick == 'N')
                return 0;
        }
        if(Menu.isActive)
        {
            System.out.println("There already is an evolution process running, continue? (Y / N)");
            pick = scn.nextLine().charAt(0);
            while(pick != 'Y' && pick != 'N') {
                System.out.println("Y - for yes\nN - for no");
                pick = scn.nextLine().charAt(0);
            }
            if(pick == 'N')
                return 0;
        }
        System.out.println("Would you like to stop after a specific amount of generations? or when reaching a certain fitness?");
        System.out.println("(Your options are GENERATIONS / FITNESS / BOTH)");
        String choose = scn.nextLine();
        while (!choose.equals("GENERATIONS") && !choose.equals("FITNESS") && !choose.equals("BOTH") && !choose.equals("QUIT"))
        {
            System.out.println("Unvalid pick - " + choose);
            System.out.println("(Your options are: GENERATIONS / FITNESS / BOTH");
            System.out.println("Or type QUIT to quit");
            choose = scn.nextLine();
        }
        if(choose.equals("QUIT"))
            return 0;
        if(choose.equals("GENERATIONS") || choose.equals("BOTH")) {
            System.out.println("Please enter the amount of max generations to process");
            numOfGenerations = Menu.readInt();
            while (numOfGenerations <= 100) {
                System.out.println("Please enter an amount over 100, or -1 to return to the menu");
                numOfGenerations = Menu.readInt();
                if (numOfGenerations == -1)
                    return 0;
            }
            wantsToStopAtGenerations = true;
        }
        if(choose.equals("FITNESS") || choose.equals("BOTH"))
        {
            System.out.println("Please enter the maximal fitness to reach (Natural number 0 - 100)");
            fitnessToStop = Menu.readInt();
            while (fitnessToStop < 0 || fitnessToStop > 100) {
                System.out.println("Value unreachable, Please enter an integer between 0 and 100, or -1 to return to the menu");
                fitnessToStop = Menu.readInt();
                if (fitnessToStop == -1)
                    return 0;
            }
            wantsToStopAtFitness = true;
        }

        System.out.println("Once in how many generations would you like to be updated?");
        onceInGenerations = Menu.readInt();
        while (onceInGenerations <= 0 || (wantsToStopAtGenerations  && (onceInGenerations > numOfGenerations))) {
            System.out.println("Please enter a valid amount, over 0 and below sum of generations (or -1 to return)");
            onceInGenerations = Menu.readInt();
            if (onceInGenerations == -1)
                return 0;
        }
        return 1;
    }
}
