package TTInfo;


import TTSolution.TTException;
import generated.ETTClass;
import generated.ETTStudy;

import java.io.Serializable;
import java.util.*;

//This class Classroom a Subject in the time table.

public class Classroom extends HasID implements Serializable {

    //super : final int id;
    private String name;
    private Map<Subject, Integer> learningHours = new Hashtable<>();

    public Classroom(int id, String name) {
        super(id);
        this.name = name;
    }

    //Construct a Subject from ETTClass object.
    //Given a list of subjects, the learning hours will be updated accordingly.
    public Classroom(ETTClass c, List<Subject> Subjects) throws TTException
    {
        this(c.getId(), c.getETTName());
        int SubjectID;
        int subjectHours;
        List<ETTStudy> studying = c.getETTRequirements().getETTStudy();
        for(int i=0; i<studying.size(); i++)
        {
            try {
                SubjectID = studying.get(i).getSubjectId();
                subjectHours = studying.get(i).getHours();
                learningHours.put(Subjects.get(SubjectID-1), subjectHours);
            }
            catch (IndexOutOfBoundsException e) {
                throw new TTException("XML error - Unidentified subject to learn for class " + this.name);
            }
        }
    }

    //Give each subject a leaning hour of 0
    public void InitHours(Set<Subject> Subjects)
    {
        for(Subject sub : Subjects) {
            learningHours.put(sub, 0);
        }
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<Subject, Integer> getLearningHoursOfAllSubjects() {
        return learningHours;
    }

    public int getLearningHoursOfSubject(Subject sub) {
        Object res = learningHours.get(sub);
        if(res == null)
            return 0;
        return (int)res;

    }

    public Set<Subject> getLearnableSubjects()
    {
        return learningHours.keySet();
    }

    public void alterLearningHours(Subject sub, int hours) {
        learningHours.put(sub, hours);
    }

    //Calculate all the time needed to learn all subjects.
    public int getNumberOfLearningHours()
    {
        int counter = 0;
        for(int n : learningHours.values())
            counter += n;
        return counter;
    }

    //Find a subject that needs to be learned, that the teacher t knows to learn.
    public Subject getCommonSubject(Teacher t)
    {
        Set<Subject> teachable = t.getTeachableSubjects();
        List<Subject> learnable = new ArrayList<>();
        for(Subject s : teachable)
        {
            if(learningHours.get(s) != null)
                learnable.add(s);
        }
        if(learnable.size() == 0)
            return null;
        else
        {
            return learnable.get(new Random().nextInt(learnable.size()));
        }
    }

    @Override
    public String toString() {
        return "Classroom{" +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", learningHours=" + learningHours +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return (super.equals(o));
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, learningHours);
    }
}
