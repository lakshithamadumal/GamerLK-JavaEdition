package controller;

import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.HibernateUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.*;

@WebServlet(name = "AdminDeleteCategoryServlet", urlPatterns = {"/AdminDeleteCategory"})
public class AdminDeleteCategoryServlet extends HttpServlet {

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
            obj.addProperty("message", "Invalid category id");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tx = null;
            try {
                tx = s.beginTransaction();
                Category c = (Category) s.get(Category.class, Integer.parseInt(id));
                if (c != null) {
                    s.delete(c);
                    tx.commit();
                    obj.addProperty("status", true);
                } else {
                    obj.addProperty("status", false);
                    obj.addProperty("message", "Category not found");
                }
            } catch (Exception e) {
                if (tx != null) {
                    tx.rollback();
                }
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
                    obj.addProperty("message", "Cannot delete: This category is used in another record");
                } else {
                    obj.addProperty("status", false);
                    obj.addProperty("message", "Error deleting category");
                }
            } finally {
                s.close();
            }
        }
        response.setContentType("application/json");
        response.getWriter().write(obj.toString());
    }
}
