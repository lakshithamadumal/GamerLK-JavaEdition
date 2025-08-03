package controller;

import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItem;
import hibernate.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SaveRating", urlPatterns = {"/SaveRating"})
public class SaveRating extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        JsonObject resp = new JsonObject();
        resp.addProperty("status", false);

        HttpSession httpSession = request.getSession(false);
        User user = (httpSession != null) ? (User) httpSession.getAttribute("user") : null;

        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            int productId = Integer.parseInt(request.getParameter("productId"));
            int rating = Integer.parseInt(request.getParameter("rating"));

            if (user == null) {
                resp.addProperty("message", "User not logged in");
            } else {
                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();
                Transaction tx = s.beginTransaction();

                Criteria criteria = s.createCriteria(OrderItem.class, "oi")
                    .createAlias("orders_id", "o")
                    .createAlias("product_id", "p")
                    .add(Restrictions.eq("o.id", orderId))
                    .add(Restrictions.eq("p.id", productId))
                    .add(Restrictions.eq("o.user_id.id", user.getId()));

                OrderItem oi = (OrderItem) criteria.uniqueResult();

                if (oi != null) {
                    if (oi.getRating() == 0) {
                        oi.setRating(rating);
                        s.update(oi);
                        tx.commit();
                        resp.addProperty("status", true);
                    } else {
                        resp.addProperty("message", "Already rated");
                    }
                } else {
                    resp.addProperty("message", "Order not found");
                }
                s.close();
            }
        } catch (Exception e) {
            resp.addProperty("message", "Error saving rating");
        }

        response.setContentType("application/json");
        response.getWriter().write(resp.toString());
    }
}