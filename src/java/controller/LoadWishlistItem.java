package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.User;
import hibernate.Wishlist;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Laky
 */
@WebServlet(name = "LoadWishlistItem", urlPatterns = {"/LoadWishlistItem"})
public class LoadWishlistItem extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        User user = (User) request.getSession().getAttribute("user");
        if (user != null) { //DB wishlist
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Criteria criteriaWishlist = s.createCriteria(Wishlist.class);
            criteriaWishlist.add(Restrictions.eq("user_id", user));
            List<Wishlist> wishlistList = criteriaWishlist.list();

            if (wishlistList.isEmpty()) {
                responseObject.addProperty("message", "Your Wishlist is Empty");
            } else {
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "WishlistList items successfully loaded");
                responseObject.add("cartItems", gson.toJsonTree(wishlistList));
            }
        } else { //sessionCart
        }
        response.setContentType("application/json");
        String responseText = gson.toJson(responseObject);
        response.getWriter().write(responseText);
    }

}
