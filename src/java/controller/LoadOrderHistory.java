package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItem;
import hibernate.Orders;
import hibernate.Product;
import hibernate.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadOrderHistory", urlPatterns = {"/LoadOrderHistory"})
public class LoadOrderHistory extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject resp = new JsonObject();
        resp.addProperty("status", false);

        HttpSession httpSession = request.getSession(false);
        User user = (httpSession != null) ? (User) httpSession.getAttribute("user") : null;

        int page = 1;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (Exception e) {
            // default page 1
        }

        if (user == null) {
            resp.addProperty("message", "User not logged in");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            try {
                // Count total order items for this user
                Criteria countCriteria = s.createCriteria(OrderItem.class, "oi")
                        .createAlias("orders_id", "o")
                        .add(Restrictions.eq("o.user_id.id", user.getId()))
                        .setProjection(Projections.rowCount());
                Long totalCount = (Long) countCriteria.uniqueResult();

                // Get paginated order items for this user
                Criteria criteria = s.createCriteria(OrderItem.class, "oi")
                        .createAlias("orders_id", "o")
                        .createAlias("product_id", "p")
                        .add(Restrictions.eq("o.user_id.id", user.getId()))
                        .addOrder(Order.desc("o.created_at"))
                        .addOrder(Order.desc("oi.id"))
                        .setFirstResult((page - 1) * PAGE_SIZE)
                        .setMaxResults(PAGE_SIZE);

                @SuppressWarnings("unchecked")
                List<OrderItem> items = criteria.list();

                JsonArray arr = new JsonArray();
                for (OrderItem oi : items) {
                    Orders order = oi.getOrders_id();
                    Product prod = oi.getProduct_id();

                    JsonObject obj = new JsonObject();
                    obj.addProperty("orderId", order.getId());
                    obj.addProperty("orderIdStr", String.format("#GKLK-2025-%04d", order.getId()));
                    obj.addProperty("gameTitle", prod.getTitle());
                    obj.addProperty("gameImg", "../../assets/Games/" + prod.getId() + "/thumb-image.jpg");
                    obj.addProperty("price", prod.getPrice());
                    obj.addProperty("purchaseDate", order.getCreated_at().toString());
                    obj.addProperty("productId", prod.getId());
                    obj.addProperty("rating", oi.getRating());
                    arr.add(obj);
                }

                resp.add("orderItems", arr);
                resp.addProperty("total", totalCount);
                resp.addProperty("page", page);
                resp.addProperty("pageSize", PAGE_SIZE);
                resp.addProperty("status", true);

            } catch (Exception e) {
                resp.addProperty("message", "Error loading order history");
            } finally {
                s.close();
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(resp));
    }
}
