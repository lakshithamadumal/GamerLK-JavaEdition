package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author Laky
 */
@WebServlet(name = "AdminLoadGamesOffer", urlPatterns = {"/AdminLoadGamesOffer"})
public class AdminLoadGamesOffer extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        //Search Product
        Criteria criteriaProduct = s.createCriteria(Product.class, "product");
        criteriaProduct.createAlias("status_id", "status");
        criteriaProduct.add(
            org.hibernate.criterion.Restrictions.in(
                "status.value",
                new String[]{"Active", "Inactive"}
            )
        );
        List<Product> GameList = criteriaProduct.list();

        s.close();

        Gson gson = new Gson();

        responseObject.add("GameList", gson.toJsonTree(GameList));

        responseObject.addProperty("status", Boolean.TRUE);

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
