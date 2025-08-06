package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.Status;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AdminLoadAllOfferData", urlPatterns = {"/AdminLoadAllOfferData"})
public class AdminLoadAllOfferData extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            // Status එක Offer තියෙන Product විතරක් ගන්න
            Criteria criteria = s.createCriteria(Product.class, "product");
            criteria.createAlias("status_id", "status");
            criteria.add(Restrictions.eq("status.value", "Offer"));
            List<Product> offerList = criteria.list();

            responseObject.add("offerList", gson.toJsonTree(offerList));
            responseObject.addProperty("status", true);
        } catch (Exception e) {
            responseObject.addProperty("message", "Error loading offers!");
        } finally {
            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}