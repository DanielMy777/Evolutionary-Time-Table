package Solve;
import Competative.IssuedProblem;
import Competative.ProblemList;
import Competative.SuggestedSolution;
import Algorithm.WebAlgorithemProgress;
import Algorithm.WebUIAdapter;
import Database.EvoAlgorithem;
import Main.EEException;
import Proporties.Crossover;
import Proporties.CrossoverMethods.AspectOriented;
import Proporties.CrossoverMethods.DayTimeOriented;
import Proporties.Mutation;
import Proporties.MutationMethods.Flipping;
import Proporties.MutationMethods.Sizer;
import Proporties.Selection;
import Proporties.SelectionMethods.RouletteWheel;
import Proporties.SelectionMethods.Tournament;
import Proporties.SelectionMethods.Truncation;
import TTSolution.FilledTimeTable;
import Users.User;
import Users.UserList;
import Utils.CommonActions;
import Utils.Constants;
import Utils.ServletUtils;
import Utils.SessionUtils;
import jdk.nashorn.internal.ir.RuntimeNode;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Locale;

@WebServlet(name = "ModifyServlet", urlPatterns = "/Solve/modify")
public class ModifyServlet extends HttpServlet {

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, EEException {
        response.setContentType("text/plain;charset=UTF-8");
        SuggestedSolution currentSolution = CommonActions.getSolution(request);
        WebAlgorithemProgress progress = currentSolution.getCurrentProgress();
        EvoAlgorithem<FilledTimeTable> theAlg = currentSolution.getPreferences();
        String toModify = request.getParameter(Constants.TO_MODIFY);
        switch (toModify)
        {
            case Constants.MODIFY_TERMINATION:
                updateTermination(request, currentSolution);
                break;
            case Constants.MODIFY_SELECTION:
                updateSelection(request, theAlg);
                break;
            case Constants.MODIFY_CROSSOVER:
                updateCrossover(request, theAlg);
                break;
            case Constants.MODIFY_MUTATION:
                updateMutations(request, theAlg);
                break;
        }
        response.setStatus(200);

    }


    private boolean updateTermination(HttpServletRequest request, SuggestedSolution sol)
    {
        int gens;
        float fit;
        long dur;
        if(!isNullOrEmpty(request.getParameter(Constants.TERMINATION_GENERATIONS)))
            gens = Integer.parseInt(request.getParameter(Constants.TERMINATION_GENERATIONS));
        else
            gens = WebAlgorithemProgress.NO_PREFERENCE;
        if(!isNullOrEmpty(request.getParameter(Constants.TERMINATION_FITNESS)))
            fit = Float.parseFloat(request.getParameter(Constants.TERMINATION_FITNESS));
        else
            fit = WebAlgorithemProgress.NO_PREFERENCE;
        if(!isNullOrEmpty(request.getParameter(Constants.TERMINATION_DURATION))) {
            String duration = "00:" + request.getParameter(Constants.TERMINATION_DURATION);
            int mins = LocalTime.parse(duration).getMinute();
            int secs = LocalTime.parse(duration).getSecond();
            dur = mins * 60 + secs;
        }
        else
            dur = WebAlgorithemProgress.NO_PREFERENCE;
        WebAlgorithemProgress prog = new WebAlgorithemProgress(gens, fit, dur);

        int initial = Integer.parseInt(request.getParameter(Constants.INITIAL_POPULATION));
        sol.getPreferences().setInitialPopulation(initial);
        sol.setCurrentProgress(prog);
        return true;
    }

    private boolean updateSelection(HttpServletRequest request, EvoAlgorithem<FilledTimeTable> theAlg)
    {
        Selection newMethod = null;
        int elitism = Integer.parseInt(request.getParameter(Constants.SELECTION_ELITISM));
        String method = request.getParameter(Constants.SELECTION_METHOD);
        switch (method)
        {
            case "Truncation":
                int topPercent = Integer.parseInt(request.getParameter(Constants.SELECTION_TOP_PERCENT));
                newMethod = new Truncation(elitism, "TopPercent="+topPercent, topPercent);
                break;
            case "Roulette Wheel":
                newMethod = new RouletteWheel(elitism, "");
                break;
            case "Tournament":
                double pte = Double.parseDouble(request.getParameter(Constants.SELECTION_PTE));
                newMethod = new Tournament(elitism, "PTE="+pte, pte);
                break;
        }

        theAlg.setSelectionMethod(newMethod);
        return true;
    }

    private boolean updateCrossover(HttpServletRequest request, EvoAlgorithem<FilledTimeTable> theAlg)
    {
        Crossover newMethod = null;
        int cutting = Integer.parseInt(request.getParameter(Constants.CROSSOVER_CUTTING_POINTS));
        String method = request.getParameter(Constants.CROSSOVER_METHOD);
        switch (method)
        {
            case "Day Time Oriented":
                newMethod = new DayTimeOriented(cutting, "");
                break;
            case "Aspect Oriented":
                String aspect = request.getParameter(Constants.CROSSOVER_ASPECT).toUpperCase(Locale.ROOT);
                newMethod = new AspectOriented(cutting, "Aspect="+aspect, aspect);
                break;
        }

        theAlg.setCrossoverMethod(newMethod);
        return true;
    }

    private boolean updateMutations(HttpServletRequest request, EvoAlgorithem<FilledTimeTable> theAlg)
    {
        boolean toAdd = request.getParameter(Constants.MUTATION_REMOVE).equals(Constants.FALSE);
        if(toAdd)
        {
            Mutation newMethod = null;
            int tupples = Integer.parseInt(request.getParameter(Constants.MUTATION_TUPPLES));
            double prob = Double.parseDouble(request.getParameter(Constants.MUTATION_PROBABILITY));
            String method = request.getParameter(Constants.MUTATION_METHOD);
            switch (method)
            {
                case "Sizer":
                    newMethod = new Sizer(prob, "totalTupples="+tupples, tupples);
                    break;
                case "Flipper":
                    String component = request.getParameter(Constants.MUTATION_COMPONENT).substring(0,1);
                    Flipping.Component c = Flipping.Component.valueOf(component);
                    newMethod = new Flipping(prob, "maxTupples"+tupples+",component="+component, tupples, c);
                    break;
            }

            theAlg.addMutation(newMethod);
        }
        else //remove
        {
            int idx = Integer.parseInt(request.getParameter(Constants.MUTATION_REMOVE));
            theAlg.removeMutation(idx);
        }
        return true;
    }

    private boolean isNullOrEmpty(String s)
    {
        return s == null || s.isEmpty();
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