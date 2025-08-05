package controller;

import com.google.gson.JsonObject;
import hibernate.Admin;
import hibernate.HibernateUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "GetCurrentAdmin", urlPatterns = {"/GetCurrentAdmin"})
public class GetCurrentAdmin extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JsonObject obj = new JsonObject();
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("admin") != null) {
            Admin admin = (Admin) session.getAttribute("admin");
            obj.addProperty("status", true);
            obj.addProperty("email", admin.getEmail());
        } else {
            obj.addProperty("status", false);
            obj.addProperty("email", "");
        }

        response.setContentType("application/json");
        response.getWriter().write(obj.toString());
    }
}