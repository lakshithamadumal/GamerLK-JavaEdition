package controller;

import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.*;

@WebServlet(name = "AdminDeleteGameServlet", urlPatterns = {"/AdminDeleteGame"})
public class AdminDeleteGameServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String productId = request.getParameter("id");
        JsonObject obj = new JsonObject();

        // Admin session check
        if (request.getSession().getAttribute("admin") == null) {
            obj.addProperty("status", false);
            obj.addProperty("message", "Admin not found");
        } else if (productId != null && productId.matches("\\d+")) {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tx = null;
            try {
                tx = s.beginTransaction();
                Product p = (Product) s.get(Product.class, Integer.valueOf(productId));
                if (p != null) {
                    s.delete(p);
                    tx.commit();
                    obj.addProperty("status", true);
                } else {
                    obj.addProperty("status", false);
                    obj.addProperty("message", "Product not found");
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
                    obj.addProperty("message", "Cannot delete: This game is used in another record");
                } else {
                    obj.addProperty("status", false);
                    obj.addProperty("message", "Error deleting game");
                }
            } finally {
                s.close();
            }
        } else {
            obj.addProperty("status", false);
            obj.addProperty("message", "Invalid product id");
        }

        response.setContentType("application/json");
        response.getWriter().write(obj.toString());
    }
}
