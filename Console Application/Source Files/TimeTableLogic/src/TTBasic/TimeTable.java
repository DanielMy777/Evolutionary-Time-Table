package TTBasic;

import java.io.Serializable;

//This class represents a simple timetable with its attributes.

public class TimeTable implements Serializable {

    public int days;
    public int hours;
    public int hardRulesWeight;

    public TimeTable(int days, int hours) {
        this.days = days;
        this.hours = hours;
        hardRulesWeight = 50;
    }

    public TimeTable(int days, int hours, int hardWeight) {
        this.days = days;
        this.hours = hours;
        hardRulesWeight = hardWeight;
    }

    public int getHardRulesWeight() {
        return hardRulesWeight;
    }

    public void setHardRulesWeight(int hardRulesWeight) {
        this.hardRulesWeight = hardRulesWeight;
    }
}
