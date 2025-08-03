package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItem;
import hibernate.Product;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Laky
 */
@WebServlet(name = "LoadPopularGmames", urlPatterns = {"/LoadPopularGmames"})
public class LoadPopularGmames extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            // Criteria for OrderItem, group by product, order by count desc, limit 4
            Criteria criteria = s.createCriteria(OrderItem.class, "orderItem")
                    .createAlias("product_id", "product")
                    .createAlias("product.status_id", "status")
                    .add(Restrictions.eq("status.value", "Active"))
                    .setProjection(Projections.projectionList()
                            .add(Projections.groupProperty("product_id"))
                            .add(Projections.count("product_id").as("orderCount"))
                    )
                    .addOrder(Order.desc("orderCount"))
                    .setMaxResults(4);

            List<Object[]> result = criteria.list();

            List<Product> popularProducts = new ArrayList<>();
            for (Object[] row : result) {
                Product p = (Product) row[0];
                popularProducts.add(p);
            }

            responseObject.add("productList", gson.toJsonTree(popularProducts));
            responseObject.addProperty("status", true);

        } catch (Exception e) {
            responseObject.addProperty("message", "Error loading data!");
        } finally {
            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
