package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import hibernate.Cart;
import hibernate.Wishlist;
import hibernate.OrderItem;

/**
 *
 * @author Laky
 */
@WebServlet(name = "GetUserDetails", urlPatterns = {"/GetUserDetails"})
public class GetUserDetails extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession ses = request.getSession(false);

        if (ses != null && ses.getAttribute("user") != null) {
            User user = (User) ses.getAttribute("user");
            JsonObject responseObject = new JsonObject();

            responseObject.addProperty("firstName", user.getFirst_name());
            responseObject.addProperty("lastName", user.getLast_name());
            responseObject.addProperty("email", user.getEmail());
            responseObject.addProperty("password", user.getPassword());
            responseObject.addProperty("status", user.getStatus().getValue());

            // --- Add counts using Criteria ---
            Session hibSession = HibernateUtil.getSessionFactory().openSession();

            // Cart count
            Long cartCount = (Long) hibSession.createCriteria(Cart.class)
                    .add(Restrictions.eq("user_id", user))
                    .setProjection(Projections.rowCount())
                    .uniqueResult();

            // Wishlist count
            Long wishlistCount = (Long) hibSession.createCriteria(Wishlist.class)
                    .add(Restrictions.eq("user_id", user))
                    .setProjection(Projections.rowCount())
                    .uniqueResult();

            // Download count (OrderItem)
            Long downloadCount = (Long) hibSession.createCriteria(OrderItem.class)
                    .createAlias("orders_id", "o")
                    .add(Restrictions.eq("o.user_id", user))
                    .setProjection(Projections.rowCount())
                    .uniqueResult();

            hibSession.close();

            responseObject.addProperty("cart", cartCount);
            responseObject.addProperty("wishlist", wishlistCount);
            responseObject.addProperty("downloads", downloadCount);

            Gson gson = new Gson();
            String toJson = gson.toJson(responseObject);
            response.setContentType("application/json");
            response.getWriter().write(toJson);

        }
    }

}
