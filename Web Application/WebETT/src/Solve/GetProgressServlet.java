package Solve;

import Competative.SuggestedSolution;
import Algorithm.WebAlgorithemProgress;
import Algorithm.WebUIAdapter;
import Main.EEException;
import Users.UserList;
import Utils.CommonActions;
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

@WebServlet(name = "GetProgressServlet", urlPatterns = "/Solve/progress")
public class GetProgressServlet extends HttpServlet {

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, EEException {
        response.setContentType("application/json");
        SuggestedSolution currentSolution = CommonActions.getSolution(request);
        WebAlgorithemProgress progress = currentSolution.getCurrentProgress();

        WebAlgorithemProgress toWrite;
        if(progress == null)
        {
            toWrite = new WebAlgorithemProgress(WebAlgorithemProgress.NO_PREFERENCE,WebAlgorithemProgress.NO_PREFERENCE,WebAlgorithemProgress.NO_PREFERENCE);
        }
        else
        {
            toWrite = progress;
        }

        boolean isStopped = !currentSolution.isPaused() && !currentSolution.isRunning();

        Gson gson = new Gson();
        String data1 = gson.toJson(toWrite);
        data1 = data1.substring(0, data1.length() - 1);
        data1 += ",\"active\":" + (isStopped ? "\"no\"" : "\"yes\"") + "}";
        try (PrintWriter out = response.getWriter()) {
            out.print(data1);
            out.flush();
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

