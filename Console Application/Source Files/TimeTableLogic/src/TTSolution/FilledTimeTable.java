package TTSolution;

import Data.Property;
import Data.Rule;
import Data.Solution;
import Database.TTFactory;
import Database.TTLoader;
import Database.TTRule;
import TTBasic.Setting;
import TTBasic.SettingSet;
import TTBasic.TimeTable;
import TTInfo.Classroom;
import TTInfo.Subject;
import TTInfo.Teacher;

import java.io.Serializable;
import java.util.*;

//This class represents a solution for the Time Table problem.

public class FilledTimeTable extends TimeTable implements Solution, Serializable {

    private SettingSet TTSetting[][];
    private double fitness;

    public FilledTimeTable(int days, int hours) {
        super(days, hours);
        TTSetting = new SettingSet[days][hours];
        for(int i=0;i<days;i++)
        {
            for(int j=0; j<hours; j++)
            {
                TTSetting[i][j] = new SettingSet();
            }
        }
        fitness = -1;
    }

    public FilledTimeTable(int days, int hours, int hardWeight, SettingSet[][] TTSetting) {
        super(days, hours, hardWeight);
        this.TTSetting = TTSetting;
        fitness = -1;
    }

    /**
     * Adds a new setting (tupple) to the time table.
     * Give it the fitting day, hour, class, teacher, subject of the setting.
     */
    public void AddSetting(int day, int hour, Classroom classroom, Teacher teacher, Subject subject) {
        Setting newSetting = new Setting(day,hour,classroom,teacher,subject);
        TTSetting[day-1][hour-1].addSetting(newSetting);
    }

    /**
     * Adds a new setting (tupple) to the time table.
     *
     * @param s the setting to add
     *
     * @return true iff the setting was added (not a duplication)
     */
    public boolean AddSetting(Setting s) {
        int day = s.getDay();
        int hour = s.getHour();
        return TTSetting[day-1][hour-1].addSetting(s);
    }

    /**
     * Get the settings of a certain cell in the tame table.
     *
     * @param day the day of the cell
     * @param hour the hour of the cell
     *
     * @return the settings as a set (SettingSet)
     */
    public SettingSet getSettingsInDay(int day, int hour)
    {
        return TTSetting[day-1][hour-1];
    }

    /**
     * Get the settings of the time table solution.
     *
     * @return an ArrayList of settings.
     */
    public ArrayList<Setting> getAllSettingsOfTimeTable(){
        ArrayList<Setting> res = new ArrayList<>();
        for(int i=0;i<days;i++)
        {
            for(int j=0; j<hours; j++)
            {
                Set<Setting> currHour = TTSetting[i][j].getSettings();
                for(Setting s : currHour) {
                    res.add(s);
                }
            }
        }
        return res;
    }

    /**
     * Get the settings of the time table solution - blocked in sets regarding their cell.
     *
     * @return an ArrayList of SettingSet.
     */
    public ArrayList<SettingSet> getSettingsAsSetArray()
    {
        ArrayList<SettingSet> resAsList = new ArrayList<>(days*hours);
        for(int i=0;i<days;i++)
        {
            for(int j=0; j<hours; j++)
            {
                resAsList.add(TTSetting[i][j]);
            }
        }
        return resAsList;
    }

    /**
     * Get the settings of the time table solution - for a certain class
     *
     * @param classroom the class to find.
     *
     * @return an ArrayList of settings.
     */
    public ArrayList<Setting> getAllSettingsOfClassroom(Classroom classroom)
    {
        ArrayList<Setting> res = new ArrayList<>();
        for(int i=0;i<days;i++)
        {
            for(int j=0; j<hours; j++)
            {
                for(Setting s : TTSetting[i][j].getSettings())
                {
                    if(s.getClassroom().equals(classroom))
                        res.add(s);
                }
            }
        }
        return res;
    }

    /**
     * Get the settings of the time table solution - for a certain teacher
     *
     * @param teacher the teacher to find.
     *
     * @return an ArrayList of settings.
     */
    public ArrayList<Setting> getAllSettingsOfTeacher(Teacher teacher)
    {
        ArrayList<Setting> res = new ArrayList<>();
        for(int i=0;i<days;i++)
        {
            for(int j=0; j<hours; j++)
            {
                for(Setting s : TTSetting[i][j].getSettings())
                {
                    if(s.getTeacher().equals(teacher))
                        res.add(s);
                }
            }
        }
        return res;
    }

    /**
     * Get the settings of the time table solution - organized by teacher IDs
     *
     * @return an ArrayList of ArrayList of settings - ArrayList for every teacher.
     */
    public ArrayList<ArrayList<Setting>> getAllSettingsOfAllTeachers()
    {
        ArrayList<ArrayList<Setting>> res = new ArrayList<>();
        for(int i=1; i<=TTLoader.getTeachersCount(); i++) {
            try {
                Teacher t = TTLoader.getTeacherByID(i);
                res.add(getAllSettingsOfTeacher(t));
            }
            catch (Exception e) {res.add(null);}
        }
        return res;
    }

