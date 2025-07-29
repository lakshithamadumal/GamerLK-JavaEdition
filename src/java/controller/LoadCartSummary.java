package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadCartSummary", urlPatterns = {"/LoadCartSummary"})
public class LoadCartSummary extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        Gson gson = new Gson();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user != null) {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria criteriaCart = s.createCriteria(Cart.class);
            criteriaCart.add(Restrictions.eq("user_id", user));
            List<Cart> cartList = criteriaCart.list();

            JsonArray cartItemsArray = new JsonArray();
            double totalPrice = 0.0;

            for (Cart cart : cartList) {
                Product p = cart.getProduct_id();

                JsonObject itemObj = new JsonObject();
                itemObj.addProperty("id", p.getId());
                itemObj.addProperty("name", p.getTitle());
                itemObj.addProperty("price", p.getPrice());

                totalPrice += p.getPrice();
                cartItemsArray.add(itemObj);
            }

            responseObject.addProperty("status", true);
            responseObject.add("cartItems", cartItemsArray);
            responseObject.addProperty("totalPrice", totalPrice);
            responseObject.addProperty("userEmail", user.getEmail()); // Optional

            s.close();
        } else {
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "User not logged in.");
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
