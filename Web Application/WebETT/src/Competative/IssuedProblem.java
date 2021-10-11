package Competative;

import Database.DTO;
import Database.SolutionLoader;
import Database.TTLoader;
import Main.EEException;
import Users.User;
import com.sun.org.apache.bcel.internal.generic.RET;
import javafx.util.Pair;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IssuedProblem {

    User creator;
    TTLoader problemDefinition;
    transient Map<User, SuggestedSolution> suggestedSolutionsByUser;

    public IssuedProblem(User user, TTLoader data)
    {
        creator = user;
        problemDefinition = data;
        suggestedSolutionsByUser = new HashMap<>();
    }

    public SolutionLoader getProblemDefinition() {
        return problemDefinition;
    }

    public void setProblemDefinition(TTLoader problemDefinition) {
        this.problemDefinition = problemDefinition;
    }

    public Map<User, SuggestedSolution> getSuggestedSolutionsByUser() {
        return suggestedSolutionsByUser;
    }

    public void setSuggestedSolutionsByUser(Map<User, SuggestedSolution> suggestedSolutionsByUser) {
        this.suggestedSolutionsByUser = suggestedSolutionsByUser;
    }

    public void addSolution(User user, SuggestedSolution ss)
    {
        suggestedSolutionsByUser.put(user, ss);
    }
    public void removeSolution(User user)
    {
        suggestedSolutionsByUser.remove(user);
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public SuggestedSolution getSolutionOfUser(User user) throws EEException {
        SuggestedSolution sol = suggestedSolutionsByUser.get(user);
        if(sol == null) {
            sol = new SuggestedSolution(user, this);
            suggestedSolutionsByUser.put(user, sol);
        }
        return sol;
    }

    public synchronized List<DTO> getSortedUsersOnProblem()
    {
        Collection<SuggestedSolution> allSolutions = suggestedSolutionsByUser.values();
        List<DTO> res = new ArrayList<>();
        DecimalFormat dformat = new DecimalFormat("###.#");
        for(SuggestedSolution sol : allSolutions)
        {
            if(sol.getCurrentProgress() == null)
            {
                res.add(new DTO(new Pair<>("owner", sol.owner.getName()),
                        new Pair<>("currGeneration", 0f),
                        new Pair<>("currFitness", 0f)));
            }
            else {
                res.add(new DTO(new Pair<>("owner", sol.owner.getName()),
                        new Pair<>("currGeneration", sol.getCurrentProgress().getCurrentGeneration()),
                        new Pair<>("currFitness", Float.valueOf(dformat.format(sol.getCurrentProgress().getCurrentFitness())))));
            }
        }
        Collections.sort(res, new Comparator<DTO>() {
            @Override
            public int compare(DTO lhs, DTO rhs) {
                float data1 = (float) lhs.getData("currFitness");
                float data2 = (float) rhs.getData("currFitness");
                return Float.compare(data2, data1);
            }
        });
        res.sort((s1,s2) -> (int) Math.ceil((float)s2.getData("currFitness") - (float)s1.getData("currFitness")));
        return res;
    }
}
