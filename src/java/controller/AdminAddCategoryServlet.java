package controller;

import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.HibernateUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.*;
import org.hibernate.Criteria;

@WebServlet(name = "AdminAddCategoryServlet", urlPatterns = {"/AdminAddCategory"})
public class AdminAddCategoryServlet extends HttpServlet {
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
            obj.addProperty("message", "Category name required");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tx = null;
            try {
                // Duplicate name check
                Criteria criteria = s.createCriteria(Category.class);
                criteria.add(org.hibernate.criterion.Restrictions.eq("name", name.trim()));
                if (!criteria.list().isEmpty()) {
                    obj.addProperty("status", false);
                    obj.addProperty("message", "Category already exists");
                } else {
                    tx = s.beginTransaction();
                    Category c = new Category();
                    c.setName(name.trim());
                    s.save(c);
                    tx.commit();
                    obj.addProperty("status", true);
                    obj.addProperty("message", "Category added successfully");
                }
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                obj.addProperty("status", false);
                obj.addProperty("message", "Error adding category");
            } finally {
                s.close();
            }
        }
        response.setContentType("application/json");
        response.getWriter().write(obj.toString());
    }
}