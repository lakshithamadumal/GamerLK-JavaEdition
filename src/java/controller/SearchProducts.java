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
                int gameCategoryId = requestJsonObject.get("gameCategory").getAsInt();
                if (gameCategoryId != 0) { // Only filter if not 'All'
                    Criteria criteriaCategory = s.createCriteria(Category.class);
                    criteriaCategory.add(Restrictions.eq("id", gameCategoryId));
                    Category categoryList = (Category) criteriaCategory.uniqueResult();
                    criteriaProduct.add(Restrictions.eq("category_id", categoryList));
                }
            }

            // Mode filter
            if (requestJsonObject.has("gameMod")) {
                int gameModId = requestJsonObject.get("gameMod").getAsInt();
                if (gameModId != 0) { // Only filter if not 'All'
                    Criteria criteriaMode = s.createCriteria(Mode.class);
                    criteriaMode.add(Restrictions.eq("id", gameModId));
                    Mode modeList = (Mode) criteriaMode.uniqueResult();
                    criteriaProduct.add(Restrictions.eq("mode_id", modeList));
                }
            }

            // Title filter
            if (requestJsonObject.has("gametext")) {
                String Gametext = requestJsonObject.get("gametext").getAsString();
                criteriaProduct.add(Restrictions.like("title", Gametext + "%")); // title starts with Gametext
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
