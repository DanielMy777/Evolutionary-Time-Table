package Solve;

import Competative.IssuedProblem;
import Competative.ProblemList;
import Competative.SuggestedSolution;
import Algorithm.WebAlgorithemProgress;
import Algorithm.WebUIAdapter;
import Database.DTO;
import Main.EEException;
import Users.User;
import Users.UserList;
import Utils.CommonActions;
import Utils.Constants;
import Utils.ServletUtils;
import Utils.SessionUtils;
import com.google.gson.Gson;
import com.sun.javaws.exceptions.InvalidArgumentException;
import com.sun.xml.internal.ws.util.StringUtils;
import javafx.util.Pair;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "GetUsersServlet", urlPatterns = "/Solve/users")
public class GetUsersServlet extends HttpServlet {

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, EEException {
        response.setContentType("application/json");
        ProblemList problemList = ServletUtils.getProblemList(request.getServletContext());
        int problemID = Integer.parseInt(request.getParameter(Constants.PROBLEM));
        IssuedProblem currentProblem = problemList.getProblem(problemID-1);
        String username = SessionUtils.getUsername(request);
        List<DTO> alldata = currentProblem.getSortedUsersOnProblem();
        DTO toWrite = new DTO(new Pair<>("allSolutions", alldata),
                new Pair<>("currentUser", username));

        synchronized (currentProblem) {
            Gson gson = new Gson();
            String data = gson.toJson(toWrite);
            try (PrintWriter out = response.getWriter()) {
                out.print(data);
                out.flush();
            }
        }
        response.setStatus(200);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
    {
        try {
            processRequest(req, res);
        } catch (EEException e) {

        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
    {
        try {
            processRequest(req, res);
        } catch (EEException e) {

        }
    }
}
