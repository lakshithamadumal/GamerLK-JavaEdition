package controller;

import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Requirement;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.*;

@WebServlet(name = "AdminAddRequirementServlet", urlPatterns = {"/AdminAddRequirement"})
public class AdminAddRequirementServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String os = request.getParameter("os");
        String memory = request.getParameter("memory");
        String processor = request.getParameter("processor");
        String graphics = request.getParameter("graphics");
        String storage = request.getParameter("storage");
        JsonObject obj = new JsonObject();

        // Admin session check
        if (request.getSession().getAttribute("admin") == null) {
            obj.addProperty("status", false);
            obj.addProperty("message", "Admin not found");
        } else if (name == null || name.trim().isEmpty() ||
                   os == null || os.trim().isEmpty() ||
                   memory == null || memory.trim().isEmpty() ||
                   processor == null || processor.trim().isEmpty() ||
                   graphics == null || graphics.trim().isEmpty() ||
                   storage == null || storage.trim().isEmpty()) {
            obj.addProperty("status", false);
            obj.addProperty("message", "All fields are required");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tx = null;
            try {
                Criteria criteria = s.createCriteria(Requirement.class);
                criteria.add(org.hibernate.criterion.Restrictions.eq("name", name.trim()));
                if (!criteria.list().isEmpty()) {
                    obj.addProperty("status", false);
                    obj.addProperty("message", "Requirement already exists");
                } else {
                    tx = s.beginTransaction();
                    Requirement r = new Requirement();
                    r.setName(name.trim());
                    r.setOs(os.trim());
                    r.setMemory(memory.trim());
                    r.setProcessor(processor.trim());
                    r.setGraphics(graphics.trim());
                    r.setStorage(storage.trim());
                    s.save(r);
                    tx.commit();
                    obj.addProperty("status", true);
                    obj.addProperty("message", "Requirement added successfully");
                }
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                obj.addProperty("status", false);
                obj.addProperty("message", "Error adding requirement");
            } finally {
                s.close();
            }
        }
        response.setContentType("application/json");
        response.getWriter().write(obj.toString());
    }
}