package TTBasic;

import Data.Property;
import Database.TTLoader;
import TTInfo.Classroom;
import TTInfo.Subject;
import TTInfo.Teacher;
import TTSolution.TTException;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

//This class represents a setting - a tupple in the Time Table.

public class Setting implements Property, Serializable {
    private int day;
    private int hour;
    private Classroom classroom;
    private Teacher teacher;
    private Subject subject;
    private boolean isBlank = true;

    public Setting() {}

    public Setting(int day, int hour, Classroom classroom, Teacher teacher, Subject subject) {
        setSetting(day,hour,classroom,teacher,subject);
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) throws TTException {
        if(isBlank == true)
            throw new TTException("Logical failure - cannot set the day (or any parameter) of a blank setting");
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) throws TTException {
        if(isBlank == true)
            throw new TTException("Logical failure - cannot set the hour (or any parameter) of a blank setting");
        this.hour = hour;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) throws TTException {
        if(isBlank == true)
            throw new TTException("Logical failure - cannot set the class (or any parameter) of a blank setting");
        this.classroom = classroom;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) throws TTException {
        if(isBlank == true)
            throw new TTException("Logical failure - cannot set the teacher (or any parameter) of a blank setting");
        this.teacher = teacher;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) throws TTException {
        if(isBlank == true)
            throw new TTException("Logical failure - cannot set subject day (or any parameter) of a blank setting");
        this.subject = subject;
    }

    public boolean isBlank(){
        return isBlank;
    }



    public void setSetting(int day, int hour, Classroom classroom, Teacher teacher, Subject subject) {
        this.day = day;
        this.hour = hour;
        this.classroom = classroom;
        this.teacher = teacher;
        this.subject = subject;
        isBlank = false;
    }

    @Override
    public int getComponent(int place)
    {
        switch (place) {
            case 0:
                return this.day;
            case 1:
                return this.hour;
            case 2:
                return this.classroom.getId();
            case 3:
                return this.teacher.getId();
            case 4:
                return this.subject.getId();
            default:
                return -1;
        }
    }

    @Override
    public void randomlyChangeComponent(String ComponentName) throws Exception
    {
        Random rand = new Random();
        int value;
        try {
            switch (ComponentName) {
                case "D":
                    value = rand.nextInt(TTLoader.getDays()) + 1;
                    this.day = value;
                    break;
                case "H":
                    value = rand.nextInt(TTLoader.getHours()) + 1;
                    this.hour = value;
                    break;
                case "C":
                    value = rand.nextInt(TTLoader.getClassroomsCount()) + 1;
                    this.setClassroom(TTLoader.getClassroomByID(value));
                    break;
                case "T":
                    value = rand.nextInt(TTLoader.getTeachersCount()) + 1;
                    this.setTeacher(TTLoader.getTeacherByID(value));
                    break;
                case "S":
                    value = rand.nextInt(TTLoader.getSubjectsCount()) + 1;
                    this.setSubject(TTLoader.getSubjectByID(value));
                    break;
                default:
                    throw new TTException("Invalid component to change");
            }
        }
        catch(TTException e) {throw new Exception(e.getMessage());}
    }

    @Override
    public int getComponent(String ComponentName)
    {
        switch (ComponentName) {
            case "D":
                return this.day;
            case "H":
                return this.hour;
            case "T":
                return this.classroom.getId();
            case "C":
                return this.teacher.getId();
            case "S":
                return this.subject.getId();
            default:
                return -1;
        }
    }

    @Override
    public int getComponentLimit(String ComponentName)
    {
        switch (ComponentName) {
            case "D":
                return this.day;
            case "H":
                return this.hour;
            case "T":
                return TTLoader.getClassroomsCount();
            case "C":
                return TTLoader.getTeachersCount();
            case "S":
                return TTLoader.getSubjectsCount();
            default:
                return -1;
        }
    }

    @Override
    public String toString() {
        return "<" +
                day +
                "," + hour +
                "," + classroom.getId() +
                "," + teacher.getId() +
                "," + subject.getId() +
                '>';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Setting setting = (Setting) o;
        return day == setting.day && hour == setting.hour && isBlank == setting.isBlank && Objects.equals(classroom, setting.classroom) && Objects.equals(teacher, setting.teacher) && Objects.equals(subject, setting.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, hour, classroom, teacher, subject, isBlank);
    }

    @Override
    public Object clone()
    {
        return new Setting(this.day, this.hour, this.classroom, this.teacher, this.subject);
    }
}
