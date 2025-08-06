package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Laky
 */
@WebServlet(name = "LoadSingleOfferProduct", urlPatterns = {"/LoadSingleOfferProduct"})
public class LoadSingleOfferProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        String productId = request.getParameter("id");

        if (Util.isInteger(productId)) {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            try {
                Product product = (Product) s.get(Product.class, Integer.valueOf(productId));
                if (product != null && product.getStatus_id().getValue().equals("Offer")) {

                    responseObject.add("product", gson.toJsonTree(product));
                    responseObject.addProperty("status", true);
                } else {
                    responseObject.addProperty("message", "Product Not Found!");
                }
            } catch (Exception e) {
                e.printStackTrace(); // optional for debugging
                responseObject.addProperty("message", "Product Not Found!");
            } finally {
                s.close();
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
