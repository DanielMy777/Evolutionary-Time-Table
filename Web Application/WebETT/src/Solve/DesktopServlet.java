package Solve;

import Competative.SuggestedSolution;
import Algorithm.WebAlgorithemProgress;
import Algorithm.WebUIAdapter;
import Data.Solution;
import Database.TTFactory;
import Database.TTLoader;
import Main.EEException;
import TTBasic.SettingSet;
import TTInfo.Classroom;
import TTInfo.Subject;
import TTInfo.Teacher;
import TTSolution.FilledTimeTable;
import TTSolution.TTException;
import Users.UserList;
import Utils.CommonActions;
import Utils.Constants;
import Utils.ServletUtils;
import Utils.SessionUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.sun.javaws.exceptions.InvalidArgumentException;
import com.sun.xml.internal.ws.util.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.SliderUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

@WebServlet(name = "DesktopServlet", urlPatterns = "/Solve/desktop")
public class DesktopServlet extends HttpServlet {

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, EEException {
        response.setContentType("application/json");
        SuggestedSolution currentSolution = CommonActions.getSolution(request);
        WebAlgorithemProgress progress = currentSolution.getCurrentProgress();

        JsonObject jsonObject;
        int gens = 0;
        double fitness = 0;
        long time = 0;
        Solution sol = null;

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        try {
            Gson gson = new Gson();
            jsonObject =  gson.fromJson(jb.toString(), JsonObject.class);
            progress.setCurrentGeneration(jsonObject.get("Data").getAsJsonObject().get("Generation").getAsInt());
            progress.setCurrentFitness(jsonObject.get("Data").getAsJsonObject().get("Fitness").getAsFloat());
            progress.setCurrentTime(jsonObject.get("Data").getAsJsonObject().get("Time").getAsLong());
            updateBestSolution(currentSolution, jsonObject.get("Data").getAsJsonObject().get("Solution"));
        } catch (Exception e) {
            // crash and burn
            throw new IOException("Error parsing JSON request string");
        }
        response.setStatus(200);
    }

    private void updateBestSolution(SuggestedSolution currentSolution, JsonElement bestSolution) throws TTException {
        if(bestSolution != null)
        {
            Gson gson = new Gson();
            DecimalFormat dformat = new DecimalFormat("##");
            LinkedTreeMap solutionToApply = gson.fromJson(bestSolution.getAsJsonObject(), LinkedTreeMap.class);
            TTLoader loader = new TTLoader();
            loader.parseFromJsonObject((Map) solutionToApply.get("schoolData"));
            int days = Integer.parseInt(dformat.format(solutionToApply.get("days")));
            int hours = Integer.parseInt(dformat.format(solutionToApply.get("hours")));
            int weight = Integer.parseInt(dformat.format(solutionToApply.get("hardRulesWeight")));
            double fit = Double.parseDouble(solutionToApply.get("fitness").toString());
            FilledTimeTable newBestSolution = new FilledTimeTable(days,hours, new TTFactory(loader));
            currentSolution.getPreferences().setFactory(new TTFactory(loader));
            newBestSolution.setHardRulesWeight(weight);
            newBestSolution.setFitness(fit);
            List<List<LinkedTreeMap>> timeTable = (List<List<LinkedTreeMap>>) solutionToApply.get("TTSetting");
            for(int i=0; i<timeTable.size(); i++)
            {
                List<LinkedTreeMap> currentDaySettings = (List<LinkedTreeMap>) timeTable.get(i);
                for(LinkedTreeMap settingSet : currentDaySettings)
                {
                    List<LinkedTreeMap> allSettings = (List<LinkedTreeMap>) settingSet.get("SettingsInHour");
                    for(LinkedTreeMap setting : allSettings)
                    {
                        int day = Integer.parseInt(dformat.format(setting.get("day")));
                        int hour = Integer.parseInt(dformat.format(setting.get("hour")));
                        Classroom clazz = loader.getClassroomByID(Integer.parseInt(dformat.format(((LinkedTreeMap)setting.get("classroom")).get("id"))));
                        Teacher teacher = loader.getTeacherByID(Integer.parseInt(dformat.format(((LinkedTreeMap)setting.get("teacher")).get("id"))));
                        Subject subject = loader.getSubjectByID(Integer.parseInt(dformat.format(((LinkedTreeMap)setting.get("subject")).get("id"))));
                        newBestSolution.AddSetting(day,hour,clazz,teacher,subject);
                    }
                }
            }
            currentSolution.setBestSolution(newBestSolution);
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

