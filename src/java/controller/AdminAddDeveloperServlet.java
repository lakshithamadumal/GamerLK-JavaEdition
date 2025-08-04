package controller;

import com.google.gson.JsonObject;
import hibernate.Developer;
import hibernate.HibernateUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.*;

@WebServlet(name = "AdminAddDeveloperServlet", urlPatterns = {"/AdminAddDeveloper"})
public class AdminAddDeveloperServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        JsonObject obj = new JsonObject();

        // Admin session check
        if (request.getSession().getAttribute("admin") == null) {
            obj.addProperty("status", false);
            obj.addProperty("message", "Admin not found");
        } else if (name == null || name.trim().isEmpty()) {
            obj.addProperty("status", false);
            obj.addProperty("message", "Developer name required");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tx = null;
            try {
                Criteria criteria = s.createCriteria(Developer.class);
                criteria.add(org.hibernate.criterion.Restrictions.eq("name", name.trim()));
                if (!criteria.list().isEmpty()) {
                    obj.addProperty("status", false);
                    obj.addProperty("message", "Developer already exists");
                } else {
                    tx = s.beginTransaction();
                    Developer d = new Developer();
                    d.setName(name.trim());
                    s.save(d);
                    tx.commit();
                    obj.addProperty("status", true);
                    obj.addProperty("message", "Developer added successfully");
                }
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                obj.addProperty("status", false);
                obj.addProperty("message", "Error adding developer");
            } finally {
                s.close();
            }
        }
        response.setContentType("application/json");
        response.getWriter().write(obj.toString());
    }
}