    /**
     * Get the settings of the time table solution - organized by class IDs
     *
     * @return an ArrayList of ArrayList of settings - ArrayList for every class.
     */
    public ArrayList<ArrayList<Setting>> getAllSettingsOfAllClassrooms()
    {
        ArrayList<ArrayList<Setting>> res = new ArrayList<>();
        for(int i=1; i<=TTLoader.getClassroomsCount(); i++) {
            try {
                Classroom c = TTLoader.getClassroomByID(i);
                res.add(getAllSettingsOfClassroom(c));
            }
            catch (Exception e) {res.add(null);}
        }
        return res;
    }

    /**
     * NOT IN USE - for testing reasons only
     * prints the timetable as a raw setting column.
     */
    private void printAsSettingArray()
    {
        ArrayList<Setting> arr = this.getAllSettingsOfTimeTable();
        for(Setting s : arr)
        {
            System.out.println(s);
        }
    }


    @Override
    public ArrayList<Property> getSolutionAsPropertyArray() {
        ArrayList<Property> res = new ArrayList<>();
        for(int i=0;i<days;i++)
        {
            for(int j=0; j<hours; j++)
            {
                Set<Setting> currHour = TTSetting[i][j].getSettings();
                for(Setting s : currHour) {
                    res.add((Setting)s.clone());
                }
            }
        }
        return res;
    }

    @Override
    public double getFitness() throws Exception
    {
        if(fitness < 0)
            throw new TTException("Logical error - trying to fetch unevaluated fitness of a solution");
        return fitness;
    }

    @Override
    public void printSolution(Object... params)
    {
        printAsSettingArray();
    }

    @Override
    public double evaluateFitness(Collection<Rule> rules){
        double fitness = 0;
        boolean type;
        double hardRulesSum=0, softRulesSum=0;
        double hardRulesCount=0, softRulesCount=0;
        double hardRulesAvg, softRulesAvg;
        for(Rule r : rules)
        {
            try{
                type = TTLoader.getRuleType((TTRule) r);
                if(type == TTLoader.HARD)
                {
                    hardRulesCount++;
                    hardRulesSum += r.eval(this);
                }
                else
                {
                    softRulesCount++;
                    softRulesSum += r.eval(this);
                }
            }
            catch (Exception e) {e.printStackTrace();}

        }
        hardRulesAvg = hardRulesSum/hardRulesCount;
        softRulesAvg = softRulesSum/softRulesCount;
        this.setHardRulesWeight(TTLoader.getHardRulesWeight());
        fitness += (hardRulesAvg * this.getHardRulesWeight())/100;
        fitness += (softRulesAvg * (100 - this.getHardRulesWeight()))/100;
        this.fitness = Math.max(fitness, 0);
        return fitness;
    }

    @Override
    public void randomlyChangeProperty(int propertyIDX, String ComponentName) throws Exception
    {
        Property s = this.getSolutionAsPropertyArray().get(propertyIDX);
        s.randomlyChangeComponent(ComponentName);
    }

    @Override
    public boolean addRandomProperty()
    {
        Random rand = new Random();
        int currentTupples = this.getSolutionAsPropertyArray().size();
        if(currentTupples >= TTLoader.getAllHoursOfLearningNeeded() + 5)
            return false;
        Setting s = TTFactory.createRandomSetting();
        TTSetting[s.getDay()-1][s.getHour()-1].addSetting(s);
        return true;
    }

    @Override
    public boolean removeRandomProperty()
    {
        int counter = 0;
        Random rand = new Random();
        int currentTupples = this.getSolutionAsPropertyArray().size();
        if(currentTupples <= days*hours)
            return false;
        int tuppleToChange  = rand.nextInt(currentTupples);
        for(int i=0; i<days; i++)
        {
            for(int j=0; j<hours; j++)
            {
                for(Setting s : TTSetting[i][j].getSettings()) {
                    counter++;
                    if(counter == tuppleToChange) {
                        TTSetting[i][j].getSettings().remove(s);
                        return true;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int compareTo(Object o) {
        if(!(o instanceof FilledTimeTable))
            return 1;
        FilledTimeTable other = (FilledTimeTable) o;
        Set<Rule> TTRules = new TTLoader().getAllRules();
        if(this.evaluateFitness(TTRules) > other.evaluateFitness(TTRules)) return 1;
        if(this.evaluateFitness(TTRules) < other.evaluateFitness(TTRules)) return -1;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilledTimeTable table = (FilledTimeTable) o;
        for(int i=0; i<this.days; i++)
        {
            for(int j=0; j<this.hours; j++)
            {
                if(this.TTSetting[i][j].equals(table.TTSetting[i][j]))
                    return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(TTSetting);
    }
}
