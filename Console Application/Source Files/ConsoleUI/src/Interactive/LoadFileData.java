package Interactive;

import Database.EvoLoader;
import Database.SolutionLoader;
import generated.ETTDescriptor;

import java.util.Scanner;

public class LoadFileData {

    /**
     * Load the system configuration from an XML file to be inputted by the user
     */
    public static void LoadFromXML() throws Exception
    {
        Scanner scn = new Scanner( System.in );
        if(checkOK() == 0)
            return;

        System.out.println("Please enter the path to the XML file: (format - myFolder/myFile.XML)");
        SolutionLoader saveSL = Menu.solutionLoader;
        EvoLoader saveEL = Menu.evoLoader;
        String path = scn.nextLine();
        if(!path.endsWith(".xml"))
            throw new Exception("Please provide an .xml file");
        try {
            if(Menu.isActive)
                Menu.runEvoThread.interrupt();
            Menu.isActive = false;
            Menu.solutionLoader = (SolutionLoader) Menu.CURRENT_APP_SOLUTION_LOADER_CLASS.newInstance(); //A loader class to the problem you have
            ETTDescriptor dataFromXml = Menu.solutionLoader.loadXMLFile(path);
            Menu.evoLoader = new EvoLoader();
            Menu.evoLoader.LoadEngineData(dataFromXml);
            Menu.bestSolution = null;
            Menu.evolution = null;
            System.out.println("\nData loaded successfully!");
        }
        catch (Exception e)
        {
            Menu.solutionLoader = saveSL;
            Menu.evoLoader = saveEL;
            throw e;
        }

    }

    /**
     * Notify the user that he is overriding an existing configuration.
     */
    public static int checkOK()
    {
        Scanner scn = new Scanner( System.in );
        char pick;
        if(Menu.solutionLoader != null)
        {
            System.out.println("You are about to override data you already loaded, continue? (Y / N)");
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
            System.out.println("You are in the middle of an evolution process, continue? (Y / N)");
            pick = scn.nextLine().charAt(0);
            while(pick != 'Y' && pick != 'N') {
                System.out.println("Y - for yes\nN - for no");
                pick = scn.nextLine().charAt(0);
            }
            if(pick == 'N')
                return 0;
        }
        return 1;
    }
}
