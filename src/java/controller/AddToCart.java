package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Admin;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.User;
import java.io.IOException;
import java.util.List;
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
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Laky
 */
@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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
            } else {
                User user = (User) request.getSession().getAttribute("user");

                if (user != null) {

                    Criteria criteriaUser = session.createCriteria(Cart.class);
                    criteriaUser.add(Restrictions.eq("user_id", user));
                    criteriaUser.add(Restrictions.eq("product_id", product));

                    if (criteriaUser.list().isEmpty()) {//product not available 

                        Cart cart = new Cart();
                        cart.setUser_id(user);
                        cart.setProduct_id(product);

                        session.save(cart);
                        tr.commit();
                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "Game Added to cart");

                    } else { //product available 
                        responseObject.addProperty("message", "Already Added");

                    }
                } else {
                    responseObject.addProperty("message", "Login Required!");
                }
            }
        }

        responseObject.addProperty("status", true);

        //addToCartProcess
        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);
    }

}
