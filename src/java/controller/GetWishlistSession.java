package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.User;
import hibernate.Wishlist;
import java.io.IOException;
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

@WebServlet(name = "GetWishlistSession", urlPatterns = {"/GetWishlistSession"})
public class GetWishlistSession extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            responseObject.addProperty("message", "User not logged in.");
            sendResponse(response, gson, responseObject);
            return;
        }

        ArrayList<Wishlist> sessionWishlist = (ArrayList<Wishlist>) request.getSession().getAttribute("sessionWishlist");

        if (sessionWishlist == null || sessionWishlist.isEmpty()) {
            responseObject.addProperty("message", "Session wishlist is empty.");
            sendResponse(response, gson, responseObject);
            return;
        }


        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Transaction tr = null;

        try {
            tr = session.beginTransaction();

            for (Wishlist wishlistSession : sessionWishlist) {

                if (wishlistSession.getProduct_id() == null || wishlistSession.getProduct_id().getId() <= 0) {
                    continue; // Skip invalid wishlist entries
                }

                Product currentProduct = (Product) session.get(Product.class, wishlistSession.getProduct_id().getId());

                if (currentProduct == null) {
                    continue; // Skip if product doesn't exist in DB
                }

                Criteria criteria = session.createCriteria(Wishlist.class);
                criteria.add(Restrictions.eq("user_id", user));
                criteria.add(Restrictions.eq("product_id", currentProduct));

                if (criteria.list().isEmpty()) {
                    Wishlist newWish = new Wishlist();
                    newWish.setUser_id(user);
                    newWish.setProduct_id(currentProduct);
                    session.save(newWish);
                } 
            }

            tr.commit();
            request.getSession().removeAttribute("sessionWishlist");
            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Wishlist synced");
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            responseObject.addProperty("message", "Error syncing wishlist: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }

        sendResponse(response, gson, responseObject);
    }

    private void sendResponse(HttpServletResponse response, Gson gson, JsonObject responseObject)
            throws IOException {
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}