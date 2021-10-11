package TTBasic;

import TTInfo.Classroom;
import TTInfo.Teacher;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

//This class represents a set of settings - use: settings in a certain day and hour in a time table.

public class SettingSet implements Serializable {

    private Set<Setting> SettingsInHour;

    public SettingSet() {
        SettingsInHour = new HashSet<>();
    }

    public boolean isEmpty()
    {
        return (SettingsInHour.size() == 0);
    }

    public boolean addSetting(Setting s)
    {
        return SettingsInHour.add(s);
    }

    public Set<Setting> getSettings()
    {
        return SettingsInHour;
    }

    public int getNumberOfTeacherAppearances(Teacher teacher)
    {
        int counter = 0;
        for(Setting s : SettingsInHour)
        {
            if(s.getTeacher() == teacher)
                counter++;
        }
        return counter;
    }

    public int getNumberOfClassroomAppearances(Classroom classroom)
    {
        int counter = 0;
        for(Setting s : SettingsInHour)
        {
            if(s.getClassroom() == classroom)
                counter++;
        }
        return counter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SettingSet that = (SettingSet) o;
        return Objects.equals(SettingsInHour, that.SettingsInHour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(SettingsInHour);
    }
}
