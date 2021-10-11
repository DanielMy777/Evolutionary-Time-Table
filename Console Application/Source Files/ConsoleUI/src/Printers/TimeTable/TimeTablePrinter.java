package Printers.TimeTable;

import Database.DTO;
import Database.TTRule;
import Printers.Printer;
import Design.Table;
import TTBasic.Setting;
import TTBasic.SettingSet;
import TTInfo.Classroom;
import TTInfo.Subject;
import TTInfo.Teacher;
import TTSolution.FilledTimeTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//this class knows hot to print a timetable and timetable system to the screen

public class TimeTablePrinter extends Printer {

    /**
     * Print the time table problem configuration to the screen
     *
     * @param data - the configuration as a DTO
     */
    @Override
    public void PrintProperties(DTO data) {
        System.out.println("Time Table Properties:\n");
        ArrayList<Subject> subjects = (ArrayList<Subject>) data.getData("Subjects");
        Table subjectTable = new Table("Subjects","ID", "Name");
        for(Subject s : subjects)
            subjectTable.addRow(String.valueOf(s.getId()), s.getName());

        ArrayList<Teacher> Teachers = (ArrayList<Teacher>) data.getData("Teachers");
        Table TeacherTable = new Table("Teachers","ID", "Name", "Teachable Subjects");
        for(Teacher t : Teachers) {
            TeacherTable.addRow(String.valueOf(t.getId()), t.getName(), "");
            for(Subject s : t.getTeachableSubjects())
                TeacherTable.addRow("", "", "Subject #" + s.getId() + " - " + s.getName());
        }

        ArrayList<Classroom> Classes = (ArrayList<Classroom>) data.getData("Classrooms");
        Table ClassroomTable = new Table("Classrooms","ID", "Name", "Learnable Subjects", "Hours Needed");
        for(Classroom c : Classes) {
            ClassroomTable.addRow(String.valueOf(c.getId()), c.getName(), "", "");
            for(Map.Entry<Subject, Integer> e : c.getLearningHoursOfAllSubjects().entrySet())
                ClassroomTable.addRow("", "",
                        "Subject #" + e.getKey().getId() + " - " + e.getKey().getName(),
                        String.valueOf(e.getValue()));

        }

        HashMap<TTRule, Boolean> Rules = (HashMap<TTRule, Boolean>) data.getData("Rules");
        Table RulesTable = new Table("Rules", "Name", "Hard / Soft");
        for(Map.Entry<TTRule, Boolean> r : Rules.entrySet()) {
            RulesTable.addRow(r.getKey().toString(), r.getValue() ? "Hard" : "Soft");
        }

        subjectTable.printTable();
        TeacherTable.printTable();
        ClassroomTable.printTable();
        RulesTable.printTable();
        System.out.println("\n");
    }

    /**
     * Print the time table solution to the screen
     *
     * @param data - a DTO thats holds the solution, and how to print it
     */
    @Override
    public void PrintSolution(DTO data) {
        System.out.println("The Solution is: ");
        String wayToShow = (String) data.getData("Configuration");
        FilledTimeTable solution = (FilledTimeTable) data.getData("Solution");
        switch (wayToShow)
        {
            case "RAW":
                PrintSolutionRaw(solution);
                break;
            case "TEACHER":
                printSolutionByTeacher(solution);
                break;
            case "CLASS":
                printSolutionByClass(solution);
                break;
            default:
                break;
        }
    }

    public void PrintSolutionRaw(FilledTimeTable solution)
    {
        ArrayList<Setting> toPrint = solution.getAllSettingsOfTimeTable();
        for(Setting s : toPrint)
        {
            System.out.println(s);
        }
        System.out.println("---------------------------------------------");
    }

    public void printSolutionByTeacher(FilledTimeTable solution)
    {
        int i=1;
        ArrayList<ArrayList<Setting>> allTeachers = solution.getAllSettingsOfAllTeachers();
        for(ArrayList<Setting> arr : allTeachers)
        {
            System.out.println("Weekly Time Table for teacher #" + i);
            System.out.println("--------------------------------");
            PrintAsTable(solution.days, solution.hours, getAsSettingSets(solution.days, solution.hours, arr));
            i++;
        }
    }

    public void printSolutionByClass(FilledTimeTable solution)
    {
        int i=1;
        ArrayList<ArrayList<Setting>> allClassrooms = solution.getAllSettingsOfAllClassrooms();
        for(ArrayList<Setting> arr : allClassrooms)
        {
            System.out.println("Weekly Time Table for classroom #" + i);
            System.out.println("--------------------------------");
            PrintAsTable(solution.days, solution.hours, getAsSettingSets(solution.days, solution.hours, arr));
            i++;
        }
    }

    private void PrintAsTable(int days, int hours, SettingSet[][] settingsByHour)
    {
        int idx = 0;
        Setting curr;
        String[] headers = new String[days+1];
        headers[0] = "Hour / Day";
        for(int i=1; i<=days; i++){
            headers[i] = "Day " + String.valueOf(i);
        }
        Table myTable = new Table("<D,H,C,T,S> = <D(Day),H(Hour),C(Class),T(Teacher),S(Subject)> ", headers);
        for(int i=1; i<= hours; i++)
        {
            String[] row = new String[days+1];
            row[0] = String.valueOf("Hour " + i);
            for(int j=1; j<=days; j++)
            {
                row[j] = "";
                for(Setting s : settingsByHour[i - 1][j - 1].getSettings())
                {
                    row[j] += s.toString();
                }
            }
            myTable.addRow(row);
        }
        myTable.printTable();
    }

    private SettingSet[][] getAsSettingSets(int days, int hours, ArrayList<Setting> arr)
    {
        SettingSet[][] res = new SettingSet[hours][days];
        for(int i=0; i<hours; i++)
            for(int j=0; j<days; j++)
                res[i][j] = new SettingSet();
        for(Setting s : arr)
        {
            res[s.getHour() - 1][s.getDay() - 1].addSetting(s);
        }
        return res;
    }
}
