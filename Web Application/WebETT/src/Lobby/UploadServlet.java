package Lobby;

import Competative.IssuedProblem;
import Database.TTLoader;
import TTSolution.TTException;
import Users.User;
import Users.UserList;
import Utils.Constants;
import Utils.ServletUtils;
import Utils.SessionUtils;
import com.google.gson.Gson;
import com.sun.javaws.exceptions.InvalidArgumentException;
import com.sun.xml.internal.ws.util.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Scanner;

@WebServlet(name = "UploadServlet", urlPatterns = "/Lobby/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadServlet extends HttpServlet {

    private final String LOBBY_URL = "../Lobby.html";
    private final String PUSH_TO_DB = "toDB";

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        response.setContentType("text/plain;charset=UTF-8");

        Collection<Part> parts = request.getParts();
        Part uploadedFile = request.getPart("file1");
        boolean toDB = (new Scanner(
                request.getPart(PUSH_TO_DB).getInputStream())).useDelimiter("\\Z").next().equals(Constants.TRUE);

        TTLoader uploadedData = new TTLoader();
        try{
            uploadedData.loadInputStream(uploadedFile.getInputStream());
            if(!toDB)
            {
                returnAllDataLoaded(uploadedData, response);
            }
            else
            {
                String userName = SessionUtils.getUsername(request);
                if(userName != null)
                {
                    User creator = ServletUtils.getUserList(request.getServletContext()).getUser(userName);
                    IssuedProblem newProb = new IssuedProblem(creator, uploadedData);
                    ServletUtils.getProblemList(request.getServletContext()).addProblem(newProb);
                    response.setStatus(200);
                    response.getOutputStream().println(LOBBY_URL);
                }
                else
                {
                    response.sendRedirect("/" + request.getContextPath());
                }
            }
        }
        catch (TTException e)
        {
            response.setStatus(409);
            response.getOutputStream().println(e.getMessage());
        }
    }

    private void returnAllDataLoaded(TTLoader AllData, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(AllData);

        response.setStatus(202);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        processRequest(req, res);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        processRequest(req, res);
    }
}
