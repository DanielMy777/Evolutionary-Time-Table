package Solve;
import Competative.SuggestedSolution;
import Algorithm.WebAlgorithemProgress;
import Algorithm.WebUIAdapter;
import Database.EvoAlgorithem;
import Main.EEException;
import TTSolution.FilledTimeTable;
import Utils.CommonActions;
import Utils.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AlgorithmServlet", urlPatterns = "/Solve/algo")
public class AlgorithmServlet extends HttpServlet {

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, EEException {
        response.setContentType("text/plain;charset=UTF-8");
        SuggestedSolution currentSolution = CommonActions.getSolution(request);
        String action = request.getParameter(Constants.DESIRED_ACTION);
        boolean isOK = true;
        switch (action)
        {
            case Constants.STATE:
                isOK = getState(currentSolution, response);
                break;
            case Constants.PLAY:
                isOK = playSolution(currentSolution, response);
                break;
            case Constants.PAUSE:
                isOK = pauseSolution(currentSolution, response);
                break;
            case Constants.STOP:
                isOK = stopSolution(currentSolution, response);
                break;
        }

        if(isOK)
            response.setStatus(200);
        else
            response.setStatus(409);
    }


    private boolean getState(SuggestedSolution sol, HttpServletResponse response) throws IOException {
        if(sol.isRunning())
            response.getWriter().print("running");
        else if(sol.isPaused())
            response.getWriter().print("paused");
        else
            response.getWriter().print("stopped");
        return true;
    }

    private boolean playSolution(SuggestedSolution sol, HttpServletResponse response) throws IOException, EEException {
        EvoAlgorithem<FilledTimeTable> theAlg = sol.getPreferences();
        WebAlgorithemProgress theProgress = sol.getCurrentProgress();
        if(theProgress == null)
        {
            response.getWriter().print("termination");
            return false;
        }
        else if(!sol.getPreferences().isReadyToWork())
        {
            if(sol.getPreferences().getSelectionMethod() == null)
                response.getWriter().print("selection");
            else
                response.getWriter().print("crossover");
            return false;
        }
        else
        {
            sol.playSolution();
        }
        return true;
    }

    private boolean pauseSolution(SuggestedSolution sol, HttpServletResponse response)
    {
        sol.pauseSolution();
        return true;
    }

    private boolean stopSolution(SuggestedSolution sol, HttpServletResponse response)
    {
        sol.stopSolution();
        return true;
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