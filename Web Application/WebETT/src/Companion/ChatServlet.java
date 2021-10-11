package Companion;

import Chat.ChatList;
import Competative.ProblemList;
import Users.User;
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
import java.util.List;

@WebServlet(name = "ChatServlet", urlPatterns = "/chat")
public class ChatServlet extends HttpServlet {

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        ChatList chatList = ServletUtils.getChatList(request.getServletContext());
        UserList userList = ServletUtils.getUserList(request.getServletContext());
        String whatToDO = request.getParameter(Constants.DESIRED_ACTION);

        if(whatToDO.equals(Constants.ADD_CHAT))
        {
            response.setContentType("text/plain");
            String message = request.getParameter(Constants.CHAT_MESSAGE);
            User writer = userList.getUser(SessionUtils.getUsername(request));
            synchronized (chatList)
            {
                chatList.addMessage(writer, message);
            }
        }
        else if(whatToDO.equals(Constants.GET_CHATS))
        {
            List<ChatList.ChatEntry> allchats = chatList.getMessages();
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(allchats);

            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse);
                out.flush();
            }
        }
        else if(whatToDO.equals(Constants.GET_USERS))
        {
            List<User> allUsers = userList.getAllUsers();
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(allUsers);

            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse);
                out.flush();
            }
        }

        response.setStatus(200);
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