package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.Status;
import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AdminCreateOffer", urlPatterns = {"/AdminCreateOffer"})
public class AdminCreateOffer extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JsonObject obj = new JsonObject();

        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        JsonObject json = gson.fromJson(sb.toString(), JsonObject.class);

        String gameId = json.has("gameId") ? json.get("gameId").getAsString() : null;
        String gameDiscount = json.has("gameDiscount") ? json.get("gameDiscount").getAsString() : null;

        // Admin session check
        if (request.getSession().getAttribute("admin") == null) {
            obj.addProperty("status", false);
            obj.addProperty("message", "Admin not found");
        }else if (gameId == null || gameId.trim().isEmpty()) {
            obj.addProperty("status", false);
            obj.addProperty("message", "Product ID is required");
        } else if (gameDiscount == null || gameDiscount.trim().isEmpty()) {
            obj.addProperty("status", false);
            obj.addProperty("message", "Discount value is required");
        } else if (!gameId.matches("\\d+")) {
            obj.addProperty("status", false);
            obj.addProperty("message", "Invalid product ID");
        } else if (!gameDiscount.matches("\\d+")) {
            obj.addProperty("status", false);
            obj.addProperty("message", "Invalid discount value");
        } else {
            int discount = Integer.parseInt(gameDiscount);
            if (discount < 1 || discount > 100) {
                obj.addProperty("status", false);
                obj.addProperty("message", "Discount must be between 1 and 100");
            } else {
                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();
                Transaction tx = null;
                try {
                    Product product = (Product) s.get(Product.class, Integer.parseInt(gameId));

                    if (product == null) {
                        obj.addProperty("status", false);
                        obj.addProperty("message", "Product not found");
                    } else {
                        String statusValue = product.getStatus_id() != null ? product.getStatus_id().getValue() : "";
                        if (!("Active".equalsIgnoreCase(statusValue) || "Inactive".equalsIgnoreCase(statusValue))) {
                            obj.addProperty("status", false);
                            obj.addProperty("message", "Only Active or Inactive products can be offered");
                        } else {
                            Criteria statusCriteria = s.createCriteria(Status.class);
                            statusCriteria.add(org.hibernate.criterion.Restrictions.eq("value", "Offer"));
                            Status offerStatus = (Status) statusCriteria.uniqueResult();

                            if (offerStatus == null) {
                                obj.addProperty("status", false);
                                obj.addProperty("message", "Offer status not found in DB");
                            } else {
                                tx = s.beginTransaction();
                                product.setStatus_id(offerStatus);
                                product.setOffer(discount);
                                s.update(product);
                                tx.commit();

                                obj.addProperty("status", true);
                                obj.addProperty("message", "Offer created successfully");
                            }
                        }
                    }
                } catch (Exception e) {
                    if (tx != null) {
                        tx.rollback();
                    }
                    obj.addProperty("status", false);
                    obj.addProperty("message", "Error creating offer");
                } finally {
                    s.close();
                }
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(obj.toString());
    }
}
