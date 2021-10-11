package Lobby;

import Competative.IssuedProblem;
import Competative.ProblemList;
import Database.DTO;
import Users.UserList;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "GetProblemsServlet", urlPatterns = "/getproblems")
public class GetProblemsServlet extends HttpServlet {

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        ProblemList allProblems = ServletUtils.getProblemList(request.getServletContext());
        List<DTO> data = new ArrayList<>();
        for(IssuedProblem prob : allProblems.getAllProblems())
        {
            List<DTO> allSolutions = prob.getSortedUsersOnProblem();
            float bestFit = allSolutions.size() == 0 ? 0f : (float) allSolutions.get(0).getData("currFitness");
            data.add(new DTO(new Pair<>("problem", prob),
                    new Pair<>("numberOfSolutions", allSolutions.size()),
                    new Pair<>("bestfit", bestFit)));
        }
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(data);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
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
