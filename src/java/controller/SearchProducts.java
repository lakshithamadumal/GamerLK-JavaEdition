package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.HibernateUtil;
import hibernate.Mode;
import hibernate.Product;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Laky
 */
@WebServlet(name = "SearchProducts", urlPatterns = {"/SearchProducts"})
public class SearchProducts extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            Criteria criteriaProduct = s.createCriteria(Product.class);

            // Category filter
            if (requestJsonObject.has("gameCategory")) {
                String categoryName = requestJsonObject.get("gameCategory").getAsString();
                Criteria criteriaCategory = s.createCriteria(Category.class);
                criteriaCategory.add(Restrictions.eq("name", categoryName));
                List<Category> categoryList = criteriaCategory.list();

                if (!categoryList.isEmpty()) {
                    criteriaProduct.add(Restrictions.eq("category", categoryList.get(0)));
                }
            }

            // Mode filter
            if (requestJsonObject.has("gameMod")) {
                String modeName = requestJsonObject.get("gameMod").getAsString();
                Criteria criteriaMode = s.createCriteria(Mode.class);
                criteriaMode.add(Restrictions.eq("name", modeName));
                List<Mode> modeList = criteriaMode.list();

                if (!modeList.isEmpty()) {
                    criteriaProduct.add(Restrictions.eq("mode", modeList.get(0)));
                }
            }

            // Price filter
            if (requestJsonObject.has("pricevalue")) {
                double pricevalue = requestJsonObject.get("pricevalue").getAsDouble();
                criteriaProduct.add(Restrictions.le("price", pricevalue));
            }

            // Sorting
            if (requestJsonObject.has("gamesort")) {
                String sortText = requestJsonObject.get("gamesort").getAsString();

                switch (sortText) {
                    case "Sort by Latest":
                        criteriaProduct.addOrder(Order.desc("id"));
                        break;
                    case "Sort by Oldest":
                        criteriaProduct.addOrder(Order.asc("id"));
                        break;
                    case "Sort by Name":
                        criteriaProduct.addOrder(Order.asc("title"));
                        break;
                    case "Sort by price":
                        criteriaProduct.addOrder(Order.asc("price"));
                        break;
                }
            }

            // Get full count
            List<Product> allProducts = criteriaProduct.list();
            responseObject.addProperty("allProductCount", allProducts.size());

            // Pagination
            if (requestJsonObject.has("firstResult")) {
                int firstResult = requestJsonObject.get("firstResult").getAsInt();
                criteriaProduct.setFirstResult(firstResult);
                criteriaProduct.setMaxResults(6);
            }

            // Final product list
            List<Product> productList = criteriaProduct.list();
            responseObject.add("productList", gson.toJsonTree(productList));
            responseObject.addProperty("status", true);

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("error", e.getMessage());
        } finally {
            s.close();
        }

        // Send response
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
