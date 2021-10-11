package Database;

import Data.Property;
import Data.Rule;
import Data.Solution;
import TTBasic.Setting;
import TTBasic.SettingSet;
import TTInfo.Classroom;
import TTInfo.Subject;
import TTInfo.Teacher;
import TTSolution.FilledTimeTable;

import java.io.Serializable;
import java.util.ArrayList;

//This enum contains all Time Table rules, with a way to evaluate then given a Time Table.

public enum TTRule implements Rule, Serializable {
    TeacherIsHuman{
        public double eval(Solution tt){
            double score = 100;
            double faultMultiplier = (double)100 / TTLoader.getAllHoursOfLearningNeeded();
            if(!(tt instanceof FilledTimeTable))
                return 0;
            else
            {
                Teacher currTeacher;
                FilledTimeTable table = (FilledTimeTable)tt;
                int numOfTeachers = TTLoader.getTeachersCount();
                int currMatches;
                ArrayList<SettingSet> tableAsArray = table.getSettingsAsSetArray();
                for(SettingSet currHour : tableAsArray)
                {
                    for(int i=1; i<=numOfTeachers; i++)
                    {
                        try {
                            currTeacher = TTLoader.getTeacherByID(i);
                            currMatches = currHour.getNumberOfTeacherAppearances(currTeacher);
                            if (currMatches > 1) {
                                score -= currMatches * faultMultiplier;
                            }
                        }
                        catch (Exception e) {}
                    }
                }
            }
            return score;
        }
    },
    Singularity{
        public double eval(Solution tt){
            double score = 100;
            double faultMultiplier = (double)100 / TTLoader.getAllHoursOfLearningNeeded();
            if(!(tt instanceof FilledTimeTable))
                return 0;
            else
            {
                Classroom currClassroom;
                FilledTimeTable table = (FilledTimeTable)tt;
                int numOfClasses = TTLoader.getClassroomsCount();
                int currMatches;
                ArrayList<SettingSet> tableAsArray = table.getSettingsAsSetArray();
                for(SettingSet currHour : tableAsArray)
                {
                    for(int i=1; i<=numOfClasses; i++)
                    {
                        try {
                            currClassroom = TTLoader.getClassroomByID(i);
                            currMatches = currHour.getNumberOfClassroomAppearances(currClassroom);
                            if (currMatches > 1) {
                                score -= currMatches * faultMultiplier;
                            }
                        }
                        catch (Exception e) {}
                    }
                }
            }
            return score;
        }
    },
    Knowledgeable{
        public double eval(Solution tt) {
            double score = 100;
            double faultMultiplier = (double)100 / TTLoader.getAllHoursOfLearningNeeded();
            if (!(tt instanceof FilledTimeTable))
                return 0;
            else {
                Teacher teacher;
                Subject subject;
                FilledTimeTable table = (FilledTimeTable) tt;
                ArrayList<SettingSet> tableAsArray = table.getSettingsAsSetArray();
                for (SettingSet currHour : tableAsArray) {
                    for (Setting s : currHour.getSettings()) {
                        teacher = s.getTeacher();
                        subject = s.getSubject();
                        if (!teacher.IsTeaching(subject)) {
                            score -= faultMultiplier;
                        }
                    }
                }
            }
            return score;
        }
    },
    DayOffTeacher{
        public double eval(Solution tt){
            double score = 100;
            if(!(tt instanceof FilledTimeTable))
                return 0;
            else
            {
                boolean[][] isTeaching = new boolean[TTLoader.getTeachersCount()][TTLoader.getDays()];
                FilledTimeTable table = (FilledTimeTable)tt;
                boolean hasDayOff;
                int numOfTeachers = TTLoader.getTeachersCount();
                ArrayList<Setting> tableAsArray = table.getAllSettingsOfTimeTable();
                for(int i=0; i<numOfTeachers; i++)
                {
                    for(int j=0; j<TTLoader.getDays(); j++)
                    {
                        isTeaching[i][j] = false;
                    }
                }
                for(Setting s : tableAsArray)
                {
                    isTeaching[s.getTeacher().getId()-1][s.getDay()-1] = true;
                }
                for(int i=0; i<numOfTeachers; i++)
                {
                    hasDayOff = false;
                    for(int j=0; j<TTLoader.getDays(); j++)
                    {
                        if(!isTeaching[i][j])
                        {
                            hasDayOff = true;
                        }
                    }
                    if(!hasDayOff)
                        score -= (double)100/(numOfTeachers);
                }
            }
            return score;
        }
    },
    Sequentiality {
        public double eval(Solution tt){
            double score = 100;
            double faultMultiplier = (double)100 / TTLoader.getAllHoursOfLearningNeeded();
            if(!(tt instanceof FilledTimeTable))
                return 0;
            else
            {
                int currDay, currHour, currSeq;
                Subject currSubject;
                int maxSeq = ConfigurationParams;
                if(maxSeq <=0) return 100;
                FilledTimeTable table = (FilledTimeTable)tt;
                int numOfClasses = TTLoader.getClassroomsCount();
                ArrayList<Setting> classSettings;
                for(int i=1; i<=numOfClasses; i++)
                {
                    currDay = currHour = currSeq = 0;
                    currSubject = null;
                    try {
                        classSettings = table.getAllSettingsOfClassroom(TTLoader.getClassroomByID(i));
                    }
                    catch (Exception e) {continue;}
                    for(Setting s : classSettings)
                    {
                        if (s.getDay() == currDay && s.getHour() == currHour+1 && s.getSubject().equals(currSubject)) {
                            currSeq++;
                            currHour++;
                            if(currSeq > maxSeq)
                                score -= faultMultiplier * Math.pow(2, currSeq);
                        }
                        else
                        {  currDay = s.getDay();
                           currHour = s.getHour();
                           currSeq = 1;
                           currSubject = s.getSubject();
                        }
                    }
                }
            }
            return score;
        }
    },
    DayOffClass{
        public double eval(Solution tt){
            double score = 100;
            if(!(tt instanceof FilledTimeTable))
                return 0;
            else
            {
                boolean[][] isLearning = new boolean[TTLoader.getClassroomsCount()][TTLoader.getDays()];
                FilledTimeTable table = (FilledTimeTable)tt;
                boolean hasDayOff;
                int numOfClasses = TTLoader.getClassroomsCount();
                ArrayList<Setting> tableAsArray = table.getAllSettingsOfTimeTable();
                for(int i=0; i<numOfClasses; i++)
                {
                    for(int j=0; j<TTLoader.getDays(); j++)
                    {
                        isLearning[i][j] = false;
                    }
                }
                for(Setting s : tableAsArray)
                {
                    isLearning[s.getClassroom().getId()-1][s.getDay()-1] = true;
                }
                for(int i=0; i<numOfClasses; i++)
                {
                    hasDayOff = false;
                    for(int j=0; j<TTLoader.getDays(); j++)
                    {
                        if(!isLearning[i][j])
                        {
                            hasDayOff = true;
                        }
                    }
                    if(!hasDayOff)
                        score -= (double)100/numOfClasses;
                }
            }
            return score;
        }
    },
    WorkingHoursPreference{
        public double eval(Solution tt){
            double score = 100;
            if (!(tt instanceof FilledTimeTable))
                return 0;
            else {
                int numOfTeachers = TTLoader.getTeachersCount();
                int[] hoursTeaching = new int[numOfTeachers];
                for(int i=0; i<hoursTeaching.length; i++)
                    hoursTeaching[i] = 0;
                FilledTimeTable table = (FilledTimeTable)tt;
                ArrayList<Setting> tableAsArray = table.getAllSettingsOfTimeTable();
                try{
                    for(Setting s : tableAsArray) {
                        hoursTeaching[s.getTeacher().getId()-1]++;
                    }

                    for(int i=0; i<numOfTeachers; i++)
                    {
                        score -= Math.abs(hoursTeaching[i] - TTLoader.getTeacherByID(i+1).getWorkingHours());
                    }
                }
                catch (Exception e) {}
            }
            return score;
            }
        },
    Satisfactory{
        public double eval(Solution tt){
            double score = 100;
            if (!(tt instanceof FilledTimeTable))
                return 0;
            else {
                int[] hoursLearning = new int[TTLoader.getSubjectsCount()];
                Classroom currClassroom;
                Subject currSubject;
                FilledTimeTable table = (FilledTimeTable)tt;
                int numOfClasses = TTLoader.getClassroomsCount();
                ArrayList<Setting> tableAsArray = table.getAllSettingsOfTimeTable();
                for(int i=1; i<=numOfClasses; i++)
                {
                    for(int j=0; j<hoursLearning.length; j++)
                        hoursLearning[j] = 0;
                    try{
                        currClassroom = TTLoader.getClassroomByID(i);
                        for(Setting s : tableAsArray)
                        {
                            if(s.getClassroom() == currClassroom)
                                hoursLearning[s.getSubject().getId()-1]++;
                        }
                        for(int j=0; j<hoursLearning.length; j++) {
                            currSubject = TTLoader.getSubjectByID(j+1);
                            score -= Math.abs(currClassroom.getLearningHoursOfSubject(currSubject) - hoursLearning[j]);
                        }
                    }
                    catch (Exception e) {}

                }
            }
            return score;
        }
    };

    String Configuration;
    int ConfigurationParams;

    public void setConfiguration(String conf)
    {
        Configuration = conf;
        ConfigurationParams = Integer.parseInt(conf.split("=")[1]);
    }
}
