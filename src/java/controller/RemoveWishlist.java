package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.User;
import hibernate.Wishlist;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Laky
 */
@WebServlet(name = "RemoveWishlist", urlPatterns = {"/RemoveWishlist"})
public class RemoveWishlist extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String prID = request.getParameter("prId");
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (!Util.isInteger(prID)) {
            responseObject.addProperty("message", "Invalid Game");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();
            Transaction tr = session.beginTransaction();

            Product product = (Product) session.get(Product.class, Integer.valueOf(prID));
            if (product == null) {
                responseObject.addProperty("message", "Invalid Game");
            } else {
                User user = (User) request.getSession().getAttribute("user");
                if (user != null) {
                    Criteria criteria = session.createCriteria(Wishlist.class);
                    criteria.add(Restrictions.eq("user_id", user));
                    criteria.add(Restrictions.eq("product_id", product));
                    Wishlist wishlist = (Wishlist) criteria.uniqueResult();

                    if (wishlist != null) {
                        session.delete(wishlist);
                        tr.commit();
                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "Game Removed from Wishlist");
                    } else {
                        responseObject.addProperty("message", "Already Removed from Wishlist");
                    }
                } else {
                    // Remove from sessionWishlist if exists
                    ArrayList<Wishlist> sessionWishlist = (ArrayList<Wishlist>) request.getSession().getAttribute("sessionWishlist");
                    if (sessionWishlist != null) {
                        sessionWishlist.removeIf(w -> w.getProduct_id() != null && w.getProduct_id().getId() == product.getId());
                        request.getSession().setAttribute("sessionWishlist", sessionWishlist);
                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "Game Removed from Wishlist");
                    } else {
                        responseObject.addProperty("message", "Already Removed from Wishlist");
                    }
                }
            }
            session.close();
        }

        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);
    }

}
