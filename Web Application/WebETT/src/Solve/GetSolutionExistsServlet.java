package Solve;

import Competative.IssuedProblem;
import Competative.ProblemList;
import Competative.SuggestedSolution;
import Algorithm.WebAlgorithemProgress;
import Algorithm.WebUIAdapter;
import Database.DTO;
import Main.EEException;
import TTSolution.FilledTimeTable;
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

@WebServlet(name = "GetSolutionExistsServlet", urlPatterns = "/Solve/exist")
public class GetSolutionExistsServlet extends HttpServlet {

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, EEException {
        response.setContentType("text/plain");
        SuggestedSolution currentSolution = CommonActions.getSolution(request);
        FilledTimeTable bestSolution = (FilledTimeTable) currentSolution.getBestSolution();
        if(bestSolution == null)
            response.getWriter().print("no");
        else
            response.getWriter().print("yes");
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