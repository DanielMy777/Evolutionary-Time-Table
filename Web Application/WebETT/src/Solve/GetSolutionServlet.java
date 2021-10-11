package Solve;

import Competative.SuggestedSolution;
import Algorithm.WebAlgorithemProgress;
import Algorithm.WebUIAdapter;
import Data.Rule;
import Database.DTO;
import Database.TTLoader;
import Main.EEException;
import TTBasic.Setting;
import TTBasic.SettingSet;
import TTInfo.Classroom;
import TTInfo.Teacher;
import TTSolution.FilledTimeTable;
import TTSolution.TTException;
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
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@WebServlet(name = "GetSolutionServlet", urlPatterns = "/Solve/best")
public class GetSolutionServlet extends HttpServlet {

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, EEException, TTException {
        response.setContentType("application/json");
        SuggestedSolution currentSolution = CommonActions.getSolution(request);
        FilledTimeTable bestSolution = (FilledTimeTable) currentSolution.getBestSolution();
        String how = request.getParameter("how");
        Object toWrite = null;
        String data = null;
        DTO allData;

        Gson gson = new Gson();

        switch (how)
        {
            case Constants.BY_RAW:
                toWrite = getRawTable(bestSolution);
                data = gson.toJson(toWrite);
                break;
            case Constants.BY_STATISTICS:
                Map<Rule, Boolean> rules = currentSolution.getPreferences().getSolutionLoaderData().getAllRulesWithWeights();
                toWrite = getStatsTable(bestSolution, rules);
                data = gson.toJson(toWrite);
                break;
            case Constants.BY_TEACHER:
                int idxTeacher = Integer.parseInt(request.getParameter(Constants.NUMBER));
                Teacher teacher = ((TTLoader)currentSolution.getPreferences().getSolutionLoaderData()).getTeacherByID(idxTeacher);
                toWrite = getTeacherTable(bestSolution, teacher);
                allData = new DTO(new Pair<>("TimeTable", toWrite),
                        new Pair<>("Days", ((TTLoader)currentSolution.getPreferences().getSolutionLoaderData()).days),
                        new Pair<>("Hours", ((TTLoader)currentSolution.getPreferences().getSolutionLoaderData()).hours));
                data = gson.toJson(allData);
                break;
            case Constants.BY_CLASS:
                int idxClass = Integer.parseInt(request.getParameter(Constants.NUMBER));
                Classroom classroom = ((TTLoader)currentSolution.getPreferences().getSolutionLoaderData()).getClassroomByID(idxClass);
                toWrite = getClassTable(bestSolution, classroom);
                allData = new DTO(new Pair<>("TimeTable", toWrite),
                        new Pair<>("Days", ((TTLoader)currentSolution.getPreferences().getSolutionLoaderData()).days),
                        new Pair<>("Hours", ((TTLoader)currentSolution.getPreferences().getSolutionLoaderData()).hours));
                data = gson.toJson(allData);
                break;
        }



        try (PrintWriter out = response.getWriter()) {
            out.print(data);
            out.flush();
        }
        response.setStatus(200);
    }

    private List<Setting> getRawTable(FilledTimeTable sol)
    {
        return sol.getAllSettingsOfTimeTable();
    }

    private String[][] getStatsTable(FilledTimeTable sol, Map<Rule, Boolean> rules)
    {
        String[][] res = new String[rules.size()][];
        DecimalFormat dformat = new DecimalFormat("##.##");
        int i=0;
        for(Map.Entry<Rule, Boolean> r : rules.entrySet())
        {
            res[i] = new String[3];
            res[i][0] = r.getKey().toString();
            res[i][1] = r.getValue() ? "Hard" : "Soft";
            res[i][2] = dformat.format(r.getKey().eval(sol));
            i++;
        }
        return res;
    }

    private SettingSet[][] getTeacherTable(FilledTimeTable sol, Teacher t)
    {
        return sol.getAllSettingsOfTeacherAsSettingSet(t);
    }

    private SettingSet[][] getClassTable(FilledTimeTable sol, Classroom c)
    {
        return sol.getAllSettingsOfClassAsSettingSet(c);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
    {
        try {
            processRequest(req, res);
        } catch (TTException | EEException e) {

        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
    {
        try {
            processRequest(req, res);
        } catch (TTException | EEException e) {

        }
    }
}

