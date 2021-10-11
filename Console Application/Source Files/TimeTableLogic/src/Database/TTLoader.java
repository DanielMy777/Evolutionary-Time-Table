package Database;

import Data.Rule;
import TTInfo.*;
import TTSolution.TTException;
import com.sun.org.apache.xpath.internal.operations.Bool;
import generated.*;
import javafx.util.Pair;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

//This class acts as a database for a problem definition of a TimeTable match finding

public class TTLoader implements SolutionLoader{

    public static final boolean HARD = true;
    public static final boolean SOFT = false;
    private final static String JAXB_GENERATED_IN = "generated";

    public static boolean databaseIsLoaded;
    public static List<Subject> Subjects;
    public static List<Teacher> Teachers;
    public static List<Classroom> Classrooms;
    public static Map<TTRule, Boolean> AllRules;
    public static int days;
    public static int hours;
    public static int hardRulesWeight;

    public TTLoader() {
        if(!databaseIsLoaded)
            clearDB();
    }

    /**
     * Get all the information of the system properties.
     *
     * @return a DTO containing all dry info.
     */
    private static DTO saveData()
    {
        DTO saver = new DTO(
                new Pair<>("Is Database Loaded", databaseIsLoaded),
                new Pair<>("Days", days),
                new Pair<>("Hours", hours),
                new Pair<>("Subjects", Subjects),
                new Pair<>("Teachers", Teachers),
                new Pair<>("Classrooms", Classrooms),
                new Pair<>("Rules", AllRules),
                new Pair<>("Hard Rules Weight", hardRulesWeight)
        );
        return saver;
    }

    /**
     * load all information of the system properties.
     *
     * @param saver a DTO containing all dry info to load.
     */
    private void loadData(DTO saver) throws TTException
    {
        try {
            databaseIsLoaded = (boolean) saver.getData("Is Database Loaded");
            Subjects = (List<Subject>) saver.getData("Subjects");
            Teachers = (List<Teacher>) saver.getData("Teachers");
            Classrooms = (List<Classroom>) saver.getData("Classrooms");
            AllRules = (Map<TTRule, Boolean>) saver.getData("Rules");
            days = (int) saver.getData("Days");
            hours = (int) saver.getData("Hours");
            hardRulesWeight = (int) saver.getData("Hard Rules Weight");
        }
        catch (Exception e) {throw new TTException("Data to load from is corrupt");}
    }

    /**
     * Clears the database from information.
     */
    private static void clearDB()
    {
        Subjects = new ArrayList<>();
        Teachers = new ArrayList<>();
        Classrooms = new ArrayList<>();
        AllRules = new HashMap<>();
        databaseIsLoaded = false;
    }

    /**
     * loads information of Subjects to the Database.
     *
     * @param SubjectData a list of ETTSubject unmarshalled from an XML file.
     */
    private void moveSubjectsIntoDB(List<ETTSubject> SubjectData)
    {
        for(ETTSubject s : SubjectData)
        {
            Subjects.add(new Subject(s));
        }
    }

    /**
     * loads information of Teachers to the Database.
     *
     * @param TeacherData a list of ETTTeacher unmarshalled from an XML file.
     */
    private void moveTeachersIntoDB(List<ETTTeacher> TeacherData) throws TTException
    {
        for(ETTTeacher t : TeacherData)
        {
            Teachers.add(new Teacher(t, Subjects));
        }
    }

    /**
     * loads information of Classes to the Database.
     *
     * @param ClassroomData a list of ETTClass unmarshalled from an XML file.
     */
    private void moveClassroomsIntoDB(List<ETTClass> ClassroomData) throws TTException
    {
        for(ETTClass c : ClassroomData)
        {
            Classrooms.add(new Classroom(c, Subjects));
        }
        Classroom badClass = checkLearningHoursOfAllClasses();
        if(badClass != null)
            throw new TTException("Logical failure - Class " + badClass.getName() +" learning time is greater than allocated leaning time");
    }

    /**
     * loads information of Rules to the Database.
     *
     * @param RulesData a list of ETTRule unmarshalled from an XML file.
     */
    private void moveRulesIntoDB(List<ETTRule> RulesData) throws TTException
    {
        boolean type;
        TTRule rule;
        String conf;
        for(int i=0; i<RulesData.size(); i++)
        {
            type = RulesData.get(i).getType().equals("Hard") ? HARD : SOFT;
            try {
                rule = TTRule.valueOf(RulesData.get(i).getETTRuleId());
                if (AllRules.get(rule) != null)
                    throw new TTException("Logical failure - cannot add the same rule twice: " + rule);
                else if(rule == TTRule.Sequentiality)
                {
                    conf = RulesData.get(i).getETTConfiguration();
                }
                AllRules.put(rule, type);
            }
            catch (EnumConstantNotPresentException | IllegalArgumentException e){
                throw new TTException("XML error - unidentified rule name in file - " + RulesData.get(i).getETTRuleId());
            }
        }
    }

    /**
     * Sorts a list of items by their ID.
     *
     * @param data a list of objects that have IDs
     *
     * @return true iff all the objects had IDs starting at 1 and in ascending order.
     */
    public static boolean sortAndcheckSerials(List<? extends HasID> data) throws TTException
    {
        data.sort(new IDComperator());

        for(int i=1; i<=data.size(); i++)
        {
            if(data.get(i-1).getId() != i) {
                String clazz = data.get(i).getClass().getSimpleName();
                throw new TTException("XML error - " + clazz +" IDs are not in a valid order (1,2,3..)");
            }

        }
        return true;
    }

