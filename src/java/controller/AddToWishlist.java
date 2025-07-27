package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
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

/**
 *
 * @author Laky
 */
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
            responseObject.addProperty("message", "Invalid1 Game");
        } else {

            //addToCartProcess
            SessionFactory sf = hibernate.HibernateUtil.getSessionFactory();
            Session session = sf.openSession();
            Transaction tr = session.beginTransaction();

            Product product = (Product) session.get(Product.class, Integer.valueOf(prID));

            if (product == null) {
                responseObject.addProperty("message", "Invalid1 Game");
            } else { //Game Avalible
                User user = (User) request.getSession().getAttribute("user");
                if (user != null) {  //User Have
                    Criteria criteriaUser = session.createCriteria(Wishlist.class);
                    criteriaUser.add(Restrictions.eq("user_id", user));
                    criteriaUser.add(Restrictions.eq("product_id", product));

                    if (criteriaUser.list().isEmpty()) {//product not available 

                        Wishlist wishlist = new Wishlist();
                        wishlist.setUser_id(user);
                        wishlist.setProduct_id(product);

                        session.save(wishlist);
                        tr.commit();
                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "Game Saved");

                    } else { //product available 
                        responseObject.addProperty("message", "Already Saaved");
                    }
                } else {//Haven't user
                    //Not have user
                    HttpSession ses = request.getSession();

                    if (ses.getAttribute("sessionWishlist") == null) { // sessionwashlist not available in the session
                        ArrayList<Wishlist> sessionWishlist = new ArrayList<>();
                        Wishlist wishlist = new Wishlist();
                        wishlist.setUser_id(null);
                        wishlist.setProduct_id(product);
                        sessionWishlist.add(wishlist);
                        ses.setAttribute("sessionWishlist", sessionWishlist);
                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "Game Saved");
                    } else { // sessionCart available
                        ArrayList<Wishlist> sessionList = (ArrayList<Wishlist>) ses.getAttribute("sessionWishlist");

                        Wishlist foundedWishlist = null;

                        for (Wishlist wishlist : sessionList) {
                            if (wishlist.getProduct_id().getId() == product.getId()) { // Wishlist has product
                                foundedWishlist = wishlist;
                                break;
                            }
                        }

                        if (foundedWishlist != null) {
                            responseObject.addProperty("message", "Already Saaved");
                        } else {
                            foundedWishlist = new Wishlist();
                            foundedWishlist.setUser_id(null);
                            foundedWishlist.setProduct_id(product);
                            sessionList.add(foundedWishlist);
                        }
                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "Game Saved");
                    }
                }
            }

        }

        responseObject.addProperty("status", true);
        //addTowishlistProcess
        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);
    }

}
