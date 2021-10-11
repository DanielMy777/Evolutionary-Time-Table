package Database;

import Management.EEException;
import Proporties.Crossover;
import Proporties.CrossoverMethods.CrossoverLoader;
import Proporties.Mutation;
import Proporties.MutationMethods.MutationLoader;
import Proporties.Selection;
import Proporties.SelectionMethods.SelectionLoader;
import generated.ETTDescriptor;
import generated.ETTEvolutionEngine;
import generated.ETTMutation;
import javafx.util.Pair;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//This class acts as a Database - responsible of loading the current information of the evolutionary system.

public class EvoLoader {

    private final static String JAXB_GENERATED_IN = "generated";

    public static boolean databaseIsLoaded;
    public static int initialPopulation;
    public static Selection SelectionMethod;
    public static Crossover CrossoverMethod;
    public static List<Mutation> MutationMethods;

    public EvoLoader() {
        if(!databaseIsLoaded)
            clearDB();
    }

    /**
     * Loads the database with information from an XML file.
     *
     * @param XMLRoute The route to the XML file
     */
    public void loadXMLFile(String XMLRoute) throws EEException {
        try {
            InputStream IS = new FileInputStream(new File(XMLRoute));
            Deserialize(IS);
            databaseIsLoaded = true;
        }
        catch (EEException e) {
            throw e;
        }
        catch (FileNotFoundException e) {
            throw new EEException("Resource error - XML file not found in " + XMLRoute);
        }
        catch (JAXBException e) {
            throw new EEException("Resource error - invalid JAXB execution");
        }
    }

    /**
     * Loads the database with information from an InputStream.
     *
     * @param IS The InputStream (XML file marshalled by JAXB)
     */
    public void Deserialize(InputStream IS) throws JAXBException, EEException
    {
        JAXBContext jc = JAXBContext.newInstance(JAXB_GENERATED_IN);
        Unmarshaller u = jc.createUnmarshaller();
        ETTDescriptor AllData = (ETTDescriptor) u.unmarshal(IS);
        DTO saver = saveData();
        clearDB();
        try {
            LoadEngineData(AllData);
        }
        catch (Exception e)
        {
            loadData(saver);
            throw e;
        }

    }

    /**
     * Get all the information of the system properties.
     *
     * @return a DTO containing all dry info.
     */
    public static DTO getSystemProperties()
    {
        return saveData();
    }

    /**
     * Get all the information of the system properties.
     *
     * @return a DTO containing all dry info.
     */
    public static DTO saveData()
    {
        DTO saver = new DTO(
                new Pair<>("Loaded", databaseIsLoaded),
                new Pair<>("InitialPopulation", initialPopulation),
                new Pair<>("SelectionMethod", SelectionMethod),
                new Pair<>("CrossoverMethod", CrossoverMethod),
                new Pair<>("MutationMethods", MutationMethods)
        );
        return saver;
    }

    /**
     * load all information of the system properties.
     *
     * @param saver a DTO containing all dry info to load.
     */
    public void loadData(DTO saver) throws EEException
    {
        try {
            databaseIsLoaded = (boolean) saver.getData("Loaded");
            initialPopulation = (int) saver.getData("InitialPopulation");
            SelectionMethod = (Selection) saver.getData("SelectionMethod");
            CrossoverMethod = (Crossover) saver.getData("CrossoverMethod");
            MutationMethods = (List<Mutation>) saver.getData("MutationMethods");
        }
        catch (Exception e) {throw new EEException("File to load data from is corrupt");}

    }

    /**
     * Clears the database from information.
     */
    private static void clearDB()
    {
        databaseIsLoaded = false;
        MutationMethods = new ArrayList<>();
        CrossoverMethod = null;
        SelectionMethod = null;
    }

    /**
     * load all information of the system properties from an unmarshalled Descriptor.
     *
     * @param AllData a Descriptor to get info from.
     */
    public void LoadEngineData(ETTDescriptor AllData) throws EEException
    {
        ETTEvolutionEngine engineData = AllData.getETTEvolutionEngine();
        DTO saver = saveData();
        clearDB();
        try {
            initialPopulation = AllData.getETTEvolutionEngine().getETTInitialPopulation().getSize();
            SelectionMethod = SelectionLoader.LoadSelectionFromXML(engineData.getETTSelection());
            CrossoverMethod = CrossoverLoader.LoadCrossoverFromXML(engineData.getETTCrossover());
            for(ETTMutation ettm : engineData.getETTMutations().getETTMutation())
            {
                MutationMethods.add(MutationLoader.LoadMutationFromXML(ettm));
            }
            databaseIsLoaded = true;
        }
        catch (Exception e)
        {
            loadData(saver);
            throw e;
        }

    }

    /**
     * NOT IN USE - for testing reasons only
     * Prints information of the system.
     */
    public static void printAllData()
    {
        if(!databaseIsLoaded)
            System.out.println("No data to show, please load first");
        else
        {
            System.out.println(initialPopulation);
            System.out.println(SelectionMethod);
            System.out.println(CrossoverMethod);
            System.out.println(MutationMethods);
        }
    }
}
