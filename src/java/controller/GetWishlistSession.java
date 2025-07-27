package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Laky
 */
@WebServlet(name = "GetWishlistSession", urlPatterns = {"/GetWishlistSession"})
public class GetWishlistSession extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        User user = (User) request.getSession().getAttribute("user");

        if (user != null) {
            ArrayList<Wishlist> sessionWishlist = (ArrayList<Wishlist>) request.getSession().getAttribute("sessionWishlist");

            if (sessionWishlist != null && !sessionWishlist.isEmpty()) {
                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session session = sf.openSession();
                Transaction tr = session.beginTransaction();

                try {
                    for (Wishlist wishlistSession : sessionWishlist) {

                        // Attach user and product to current session
                        User currentUser = (User) session.get(User.class, user.getId());
                        Product currentProduct = (Product) session.get(Product.class, wishlistSession.getProduct_id().getId());

                        Criteria criteria = session.createCriteria(Wishlist.class);
                        criteria.add(Restrictions.eq("user_id", currentUser));
                        criteria.add(Restrictions.eq("product_id", currentProduct));

                        if (criteria.list().isEmpty()) {
                            Wishlist newWish = new Wishlist();
                            newWish.setUser_id(currentUser);
                            newWish.setProduct_id(currentProduct);
                            session.save(newWish);
                        }

                        // Debug log
                        System.out.println("Processed wishlist: P" + currentProduct.getId() + " U" + currentUser.getId());
                    }

                    tr.commit();
                    session.flush();
                    session.clear();
                    session.close();

                    request.getSession().removeAttribute("sessionWishlist");

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Wishlist synced.");
                } catch (Exception e) {
                    tr.rollback();
                    responseObject.addProperty("message", "Error syncing wishlist: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                responseObject.addProperty("message", "Session wishlist empty.");
            }
        } else {
            responseObject.addProperty("message", "User not logged in.");
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
