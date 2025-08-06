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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject obj = new JsonObject();
        Gson gson = new Gson();

        // Admin session check
        if (request.getSession().getAttribute("admin") == null) {
            obj.addProperty("status", false);
            obj.addProperty("message", "Admin not found");
            response.setContentType("application/json");
            response.getWriter().write(obj.toString());
        } else {

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

            if (gameId == null || gameId.trim().isEmpty()) {
                obj.addProperty("status", false);
                obj.addProperty("message", "Product id is required");
            } else if (gameDiscount == null || gameDiscount.trim().isEmpty()) {
                obj.addProperty("status", false);
                obj.addProperty("message", "Discount value is required");
            } else if (!gameId.matches("\\d+")) {
                obj.addProperty("status", false);
                obj.addProperty("message", "Invalid product id");
            } else if (!gameDiscount.matches("\\d+")) {
                obj.addProperty("status", false);
                obj.addProperty("message", "Invalid discount value");
            } else if (Integer.parseInt(gameDiscount) < 1 || Integer.parseInt(gameDiscount) > 100) {
                obj.addProperty("status", false);
                obj.addProperty("message", "Discount value must be between 1 and 100");
            } else {
                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();
                Transaction tx = null;
                try {
                    Criteria criteria = s.createCriteria(Product.class);
                    criteria.add(Restrictions.eq("id", Integer.parseInt(gameId)));
                    Product product = (Product) criteria.uniqueResult();

                    if (product == null) {
                        obj.addProperty("status", false);
                        obj.addProperty("message", "Product not found");
                    } else {
                        String statusValue = product.getStatus_id() != null ? product.getStatus_id().getValue() : "";
                        if (!("Active".equalsIgnoreCase(statusValue) || "Inactive".equalsIgnoreCase(statusValue))) {
                            obj.addProperty("status", false);
                            obj.addProperty("message", "Only Active or Inactive games can be set as Offer");
                        } else {
                            tx = s.beginTransaction();
                            Criteria statusCriteria = s.createCriteria(Status.class);
                            statusCriteria.add(Restrictions.eq("value", "Offer"));
                            Status offerStatus = (Status) statusCriteria.uniqueResult();

                            if (offerStatus == null) {
                                obj.addProperty("status", false);
                                obj.addProperty("message", "Offer status not found in DB");
                            } else {
                                product.setStatus_id(offerStatus);
                                product.setOffer(Integer.parseInt(gameDiscount));
                                s.update(product);
                                tx.commit();
                                obj.addProperty("status", true);
                                obj.addProperty("message", "Offer Created Successfully");
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
