package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.User;
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
@WebServlet(name = "LoadCartItem", urlPatterns = {"/LoadCartItem"})
public class LoadCartItem extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        User user = (User) request.getSession().getAttribute("user");
        if (user != null) { //DB Cart
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Criteria criteriaCart = s.createCriteria(Cart.class);
            criteriaCart.add(Restrictions.eq("user", user));
            List<Cart> cartList = criteriaCart.list();

            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Cart items successfully loaded");
            responseObject.add("cartItems", gson.toJsonTree(cartList));
        } else { //sessionCart
        }
        response.setContentType("application/json");
        String responseText = gson.toJson(responseObject);
        response.getWriter().write(responseText);
    }

}
