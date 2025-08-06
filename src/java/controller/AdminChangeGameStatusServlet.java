package controller;

import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.Status;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.*;

@WebServlet(name = "AdminChangeGameStatusServlet", urlPatterns = {"/AdminChangeGameStatus"})
public class AdminChangeGameStatusServlet extends HttpServlet {

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
                    Status currentStatus = p.getStatus_id();
                    Status newStatus;
                    String currentValue = currentStatus != null ? currentStatus.getValue() : "";

                    if ("Active".equalsIgnoreCase(currentValue)) {
                        // If Active, set to Inactive (2)
                        newStatus = (Status) s.get(Status.class, 2);
                    } else if ("Inactive".equalsIgnoreCase(currentValue)) {
                        // If Inactive, set to Active (1)
                        newStatus = (Status) s.get(Status.class, 1);
                    } else {
                        obj.addProperty("status", false);
                        obj.addProperty("message", "Invalid status value: " + currentValue);
                        s.close();
                        response.setContentType("application/json");
                        response.getWriter().write(obj.toString());
                        return;
                    }
                    p.setStatus_id(newStatus);
                    s.update(p);
                    tx.commit();
                    obj.addProperty("status", true);
                    obj.addProperty("newStatus", newStatus.getValue());
                } else {
                    obj.addProperty("status", false);
                    obj.addProperty("message", "Product not found");
                }
            } catch (Exception e) {
                if (tx != null) {
                    tx.rollback();
                }
                obj.addProperty("status", false);
                obj.addProperty("message", "Error updating status");
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
