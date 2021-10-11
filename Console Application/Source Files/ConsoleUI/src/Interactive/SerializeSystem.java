package Interactive;

import Data.Solution;
import Database.DTO;
import Database.EvoAlgorithem;
import Database.EvoLoader;
import Database.SolutionLoader;
import javafx.util.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class SerializeSystem {

    private static final int EVO_LOADER_DTO_IN_LIST = 2;
    private static final int SOLUTION_LOADER_DTO_IN_LIST = 1;
    private static final int ALGORITHM_DTO_IN_LIST = 0;

    /**
     * Save all system information data to a file of the user's choice
     */
    public static void saveSystemToFile() throws Exception
    {
        if(Menu.solutionLoader == null)
            throw new Exception("There is no data to save...");
        Scanner scn = new Scanner(System.in);
        System.out.println("Please enter the path you would like the file to be saved (example: C\\MyFolder\\MyFile.dat)");
        String path = scn.nextLine();
        ArrayList<DTO> data = new ArrayList<>();
        data.add(new DTO(
                new Pair<>("Best Solution", Menu.bestSolution),
                new Pair<>("Best Solution Generation", Menu.bestSolutionGeneration),
                new Pair<>("Evolution Progress", Menu.evolution)
        ));
        data.add(((SolutionLoader)Menu.CURRENT_APP_SOLUTION_LOADER_CLASS.newInstance()).saveDataForSerialization());
        data.add((EvoLoader.saveData()));
        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream(path))) {
            out.writeObject(data);
            out.flush();
            System.out.println("System data saved successfully!");
        }
        catch (FileNotFoundException e)
        {
            throw new Exception("File location unavailable on your system");
        }
    }

    /**
     * load all system information data from a file of the user's choice
     */
    public static void loadSystemFromFile() throws Exception {
        Scanner scn = new Scanner(System.in);
        System.out.println("Please enter the path of the saved file (example: C\\MyFolder\\MyFile.dat)");
        String path = scn.nextLine();
        Path p = Paths.get(path);
        if (!Files.exists(p))
            throw new Exception("File does not exist");
        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(path))) {
            ArrayList<DTO> data = (ArrayList<DTO>) in.readObject();
            DTO algDTO = data.get(ALGORITHM_DTO_IN_LIST);
            DTO evoDTO = data.get(EVO_LOADER_DTO_IN_LIST);
            DTO solDTO = data.get(SOLUTION_LOADER_DTO_IN_LIST);
            Menu.solutionLoader = (SolutionLoader) Menu.CURRENT_APP_SOLUTION_LOADER_CLASS.newInstance();
            Menu.solutionLoader.loadDataForSerialization(solDTO);
            Menu.evoLoader = new EvoLoader();
            Menu.evoLoader.loadData(evoDTO);
            Menu.evolution = (Map<Integer, Double>) algDTO.getData("Evolution Progress");
            Menu.bestSolution = (Solution) algDTO.getData("Best Solution");
            Menu.bestSolutionGeneration = (int) algDTO.getData("Best Solution Generation");
            Menu.problem = new EvoAlgorithem<>(Menu.solutionLoader.getAllRules());
            System.out.println("System data loaded successfully!");
        }
        catch (FileNotFoundException e)
        {
            throw new Exception("File not found at the specified location");
        }
    }
}
