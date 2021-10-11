package Solve;

import Competative.IssuedProblem;
import Competative.ProblemList;
import Competative.SuggestedSolution;
import Algorithm.WebAlgorithemProgress;
import Algorithm.WebUIAdapter;
import Database.EvoAlgorithem;
import Main.EEException;
import Proporties.Crossover;
import Proporties.Mutation;
import Proporties.Selection;
import TTSolution.FilledTimeTable;
import Users.User;
import Users.UserList;
import Utils.CommonActions;
import Utils.Constants;
import Utils.ServletUtils;
import Utils.SessionUtils;
import com.google.gson.Gson;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "GetPreferencesServlet", urlPatterns = "/Solve/getpref")
public class GetPreferencesServlet extends HttpServlet {

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, EEException {
        response.setContentType("application/json");
        String whatToGet = request.getParameter("what");
        SuggestedSolution currentSolution = CommonActions.getSolution(request);
        WebAlgorithemProgress progress = currentSolution.getCurrentProgress();
        EvoAlgorithem<FilledTimeTable> theAlg = currentSolution.getPreferences();

        switch (whatToGet)
        {
            case Constants.MODIFY_TERMINATION:
                int initial = currentSolution.getPreferences().getInitialPopulation();
                getTerminationData(response, progress, initial);
                break;
            case Constants.MODIFY_SELECTION:
                getSelectionData(response, theAlg);
                break;
            case Constants.MODIFY_CROSSOVER:
                getCrossoverData(response, theAlg);
                break;
            case Constants.MODIFY_MUTATION:
                getMutationData(response, theAlg);
                break;
        }

        response.setStatus(200);
    }

    private void getTerminationData(HttpServletResponse response, WebAlgorithemProgress prog, int initial) throws IOException {
        Gson gson = new Gson();
        String data;

        if (prog == null) {
            data = gson.toJson("none");
        }
        else {
            data = gson.toJson(prog);
            data = data.substring(0, data.length()-1);
            data += ",\"initial\":"+initial+"}";
        }
        try (PrintWriter out = response.getWriter()) {
            out.print(data);
            out.flush();
        }
    }

    private void getSelectionData(HttpServletResponse response, EvoAlgorithem<FilledTimeTable> theAlg) throws IOException {
        Gson gson = new Gson();
        Selection sel = theAlg.getSelectionMethod();
        String data;

        if(sel == null)
        {
            data = gson.toJson("none");
        }
        else {
            data = gson.toJson(sel);
            data = data.substring(0, data.length() - 1);
            data += ",\"Method\":\"" + theAlg.getSelectionMethod().getClass().getSimpleName() + "\"}";
        }
        try (PrintWriter out = response.getWriter()) {
            out.print(data);
            out.flush();
        }
    }

    private void getCrossoverData(HttpServletResponse response, EvoAlgorithem<FilledTimeTable> theAlg) throws IOException {
        Gson gson = new Gson();
        Crossover cross = theAlg.getCrossoverMethod();
        String data;

        if(cross == null)
        {
            data = gson.toJson("none");
        }
        else {
            data = gson.toJson(cross);
            data = data.substring(0, data.length() - 1);
            data += ",\"Method\":\"" + theAlg.getCrossoverMethod().getClass().getSimpleName() + "\"}";
        }
        try (PrintWriter out = response.getWriter()) {
            out.print(data);
            out.flush();
        }
    }

    private void getMutationData(HttpServletResponse response, EvoAlgorithem<FilledTimeTable> theAlg) throws IOException {
        Gson gson = new Gson();
        List<Mutation> mutations = theAlg.getMutationMethods();
        String data = gson.toJson(mutations);
        try (PrintWriter out = response.getWriter()) {
            out.print(data);
            out.flush();
        }

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