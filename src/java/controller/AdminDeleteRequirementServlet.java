package controller;

import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Requirement;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.*;

@WebServlet(name = "AdminDeleteRequirementServlet", urlPatterns = {"/AdminDeleteRequirement"})
public class AdminDeleteRequirementServlet extends HttpServlet {
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
            obj.addProperty("message", "Invalid requirement id");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tx = null;
            try {
                tx = s.beginTransaction();
                Requirement r = (Requirement) s.get(Requirement.class, Integer.parseInt(id));
                if (r != null) {
                    s.delete(r);
                    tx.commit();
                    obj.addProperty("status", true);
                } else {
                    obj.addProperty("status", false);
                    obj.addProperty("message", "Requirement not found");
                }
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                obj.addProperty("status", false);
                obj.addProperty("message", "Error deleting requirement");
            } finally {
                s.close();
            }
        }
        response.setContentType("application/json");
        response.getWriter().write(obj.toString());
    }
}