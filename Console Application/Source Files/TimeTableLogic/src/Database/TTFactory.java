package Database;

import Data.Property;
import Data.Solution;
import Data.SolutionFactory;
import TTBasic.Setting;
import TTInfo.Classroom;
import TTInfo.Subject;
import TTInfo.Teacher;
import TTSolution.FilledTimeTable;
import TTSolution.TTException;

import java.util.ArrayList;
import java.util.Random;

//This class acts as a factory - for the solution type pf FilledTimeTable

public class TTFactory implements SolutionFactory<FilledTimeTable> {

    private static Random rand = new Random();

    /**
     * Generates a random timetable with random tupples
     *
     * @return The spoken timetable.
     */
    public static FilledTimeTable createRandomTimeTable() throws TTException
    {
        if(!TTLoader.databaseIsLoaded)
            return null;
        int day;
        int hour;
        Classroom classroom;
        Teacher teacher;
        Subject subject;
        Setting s;
        FilledTimeTable table = new FilledTimeTable(TTLoader.getDays(),TTLoader.getHours());
        int numOfSettings = TTLoader.getAllHoursOfLearningNeeded();
        numOfSettings += (rand.nextInt(6) - 3); //+-3
        for(int i=0; i<numOfSettings; i++)
        {
            table.AddSetting(s = createRandomSetting());
        }
        table.setHardRulesWeight(TTLoader.hardRulesWeight);
        return table;
    }

    /**
     * Generates timetable with considerate tupples - to fit the teacher's preferences.
     *
     * @return The spoken timetable.
     */
    public static FilledTimeTable createConsiderateTimeTable() throws TTException
    {
        if(!TTLoader.databaseIsLoaded)
            return null;
        int day;
        int hour;
        int TeacherID = 0;
        int[] LearningHoursLeftForClass = getClassHours();
        int[] TeachingHoursLeftForTeacher = getTeacherHours();
        Classroom classroom;
        Teacher teacher;
        Subject subject;
        Setting s;
        FilledTimeTable table = new FilledTimeTable(TTLoader.getDays(),TTLoader.getHours());
        int numOfSettings = TTLoader.getAllHoursOfLearningNeeded();
        numOfSettings += (rand.nextInt(6) - 3); //+-3
        for(int i=0; i<numOfSettings; i++)
        {
            if(rand.nextInt(15)==0) //probability of 1/15
            {
                table.AddSetting(s = createRandomSetting());
            }
            else
            {
                classroom = TTLoader.getClassroomByID(getMaxIndexInArrayOfHours(LearningHoursLeftForClass));
                teacher = TTLoader.getTeacherByID(getMaxIndexInArrayOfHours(TeachingHoursLeftForTeacher));
                subject = classroom.getCommonSubject(teacher);
                if(subject == null) {
                    table.AddSetting(s = createRandomSetting());
                }
                else
                {
                    do {
                        day = rand.nextInt(TTLoader.getDays()) + 1;
                        hour = rand.nextInt(TTLoader.getHours()) + 1;
                    }while(table.getSettingsInDay(day,hour).getNumberOfTeacherAppearances(teacher) != 0
                    || table.getSettingsInDay(day,hour).getNumberOfClassroomAppearances(classroom) != 0);
                    s = new Setting(day, hour, classroom,teacher,subject);
                    table.AddSetting(s);
                }

            }
            LearningHoursLeftForClass[s.getClassroom().getId()-1]--;
            TeachingHoursLeftForTeacher[s.getTeacher().getId()-1]--;
        }
        table.setHardRulesWeight(TTLoader.hardRulesWeight);
        return table;
    }

    /**
     * Generates a random tupple - which is a setting for the timetable
     *
     * @return The spoken setting.
     */
    public static Setting createRandomSetting()
    {
        int day;
        int hour;
        Classroom classroom;
        Teacher teacher;
        Subject subject;
        Setting res = null;
        try {
            day = rand.nextInt(TTLoader.getDays()) + 1;
            hour = rand.nextInt(TTLoader.getHours()) + 1;
            classroom = TTLoader.getClassroomByID(rand.nextInt(TTLoader.getClassroomsCount()) + 1);
            teacher = TTLoader.getTeacherByID(rand.nextInt(TTLoader.getTeachersCount()) + 1);
            subject = TTLoader.getSubjectByID(rand.nextInt(TTLoader.getSubjectsCount()) + 1);
            res = new Setting(day,hour,classroom,teacher,subject);
        }
        catch (Exception e)
        {
            System.out.println("something went wrong");
        }
        return res;
    }

    /**
     * calculates the amount of learning hours a class needs.
     *
     * @return An array of learning hours. arr[i] = learning hours for class i+1.
     */
    private static int[] getClassHours()
    {
        int[] LearningHoursForClass = new int[TTLoader.getClassroomsCount()];
        for(int i=0; i<TTLoader.getClassroomsCount(); i++)
        {
            try{
                LearningHoursForClass[i] = TTLoader.getClassroomByID(i+1).getNumberOfLearningHours();
            }
            catch (Exception e) {}
        }
        return LearningHoursForClass;
    }

    /**
     * calculates the amount of teaching hours a teacher wants.
     *
     * @return An array of teaching hours. arr[i] = teaching hours for teacher i+1.
     */
    private static int[] getTeacherHours()
    {
        int[] TeachingHoursForTeacher = new int[TTLoader.getTeachersCount()];
        for(int i=0; i<TTLoader.getTeachersCount(); i++)
        {
            try{
                TeachingHoursForTeacher[i] = TTLoader.getTeacherByID(i+1).getWorkingHours();
            }
            catch (Exception e) {}
        }
        return TeachingHoursForTeacher;
    }

    /**
     * finds the class that needs most hours.
     *
     * @param arr array of hours needed for the class.
     *
     * @return the class ID that needs the most hours in the array.
     */
    private static int getMaxIndexInArrayOfHours(int[] arr)
    {
        int maxID = -1;
        int max = 0;
        for(int i=0; i<arr.length; i++)
        {
            if(arr[i]>max)
            {
                maxID = i+1;
                max = arr[i];
            }
        }
        if(maxID == -1)
        {
            maxID = rand.nextInt(arr.length) + 1;
        }
        return maxID;
    }

    @Override
    public Property generateProperty()
    {
        return createRandomSetting();
    }


    @Override
    public FilledTimeTable generateSolution() {
        try {
            return createRandomTimeTable();
        }
        catch (Exception e)
        {
            System.out.println("Failed to generate solution, try again...");
            return null;
        }
    }

    @Override
    public FilledTimeTable createSolution(ArrayList<Property> props) {
        FilledTimeTable res = new FilledTimeTable(TTLoader.days, TTLoader.hours);
        for(Property p : props)
        {
            if(p instanceof Setting)
                while(!res.AddSetting((Setting)p))
                {
                    p = createRandomSetting();
                }
        }
        return res;
    }

}
