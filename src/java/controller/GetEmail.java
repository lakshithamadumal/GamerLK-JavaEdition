package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Laky
 */
@WebServlet(name = "GetEmail", urlPatterns = {"/GetEmail"})
public class GetEmail extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonObject responseObject = new JsonObject();

        HttpSession ses = request.getSession(false);
        if (ses != null && ses.getAttribute("email") != null) {
            String email = ses.getAttribute("email").toString();//Get Session have email

            responseObject.addProperty("email", email);
            responseObject.addProperty("message", "Email Found");

        } else {
            responseObject.addProperty("message", "Email Not Found");
        }
        
        Gson gson = new Gson();
        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }
}
