package controller;

import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItem;
import hibernate.Product;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.*;

import org.hibernate.criterion.Restrictions;

@WebServlet(name = "ProductRatingServlet", urlPatterns = {"/ProductRating"})
public class ProductRatingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String productId = request.getParameter("id");
        JsonObject obj = new JsonObject();
        double avgRating = 0.0;

        if (productId != null && productId.matches("\\d+")) {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            try {
                Criteria c = s.createCriteria(OrderItem.class);
                c.add(Restrictions.eq("product_id.id", Integer.valueOf(productId)));
                c.add(Restrictions.gt("rating", 0)); // Only rated items

                List<OrderItem> items = c.list();
                int total = 0;
                int count = 0;
                for (OrderItem oi : items) {
                    total += oi.getRating();
                    count++;
                }
                if (count > 0) {
                    avgRating = (double) total / count;
                    // Round to 1 decimal
                    BigDecimal bd = new BigDecimal(avgRating).setScale(1, RoundingMode.HALF_UP);
                    avgRating = bd.doubleValue();
                    // Never exceed 5.0
                    avgRating = Math.min(avgRating, 5.0);
                }
                obj.addProperty("status", true);
                obj.addProperty("rating", avgRating);
                obj.addProperty("count", count);
            } catch (Exception e) {
                obj.addProperty("status", false);
                obj.addProperty("rating", 0.0);
                obj.addProperty("count", 0);
            } finally {
                s.close();
            }
        } else {
            obj.addProperty("status", false);
            obj.addProperty("rating", 0.0);
            obj.addProperty("count", 0);
        }

        response.setContentType("application/json");
        response.getWriter().write(obj.toString());
    }
}