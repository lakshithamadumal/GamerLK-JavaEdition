package controller;

import com.google.gson.JsonObject;
import hibernate.*;
import java.io.IOException;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.*;

@WebServlet(name = "UpdateProduct", urlPatterns = {"/UpdateProduct"})
public class UpdateProduct extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String id = request.getParameter("id");
        String gameName = request.getParameter("gameName");
        String gameDescription = request.getParameter("gameDescription");
        String gamePrice = request.getParameter("gamePrice");
        String gameLink = request.getParameter("gameLink");
        String gameSize = request.getParameter("gameSize");
        String gameDate = request.getParameter("gameDate");
        String gameCategory = request.getParameter("gameCategory");
        String gameMod = request.getParameter("gameMod");
        String gameDeveloper = request.getParameter("gameDeveloper");
        String gameTag = request.getParameter("gameTag");
        String gameMin = request.getParameter("gameMin");
        String gameMax = request.getParameter("gameMax");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (id == null || !id.matches("\\d+")) {
            responseObject.addProperty("message", "Invalid Product ID");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tx = null;
            try {
                tx = s.beginTransaction();
                Product p = (Product) s.get(Product.class, Integer.parseInt(id));
                if (p == null) {
                    responseObject.addProperty("message", "Product Not Found");
                } else {
                    // Validation (same as SaveProduct)
                    if (gameName == null || gameName.isEmpty()) {
                        responseObject.addProperty("message", "Game Name Required");
                    } else if (gameDescription == null || gameDescription.isEmpty()) {
                        responseObject.addProperty("message", "Game Description Required");
                    } else if (gamePrice == null || gamePrice.isEmpty()) {
                        responseObject.addProperty("message", "Game Price Required");
                    } else if (gameLink == null || gameLink.isEmpty()) {
                        responseObject.addProperty("message", "Game Download Link Required");
                    } else if (gameSize == null || gameSize.isEmpty()) {
                        responseObject.addProperty("message", "Game Size Required");
                    } else if (gameDate == null || gameDate.isEmpty()) {
                        responseObject.addProperty("message", "Release date Required");
                    } else {
                        // Set new values
                        p.setTitle(gameName);
                        p.setDescription(gameDescription);
                        p.setPrice(Double.parseDouble(gamePrice));
                        p.setGame_link(gameLink);
                        p.setGame_size(Double.parseDouble(gameSize));
                        p.setRelease_date(Date.valueOf(gameDate));
                        p.setTag(gameTag);

                        // Foreign keys
                        if (gameCategory != null) {
                            Category category = (Category) s.get(Category.class, Integer.parseInt(gameCategory));
                            p.setCategory_id(category);
                        }
                        if (gameMod != null) {
                            Mode mode = (Mode) s.get(Mode.class, Integer.parseInt(gameMod));
                            p.setMode_id(mode);
                        }
                        if (gameDeveloper != null) {
                            Developer dev = (Developer) s.get(Developer.class, Integer.parseInt(gameDeveloper));
                            p.setDeveloper_id(dev);
                        }
                        if (gameMin != null) {
                            Requirement min = (Requirement) s.get(Requirement.class, Integer.parseInt(gameMin));
                            p.setMin_requirement_id(min);
                        }
                        if (gameMax != null) {
                            Requirement max = (Requirement) s.get(Requirement.class, Integer.parseInt(gameMax));
                            p.setRec_requirement_id(max);
                        }

                        s.update(p);
                        tx.commit();
                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "Game Updated Successfully");
                    }
                }
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                responseObject.addProperty("message", "Error updating game");
            } finally {
                s.close();
            }
        }
        response.setContentType("application/json");
        response.getWriter().write(responseObject.toString());
    }
}