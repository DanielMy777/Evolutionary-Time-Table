package TTInfo;


import TTSolution.TTException;
import com.sun.org.apache.xpath.internal.operations.Bool;
import generated.ETTTeacher;
import generated.ETTTeaches;
import sun.invoke.empty.Empty;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//This class Teacher a Subject in the time table.

public class Teacher extends HasID implements Serializable {

    //super : final int id;
    private String name;
    private int workingHours;
    private Set<Subject> teachableSubjects;

    public Teacher(int id, String name, int workingHours) {
        super(id);
        this.name = name;
        this.workingHours = workingHours;
        teachableSubjects = new HashSet<>();

    }

    //Construct a Subject from ETTTeacher object.
    //Given a list of subjects, the teachable subjects will be updated accordingly.
    public Teacher(ETTTeacher t, List<Subject> Subjects) throws TTException
    {
        this(t.getId(), t.getETTName(), 10);
        int subjectID;
        List<ETTTeaches> teaches = t.getETTTeaching().getETTTeaches();
        for(int i=0; i<teaches.size(); i++)
        {
            try {
                subjectID = teaches.get(i).getSubjectId() - 1;
                teachableSubjects.add(Subjects.get(subjectID));
            }
            catch (IndexOutOfBoundsException e){
                throw new TTException("XML error - Unidentified subject to teach for teacher " + this.name);
            }
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWorkingHours() {
        return workingHours;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWorkingHours(int workingHours) {
        this.workingHours = workingHours;
    }

    public void addTeachableSubject(Subject subToAdd) {
        teachableSubjects.add(subToAdd);
    }

    public Set<Subject> getTeachableSubjects() {
        return teachableSubjects;
    }

    public boolean IsTeaching(Subject sub) {return teachableSubjects.contains(sub); }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", workingHours=" + workingHours +
                ", teachableSubjects=" + teachableSubjects +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return (super.equals(o));
    }
}
