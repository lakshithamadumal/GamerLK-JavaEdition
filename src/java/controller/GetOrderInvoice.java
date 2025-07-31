package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.OrderItem;
import hibernate.Orders;
import hibernate.Product;
import hibernate.Developer;
import hibernate.User;
import hibernate.HibernateUtil;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "GetOrderInvoice", urlPatterns = {"/GetOrderInvoice"})
public class GetOrderInvoice extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String orderIdStr = request.getParameter("orderId");
        if (orderIdStr == null) {
            response.setStatus(400);
            return;
        }
        int orderId = Integer.parseInt(orderIdStr);

        // Get logged-in user from session
        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute("user") == null) {
            response.setStatus(401); // Unauthorized
            return;
        }
        User sessionUser = (User) httpSession.getAttribute("user");

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Orders order = (Orders) s.get(Orders.class, orderId);
        if (order == null) {
            s.close();
            response.setStatus(404);
            return;
        }
        User orderUser = order.getUser_id();

        // Check if the logged-in user is the owner of the order
        if (orderUser.getId() != sessionUser.getId()) {
            s.close();
            response.setStatus(403); // Forbidden
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("userName", orderUser.getFirst_name() + " " + orderUser.getLast_name());
        json.addProperty("userEmail", orderUser.getEmail());
        json.addProperty("orderNumber", "GKLK-2025-" + String.format("%04d", orderId));
        json.addProperty("dueDate", new SimpleDateFormat("yyyy-MM-dd").format(order.getCreated_at()));

        Criteria criteria = s.createCriteria(OrderItem.class);
        criteria.add(Restrictions.eq("orders_id", order));
        List<OrderItem> items = criteria.list();

        JsonArray arr = new JsonArray();
        for (OrderItem oi : items) {
            Product p = oi.getProduct_id();
            Developer dev = p.getDeveloper_id();
            JsonObject obj = new JsonObject();
            obj.addProperty("productName", p.getTitle());
            obj.addProperty("developer", dev != null ? dev.getName() : "");
            obj.addProperty("price", p.getPrice());
            arr.add(obj);
        }
        json.add("items", arr);

        response.setContentType("application/json");
        response.getWriter().write(json.toString());
        s.close();
    }
}
