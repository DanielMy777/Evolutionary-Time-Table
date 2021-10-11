package Utils;

import Competative.IssuedProblem;
import Competative.ProblemList;
import Competative.SuggestedSolution;
import Main.EEException;
import Users.User;
import Users.UserList;

import javax.servlet.http.HttpServletRequest;

public class CommonActions {

    public static SuggestedSolution getSolution(HttpServletRequest request) throws EEException {
        ProblemList problemList = ServletUtils.getProblemList(request.getServletContext());
        int problemID = Integer.parseInt(request.getParameter(Constants.PROBLEM));
        IssuedProblem currentProblem = problemList.getProblem(problemID-1);
        UserList userList = ServletUtils.getUserList(request.getServletContext());
        String myUsername = SessionUtils.getUsername(request);
        String watchUsername = request.getParameter(Constants.OTHER_USER);
        User user;
        if(watchUsername != null) {
            user = userList.getUser(watchUsername);
            if(user == null)
                user = userList.getUser(myUsername);
        }
        else
            user = userList.getUser(myUsername);
        return currentProblem.getSolutionOfUser(user);
    }
}
