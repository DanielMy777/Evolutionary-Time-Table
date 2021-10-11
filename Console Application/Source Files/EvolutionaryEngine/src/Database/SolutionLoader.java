package Database;

import Data.Rule;
import generated.ETTDescriptor;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

//This interface represents a database containing system information for a solution.

public interface SolutionLoader {

    /**
     * Loads the database with information from an XML file.
     *
     * @param XMLRoute The route to the XML file
     */
    ETTDescriptor loadXMLFile(String XMLRoute) throws Exception;

    /**
     * Loads the database with information from an InputStream.
     *
     * @param IS The InputStream (XML file marshalled by JAXB)
     */
    ETTDescriptor Deserialize(InputStream IS) throws JAXBException, Exception;

    /**
     * Get all the information of the system properties.
     *
     * @return a DTO containing all dry info.
     */
    DTO saveDataForSerialization();

    /**
     * load all information of the system properties.
     *
     * @param saver a DTO containing all dry info to load - of the fitting solution.
     */
    void loadDataForSerialization(DTO saver) throws Exception;

    /**
     * Get all the information of the system properties.
     *
     * @return a DTO containing all dry info.
     */
    DTO getSystemProperties();

    /**
     * Get all rules in the DB
     */
    Set<Rule> getAllRules();

    /**
     * Get all rules in the DB - with their weights
     */
    Map<Rule, Boolean> getAllRulesWithWeights();
}
