package controller;

import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItem;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "ProductOrderCountServlet", urlPatterns = {"/ProductOrderCount"})
public class ProductOrderCountServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String productId = request.getParameter("id");
        JsonObject obj = new JsonObject();
        int count = 0;

        if (productId != null && productId.matches("\\d+")) {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            try {
                Criteria c = s.createCriteria(OrderItem.class);
                c.add(Restrictions.eq("product_id.id", Integer.valueOf(productId)));
                count = c.list().size();
                obj.addProperty("status", true);
                obj.addProperty("count", count);
            } catch (Exception e) {
                obj.addProperty("status", false);
                obj.addProperty("count", 0);
            } finally {
                s.close();
            }
        } else {
            obj.addProperty("status", false);
            obj.addProperty("count", 0);
        }

        response.setContentType("application/json");
        response.getWriter().write(obj.toString());
    }
}