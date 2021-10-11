package Login;

import Utils.SessionUtils;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "WelcomeServlet", urlPatterns = "/welcome")
public class WelcomeServlet extends HttpServlet {

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String userName = SessionUtils.getUsername(request);

        if(userName == null) {
            userName = "";
        }

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(userName);

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
