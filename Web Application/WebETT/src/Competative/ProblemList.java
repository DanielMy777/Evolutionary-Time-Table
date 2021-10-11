package Competative;

import Main.EEException;
import Users.User;

import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.List;

public class ProblemList {

    List<IssuedProblem> allProblems;

    public ProblemList()
    {
        allProblems = new ArrayList<>();
    }

    public void addProblem(IssuedProblem ip)
    {
        allProblems.add(ip);
    }

    public IssuedProblem getProblem(int id)
    {
        return allProblems.get(id);
    }

    public List<IssuedProblem> getAllProblems() {
        return allProblems;
    }
}
