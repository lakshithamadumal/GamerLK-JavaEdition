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

@WebServlet(name = "AdminCloseOfferServlet", urlPatterns = {"/AdminCloseOffer"})
public class AdminCloseOfferServlet extends HttpServlet {

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
            obj.addProperty("message", "Invalid product id");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tx = null;
            try {
                tx = s.beginTransaction();
                Product p = (Product) s.get(Product.class, Integer.parseInt(id));
                if (p != null) {
                    p.setOffer(0); // offer 0
                    Status activeStatus = (Status) s.get(Status.class, 1); // Active
                    p.setStatus_id(activeStatus);
                    s.update(p);
                    tx.commit();
                    obj.addProperty("status", true);
                    obj.addProperty("message", "Offer closed successfully");
                } else {
                    obj.addProperty("status", false);
                    obj.addProperty("message", "Product not found");
                }
            } catch (Exception e) {
                if (tx != null) {
                    tx.rollback();
                }
                obj.addProperty("status", false);
                obj.addProperty("message", "Error closing offer");
            } finally {
                s.close();
            }
        }
        response.setContentType("application/json");
        response.getWriter().write(obj.toString());
    }
}
