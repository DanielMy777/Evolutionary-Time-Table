package TTInfo;

import generated.ETTSubject;

import java.io.Serializable;

//This class represents a Subject in the time table.

public class Subject extends HasID implements Serializable {

    //super : final int id;
    private String name;

    public Subject(int id, String name) {
        super(id);
        this.name = name;
    }

    //Construct a Subject from ETTSubject object
    public Subject(ETTSubject s) {
        this(s.getId(), s.getName());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return (super.equals(o));
    }

}
