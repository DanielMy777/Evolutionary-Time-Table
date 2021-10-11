package Login;

import Competative.IssuedProblem;
import Competative.ProblemList;
import Users.User;
import Users.UserList;
import Utils.Constants;
import Utils.ServletUtils;
import Utils.SessionUtils;
import com.google.gson.Gson;
import com.sun.javaws.exceptions.InvalidArgumentException;
import com.sun.xml.internal.ws.util.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "LogoutServlet", urlPatterns = "/logout")
public class LogoutServlet extends HttpServlet {

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        String usernameOfSession = SessionUtils.getUsername(request);
        UserList userList = ServletUtils.getUserList(request.getServletContext());

        if(usernameOfSession != null) {
            User user = userList.getUser(usernameOfSession);
            removeUserFromProblems(request.getServletContext(), user);
            userList.removeUser(usernameOfSession);
            SessionUtils.clearSession(request);
        }

        if(request.getParameter(Constants.REDIRECT) == null)
            response.sendRedirect(request.getContextPath() + "/index.html");
    }

    private void removeUserFromProblems(ServletContext cont,User u)
    {
        ProblemList problemList = ServletUtils.getProblemList(cont);
        for(IssuedProblem prob : problemList.getAllProblems())
        {
            prob.removeSolution(u);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
    {
        processRequest(req, res);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
    {
        processRequest(req, res);
    }
}
