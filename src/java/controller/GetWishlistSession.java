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

        System.out.println("GetWishlistSession: doGet called");

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        User user = (User) request.getSession().getAttribute("user");
        System.out.println("User from session: " + (user != null ? user.getId() : "null"));

        if (user == null) {
            System.out.println("No user logged in.");
            responseObject.addProperty("message", "User not logged in.");
            sendResponse(response, gson, responseObject);
            return;
        }

        ArrayList<Wishlist> sessionWishlist = (ArrayList<Wishlist>) request.getSession().getAttribute("sessionWishlist");
        System.out.println("SessionWishlist from session: " + (sessionWishlist != null ? sessionWishlist.size() : "null"));

        if (sessionWishlist == null || sessionWishlist.isEmpty()) {
            System.out.println("Session wishlist is empty.");
            responseObject.addProperty("message", "Session wishlist is empty.");
            sendResponse(response, gson, responseObject);
            return;
        }

        // Debug: Print session wishlist contents
        System.out.println("Session Wishlist Contents BEFORE SYNC:============================");
        for (Wishlist w : sessionWishlist) {
            System.out.println("Product ID: " + (w.getProduct_id() != null ? w.getProduct_id().getId() : "null"));
        }

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Transaction tr = null;

        try {
            tr = session.beginTransaction();
            System.out.println("Hibernate transaction started.");

            for (Wishlist wishlistSession : sessionWishlist) {
                System.out.println("Checking wishlistSession: " + wishlistSession);

                if (wishlistSession.getProduct_id() == null || wishlistSession.getProduct_id().getId() <= 0) {
                    System.out.println("Invalid product in wishlistSession, skipping.");
                    continue; // Skip invalid wishlist entries
                }

                Product currentProduct = (Product) session.get(Product.class, wishlistSession.getProduct_id().getId());
                System.out.println("Current product from DB: " + (currentProduct != null ? currentProduct.getId() : "null"));

                if (currentProduct == null) {
                    System.out.println("Product not found in DB, skipping.");
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
                    System.out.println("Synced wishlist: Product ID " + currentProduct.getId() + " for User ID " + user.getId());
                } else {
                    System.out.println("Wishlist already exists for Product ID " + currentProduct.getId());
                }
            }

            tr.commit();
            System.out.println("Transaction committed. Removing sessionWishlist from session.");
            request.getSession().removeAttribute("sessionWishlist");
            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Wishlist synced successfully.");
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.out.println("Exception occurred: " + e.getMessage());
            responseObject.addProperty("message", "Error syncing wishlist: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
            System.out.println("Hibernate session closed.");
        }

        sendResponse(response, gson, responseObject);
    }

    private void sendResponse(HttpServletResponse response, Gson gson, JsonObject responseObject)
            throws IOException {
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}