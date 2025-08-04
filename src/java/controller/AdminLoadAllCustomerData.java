package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.User;
import hibernate.Wishlist;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Laky
 */
@WebServlet(name = "AdminLoadAllCustomerData", urlPatterns = {"/AdminLoadAllCustomerData"})
public class AdminLoadAllCustomerData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            Criteria criteriaUser = s.createCriteria(User.class);
            List<User> userList = criteriaUser.list();

            JsonArray userArray = new JsonArray();

            for (User user : userList) {
                JsonObject userObj = new JsonObject();
                userObj.addProperty("id", user.getId());
                userObj.addProperty("name", user.getFirst_name() + " " + user.getLast_name());
                userObj.addProperty("email", user.getEmail());
                userObj.addProperty("status", user.getStatus() != null ? user.getStatus().getValue() : "Unknown");

                // Wishlist count
                Criteria wishlistCriteria = s.createCriteria(Wishlist.class);
                wishlistCriteria.add(Restrictions.eq("user_id", user));
                int wishlistCount = wishlistCriteria.list().size();
                userObj.addProperty("wishlistCount", wishlistCount);

                // Cart count
                Criteria cartCriteria = s.createCriteria(Cart.class);
                cartCriteria.add(Restrictions.eq("user_id", user));
                int cartCount = cartCriteria.list().size();
                userObj.addProperty("cartCount", cartCount);

                // Downloads & Spend calculation
                Criteria ordersCriteria = s.createCriteria(hibernate.Orders.class);
                ordersCriteria.add(Restrictions.eq("user_id", user));
                List<hibernate.Orders> ordersList = ordersCriteria.list();

                int downloads = 0;
                double spend = 0.0;

                for (hibernate.Orders order : ordersList) {
                    Criteria orderItemCriteria = s.createCriteria(hibernate.OrderItem.class);
                    orderItemCriteria.add(Restrictions.eq("orders_id", order));
                    List<hibernate.OrderItem> orderItems = orderItemCriteria.list();
                    downloads += orderItems.size();
                    for (hibernate.OrderItem item : orderItems) {
                        hibernate.Product product = item.getProduct_id();
                        if (product != null) {
                            spend += product.getPrice();
                        }
                    }
                }
                userObj.addProperty("downloads", downloads);
                userObj.addProperty("spend", spend);

                userArray.add(userObj);
            }

            responseObject.add("userList", userArray);
            responseObject.addProperty("status", true);

        } catch (Exception e) {
            responseObject.addProperty("message", "Error loading data!");
        } finally {
            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
