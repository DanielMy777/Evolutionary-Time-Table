package Login;

import Users.UserList;
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

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private final String LOBBY_URL = "Lobby/Lobby.html";

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean error = false;
        String errorMsg = null;
        response.setContentType("text/plain;charset=UTF-8");
        String usernameOfSession = SessionUtils.getUsername(request);
        UserList userList = ServletUtils.getUserList(request.getServletContext());
        if(usernameOfSession == null)
        {
            String wantedUserName = StringUtils.capitalize(request.getParameter(Constants.USERNAME));
            if(wantedUserName == null || wantedUserName.isEmpty() || isOnlyWhiteSpace(wantedUserName))
            {
                error = true;
                errorMsg = "Invalid username, Please choose another";
            }
            else
            {
                synchronized (this) {
                    try {
                        wantedUserName = wantedUserName.trim();
                        userList.addUser(wantedUserName);
                        request.getSession(true).setAttribute(Constants.USERNAME, wantedUserName);
                        response.setStatus(200);
                        response.getOutputStream().println(LOBBY_URL);
                    } catch (UserList.UserExistsException e) {
                        error = true;
                        errorMsg = "Username " + wantedUserName + " already exists, Please choose another";
                    }
                }
            }
        }
        else
        {
            response.setStatus(200);
            response.getOutputStream().println(LOBBY_URL);
        }

        if(error)
        {
            response.setStatus(409);
            response.getOutputStream().println(errorMsg);
        }
    }

    private boolean isOnlyWhiteSpace(String str)
    {
        for(char c : str.toCharArray())
        {
            if(!Character.isWhitespace(c))
                return false;
        }
        return true;
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
