package Interactive;

import Data.Problem;
import Data.Solution;
import Data.SolutionFactory;
import Database.EvoLoader;
import Database.SolutionLoader;
import Database.TTFactory;
import Database.TTLoader;
import Printers.Printer;
import Management.EEException;
import TTSolution.TTException;
import Printers.TimeTable.TimeTablePrinter;

import java.util.Map;
import java.util.Scanner;

public class Menu {
    //-------------------------------------------------------
    //Put here the factory and loader of your current project
    public static final Class CURRENT_APP_SOLUTION_LOADER_CLASS = TTLoader.class;
    public static final Class CURRENT_APP_SOLUTION_FACTORY_CLASS = TTFactory.class;
    public static final Class CURRENT_APP_SOLUTION_PRINTER_CLASS = TimeTablePrinter.class;
    //-------------------------------------------------------

    //-------------------------------------------------------
    //Current system configuration and information
    public static Map<Integer,Double> evolution = null;
    public static Thread runEvoThread = null;
    public static SolutionLoader solutionLoader = null;
    public static EvoLoader evoLoader = null;
    public static Printer solutionPrinter = null;
    public static SolutionFactory solutionFactory = null;
    public static Problem problem = null;
    public static Solution bestSolution = null;
    public static int bestSolutionGeneration = 0;
    public static boolean isActive = false; //Like Thread.IsAlive() but gives me more immediate data
    //-------------------------------------------------------

    public static  void runApp()
    {
        int input;
        System.out.println("Hello, Welcome to the evolutionary problem solving app");
        showMenu();
        input = readInt();
        while (input != 8)
        {
            try {
                switch (input) {
                    case 1:
                        LoadFileData.LoadFromXML();
                        break;
                    case 2:
                        ObserveData.showSystem();
                        break;
                    case 3:
                        if (!isActive)
                            StartAlgorithem.StartEvolutionProcess();
                        else {
                            runEvoThread.interrupt();
                            isActive = false;
                        }
                        break;
                    case 4:
                        ObserveResults.viewBestSolution();
                        break;
                    case 5:
                        ObserveResults.viewEvolutionProgress();
                        break;
                    case 6:
                        if(isActive)
                            throw new Exception("You cant save the current state when the algorithm is running, stop it first");
                        SerializeSystem.saveSystemToFile();
                        break;
                    case 7:
                        if(isActive)
                            throw new Exception("You cant load an existing state when the algorithm is running, stop it first");
                        SerializeSystem.loadSystemFromFile();
                        break;
                    default:
                        System.out.println("Please enter a valid option");
                        break;
                }
            }
            catch (TTException | EEException e)
            {
                System.out.println(">>>>>>>>>>>>>>>>>");
                System.out.println(e.getMessage());
                System.out.println(">>>>>>>>>>>>>>>>>");
            }
            catch (Exception e)
            {
                System.out.println("Encountered a problem: " + e.getMessage());
            }
            showMenu();
            input = readInt();
        }
        if(Menu.runEvoThread != null)
            Menu.runEvoThread.interrupt();
        System.out.println("Good bye!");
    }

    private static void showMenu()
    {
        System.out.println("================================================================================");
        System.out.println("Please choose an action: ");
        System.out.println("1. Load XML file regarding system components");
        System.out.println("2. Show system properties and configuration");
        if(!isActive)
            System.out.println("3. Activate evolutionary algorithm");
        else
            System.out.println("3. Stop evolutionary algorithm");
        System.out.println("4. Show best solution");
        System.out.println("5. Show evolution process");
        System.out.println("6. Save current state");
        System.out.println("7. Load existing state");
        System.out.println("8. Exit");
    }

    /**
     * Read an integer number from the user
     *
     * @return The integer that was read.
     */
    public static int readInt()
    {
        Scanner scn = new Scanner( System.in );
        while (true) {
            try {
                int res = Integer.parseInt(scn.nextLine());
                return res;
            } catch (NumberFormatException e) {
                System.out.println("Please provide a valid integer!");
            }
        }
    }
}