    public static int getDays() {
        return days;
    }

    public static int getHours() {
        return hours;
    }

    public static int getHardRulesWeight() {
        return hardRulesWeight;
    }

    public static List<Subject> getSubjects() {
        return Subjects;
    }

    public static List<Teacher> getTeachers() {
        return Teachers;
    }

    public static List<Classroom> getClassrooms() {
        return Classrooms;
    }

    public static int getSubjectsCount() {
        return Subjects.size();
    }

    public static int getTeachersCount() {
        return Teachers.size();
    }

    public static int getClassroomsCount() {
        return Classrooms.size();
    }

    public static Teacher getTeacherByID(int id) throws TTException
    {
        if(id<1 || id>Teachers.size())
            throw new TTException("Logical failure - tried to reach teacher by invalid id " + id);
        return Teachers.get(id-1);
    }

    public static Subject getSubjectByID(int id) throws TTException
    {
        if(id<1 || id>Subjects.size())
            throw new TTException("Logical failure - tried to reach subject by invalid id " + id);
        return Subjects.get(id-1);
    }

    public static Classroom getClassroomByID(int id) throws TTException
    {
        if(id<1 || id>Classrooms.size())
            throw new TTException("Logical failure - tried to reach classroom by invalid id " + id);
        return Classrooms.get(id-1);
    }

    /**
     * calculates all the hours needed to be learned in the timetable.
     *
     * @return the amount of hours.
     */
    public static int getAllHoursOfLearningNeeded()
    {
        int counter = 0;
        for(Classroom c : Classrooms)
        {
            for(Subject s : Subjects) {
                counter += c.getLearningHoursOfSubject(s);
            }
        }
        return counter;
    }

    /**
     * checks weather a rule is SOFT or HARD
     *
     * @return true iff the rule is HARD.
     */
    public static boolean getRuleType(TTRule r)
    {
        return AllRules.get(r);
    }

    /**
     * checks weather a class wants to learn more hours than we can have.
     *
     * @return the class if there is such, null otherwise.
     */
    public static Classroom checkLearningHoursOfAllClasses()
    {
        int timeWeHave = days * hours;
        for(Classroom c : Classrooms)
        {
            if(c.getNumberOfLearningHours() > timeWeHave)
                return c;
        }
        return null;
    }

    @Override
    public ETTDescriptor loadXMLFile(String XMLRoute) throws TTException{
        ETTDescriptor allData = null;

        try {
            InputStream IS = new FileInputStream(new File(XMLRoute));
            allData = Deserialize(IS);
            databaseIsLoaded = true;
        }
        catch (TTException e) {
            throw e;
        }
        catch (FileNotFoundException e) {
            throw new TTException("Resource error - XML file not found in " + XMLRoute);
        }
        catch (JAXBException e) {
            throw new TTException("Resource error - invalid JAXB execution");
        }
        return allData;
    }

    @Override
    public ETTDescriptor Deserialize(InputStream IS) throws JAXBException, TTException
    {
        JAXBContext jc = JAXBContext.newInstance(JAXB_GENERATED_IN);
        Unmarshaller u = jc.createUnmarshaller();
        ETTDescriptor AllData = (ETTDescriptor) u.unmarshal(IS);

        DTO saver = saveData();
        clearDB();
        try {

            ETTTimeTable TTData = AllData.getETTTimeTable();
            this.days = TTData.getDays();
            this.hours = TTData.getHours();

            List<ETTSubject> SubjectData = TTData.getETTSubjects().getETTSubject();
            moveSubjectsIntoDB(SubjectData);
            sortAndcheckSerials(Subjects);

            List<ETTTeacher> TeacherData = TTData.getETTTeachers().getETTTeacher();
            moveTeachersIntoDB(TeacherData);
            sortAndcheckSerials(Teachers);

            List<ETTClass> ClassroomsData = TTData.getETTClasses().getETTClass();
            moveClassroomsIntoDB(ClassroomsData);
            sortAndcheckSerials(Classrooms);

            List<ETTRule> RulesData = TTData.getETTRules().getETTRule();
            this.hardRulesWeight = TTData.getETTRules().getHardRulesWeight();
            moveRulesIntoDB(RulesData);
        }
        catch (Exception e)
        {
            loadData(saver);
            throw e;
        }

        return AllData;
    }

    @Override
    public DTO getSystemProperties()
    {
        return saveData();
    }

    @Override
    public DTO saveDataForSerialization() {
        return saveData();
    }

    @Override
    public void loadDataForSerialization(DTO saver) throws Exception {
        loadData(saver);
    }

    @Override
    public Set<Rule> getAllRules()
    {
        Set<Rule> res = new HashSet<>();
        res.addAll(AllRules.keySet());
        return res;
    }

    @Override
    public Map<Rule, Boolean> getAllRulesWithWeights() //Duplication
    {
        Map<Rule, Boolean> res = new HashMap<>();
        for(Map.Entry<TTRule, Boolean> e : AllRules.entrySet())
        {
            res.put(e.getKey(), e.getValue());
        }
        return res;
    }
}
