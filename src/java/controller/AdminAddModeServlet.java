package controller;

import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Mode;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.*;

@WebServlet(name = "AdminAddModeServlet", urlPatterns = {"/AdminAddMode"})
public class AdminAddModeServlet extends HttpServlet {
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
            obj.addProperty("message", "Mode name required");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tx = null;
            try {
                Criteria criteria = s.createCriteria(Mode.class);
                criteria.add(org.hibernate.criterion.Restrictions.eq("name", name.trim()));
                if (!criteria.list().isEmpty()) {
                    obj.addProperty("status", false);
                    obj.addProperty("message", "Mode already exists");
                } else {
                    tx = s.beginTransaction();
                    Mode m = new Mode();
                    m.setName(name.trim());
                    s.save(m);
                    tx.commit();
                    obj.addProperty("status", true);
                    obj.addProperty("message", "Mode added successfully");
                }
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                obj.addProperty("status", false);
                obj.addProperty("message", "Error adding mode");
            } finally {
                s.close();
            }
        }
        response.setContentType("application/json");
        response.getWriter().write(obj.toString());
    }
}