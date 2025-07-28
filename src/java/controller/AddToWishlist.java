package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AddToWishlist", urlPatterns = {"/AddToWishlist"})
public class AddToWishlist extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String prID = request.getParameter("prId");
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (!Util.isInteger(prID)) {
            responseObject.addProperty("message", "Invalid Game");
            sendResponse(response, gson, responseObject);
            return;
        }

        SessionFactory sf = hibernate.HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Transaction tr = null;

        try {
            tr = session.beginTransaction();
            Product product = (Product) session.get(Product.class, Integer.valueOf(prID));

            if (product == null) {
                responseObject.addProperty("message", "Invalid Game");
                sendResponse(response, gson, responseObject);
                return;
            }

            User user = (User) request.getSession().getAttribute("user");
            if (user != null) { // User is logged in
                Criteria criteriaUser = session.createCriteria(Wishlist.class);
                criteriaUser.add(Restrictions.eq("user_id", user));
                criteriaUser.add(Restrictions.eq("product_id", product));

                if (criteriaUser.list().isEmpty()) {
                    Wishlist wishlist = new Wishlist();
                    wishlist.setUser_id(user);
                    wishlist.setProduct_id(product);
                    session.save(wishlist);
                    tr.commit();
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Game Saved to Wishlist");
                } else {
                    responseObject.addProperty("message", "Game Already in Wishlist");
                }
            } else { // No user logged in
                HttpSession ses = request.getSession();
                ArrayList<Wishlist> sessionWishlist = (ArrayList<Wishlist>) ses.getAttribute("sessionWishlist");

                if (sessionWishlist == null) {
                    sessionWishlist = new ArrayList<>();
                    ses.setAttribute("sessionWishlist", sessionWishlist);
                }

                // Check for duplicates in session wishlist
                boolean exists = sessionWishlist.stream()
                        .anyMatch(w -> w.getProduct_id().getId() == product.getId());

                if (!exists) {
                    Wishlist wishlist = new Wishlist();
                    wishlist.setUser_id(null);
                    wishlist.setProduct_id(product);
                    sessionWishlist.add(wishlist);
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Game Saved");
                } else {
                    responseObject.addProperty("message", "Game Already Saved");
                }

            }
        } catch (Exception e) {
            if (tr != null) {
                tr.rollback();
            }
            responseObject.addProperty("message", "Error: " + e.getMessage());
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
