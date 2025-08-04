package controller;

import com.google.gson.JsonObject;
import hibernate.Developer;
import hibernate.HibernateUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.*;

@WebServlet(name = "AdminDeleteDeveloperServlet", urlPatterns = {"/AdminDeleteDeveloper"})
public class AdminDeleteDeveloperServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String id = request.getParameter("id");
        JsonObject obj = new JsonObject();

        // Admin session check
        if (request.getSession().getAttribute("admin") == null) {
            obj.addProperty("status", false);
            obj.addProperty("message", "Admin not found");
        } else if (id == null || !id.matches("\\d+")) {
            obj.addProperty("status", false);
            obj.addProperty("message", "Invalid developer id");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tx = null;
            try {
                tx = s.beginTransaction();
                Developer d = (Developer) s.get(Developer.class, Integer.parseInt(id));
                if (d != null) {
                    s.delete(d);
                    tx.commit();
                    obj.addProperty("status", true);
                } else {
                    obj.addProperty("status", false);
                    obj.addProperty("message", "Developer not found");
                }
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                // Check for foreign key constraint violation (deep cause)
                Throwable cause = e;
                boolean isConstraint = false;
                while (cause != null) {
                    String msg = cause.getMessage();
                    if (msg != null && (msg.toLowerCase().contains("constraint") || msg.toLowerCase().contains("foreign key"))) {
                        isConstraint = true;
                        break;
                    }
                    cause = cause.getCause();
                }
                if (isConstraint) {
                    obj.addProperty("status", false);
                    obj.addProperty("message", "Cannot delete: This developer is used in another record");
                } else {
                    obj.addProperty("status", false);
                    obj.addProperty("message", "Error deleting developer");
                }
            } finally {
                s.close();
            }
        }
        response.setContentType("application/json");
        response.getWriter().write(obj.toString());
    }
}