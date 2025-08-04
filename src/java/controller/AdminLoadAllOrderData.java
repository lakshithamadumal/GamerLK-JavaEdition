package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.Orders;
import hibernate.OrderItem;
import hibernate.Product;
import hibernate.User;
import hibernate.Status;
import hibernate.HibernateUtil;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.*;

@WebServlet(name = "AdminLoadAllOrderData", urlPatterns = {"/AdminLoadAllOrderData"})
public class AdminLoadAllOrderData extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            Criteria criteriaOrder = s.createCriteria(Orders.class);
            List<Orders> orderList = criteriaOrder.list();

            JsonArray orderArray = new JsonArray();

            for (Orders order : orderList) {
                JsonObject orderObj = new JsonObject();
                orderObj.addProperty("id", order.getId());
                User user = order.getUser_id();
                orderObj.addProperty("customer", user != null ? user.getFirst_name() + " " + user.getLast_name() : "-");
                orderObj.addProperty("customerId", user != null ? user.getId() : 0);

                // Get order items
                Criteria itemCriteria = s.createCriteria(OrderItem.class);
                itemCriteria.add(org.hibernate.criterion.Restrictions.eq("orders_id", order));
                List<OrderItem> items = itemCriteria.list();

                double totalPrice = 0.0;
                JsonArray gamesArray = new JsonArray();
                for (OrderItem item : items) {
                    Product p = item.getProduct_id();
                    if (p != null) {
                        JsonObject gameObj = new JsonObject();
                        gameObj.addProperty("title", p.getTitle());
                        gameObj.addProperty("price", p.getPrice());
                        gamesArray.add(gameObj);
                        totalPrice += p.getPrice();
                    }
                }
                orderObj.add("games", gamesArray);
                orderObj.addProperty("price", totalPrice);
                orderObj.addProperty("items", items.size());

                // Date
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
                orderObj.addProperty("created", order.getCreated_at() != null ? sdf.format(order.getCreated_at()) : "-");

                // Status
                orderObj.addProperty("status", "Completed"); // Change if you have status field

                orderArray.add(orderObj);
            }

            responseObject.add("orderList", orderArray);
            responseObject.addProperty("status", true);

        } catch (Exception e) {
            responseObject.addProperty("message", "Error loading order data!");
        } finally {
            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}