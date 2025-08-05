package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.Util;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
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
        Part thumbnailImage = request.getPart("thumbnailImage");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        Transaction tx = null;

        //Have a Product
        Criteria criteriaProduct = s.createCriteria(Product.class);
        criteriaProduct.add(Restrictions.eq("game_link", gameLink));
        criteriaProduct.add(Restrictions.ne("id", Integer.parseInt(id))); // <-- update කරන product එක අයින් කරන්න

        if (!criteriaProduct.list().isEmpty()) {
            responseObject.addProperty("message", "Game Already Added");
        } else {

            if (request.getSession().getAttribute("admin") == null) {
                responseObject.addProperty("message", "Admin Not found");
            } else if (id == null || !id.matches("\\d+")) {
                responseObject.addProperty("message", "Invalid Product ID");
            } else {

                try {
                    tx = s.beginTransaction();
                    Product p = (Product) s.get(Product.class, Integer.parseInt(id));
                    if (p == null) {
                        responseObject.addProperty("message", "Product Not Found");
                    } else {
                        // Validation
                        if (gameName == null || gameName.isEmpty()) {
                            responseObject.addProperty("message", "Game Name Required");
                        } else if (gameDescription == null || gameDescription.isEmpty()) {
                            responseObject.addProperty("message", "Game Description Required");
                        } else if (gamePrice == null || gamePrice.isEmpty()) {
                            responseObject.addProperty("message", "Game Price Required");
                        } else if (!Util.isPrice(gamePrice)) {
                            responseObject.addProperty("message", "Invalid Game Price");
                        } else if (gameLink == null || gameLink.isEmpty()) {
                            responseObject.addProperty("message", "Game Download Link Required");
                        } else if (!Util.isLinkValid(gameLink)) {
                            responseObject.addProperty("message", "Invalid Game Download Link");
                        } else if (gameSize == null || gameSize.isEmpty()) {
                            responseObject.addProperty("message", "Game Size Required");
                        } else if (!Util.isPrice(gameSize)) {
                            responseObject.addProperty("message", "Invalid Game Size");
                        } else if (gameDate == null || gameDate.isEmpty()) {
                            responseObject.addProperty("message", "Release date Required");
                        } else if (!Util.isInteger(gameCategory) || Integer.parseInt(gameCategory) <= 0) {
                            responseObject.addProperty("message", "Please select a valid Game Category");
                        } else if (!Util.isInteger(gameMod) || Integer.parseInt(gameMod) <= 0) {
                            responseObject.addProperty("message", "Please select a valid Game Mode");
                        } else if (!Util.isInteger(gameDeveloper) || Integer.parseInt(gameDeveloper) <= 0) {
                            responseObject.addProperty("message", "Please select a valid Developer");
                        } else if (gameTag == null || gameTag.trim().isEmpty()) {
                            responseObject.addProperty("message", "Game Tag Required");
                        } else if (!Util.isInteger(gameMin) || Integer.parseInt(gameMin) <= 0) {
                            responseObject.addProperty("message", "Please select a valid Minimum Requirement");
                        } else if (!Util.isInteger(gameMax) || Integer.parseInt(gameMax) <= 0) {
                            responseObject.addProperty("message", "Please select a valid Recommended Requirement");
                        } else if (Integer.parseInt(gameMin) == Integer.parseInt(gameMax)) {
                            responseObject.addProperty("message", "Minimum & Recommended Requirements cannot be the same");
                        } else {
                            // Foreign keys
                            Category category = (Category) s.get(Category.class, Integer.parseInt(gameCategory));
                            Mode mode = (Mode) s.get(Mode.class, Integer.parseInt(gameMod));
                            Developer developer = (Developer) s.get(Developer.class, Integer.parseInt(gameDeveloper));
                            Requirement minRequirement = (Requirement) s.get(Requirement.class, Integer.parseInt(gameMin));
                            Requirement maxRequirement = (Requirement) s.get(Requirement.class, Integer.parseInt(gameMax));
                            Status status = (Status) s.load(Status.class, 1); //Active

                            // Set new values
                            p.setTitle(gameName);
                            p.setDescription(gameDescription);
                            p.setPrice(Double.parseDouble(gamePrice));
                            p.setGame_link(gameLink);
                            p.setGame_size(Double.parseDouble(gameSize));
                            p.setRelease_date(Date.valueOf(gameDate));
                            p.setTag(gameTag);
                            p.setCategory_id(category);
                            p.setMode_id(mode);
                            p.setDeveloper_id(developer);
                            p.setMin_requirement_id(minRequirement);
                            p.setRec_requirement_id(maxRequirement);
                            p.setStatus_id(status);

                            // Thumbnail image update (optional)
                            if (thumbnailImage != null && thumbnailImage.getSubmittedFileName() != null && !thumbnailImage.getSubmittedFileName().isEmpty()) {
                                String fileName = thumbnailImage.getSubmittedFileName();
                                if (!fileName.matches("(?i)^.*\\.(jpg|jpeg|png)$")) {
                                    responseObject.addProperty("message", "Thumbnail Image must be a .jpg, .jpeg, or .png file");
                                    s.close();
                                    response.setContentType("application/json");
                                    response.getWriter().write(new Gson().toJson(responseObject));
                                    return;
                                }
                                String appPath = getServletContext().getRealPath("");
                                String newPath = appPath.replace("build\\web", "web\\assets\\Games");
                                File productFolder = new File(newPath, String.valueOf(p.getId()));
                                productFolder.mkdir();

                                File fileImage = new File(productFolder, "thumb-image.jpg");
                                Files.copy(thumbnailImage.getInputStream(), fileImage.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            }

                            s.update(p);
                            tx.commit();
                            responseObject.addProperty("status", true);
                            responseObject.addProperty("message", "Game Updated Successfully");
                        }
                    }
                } catch (Exception e) {
                    if (tx != null) {
                        tx.rollback();
                    }
                    responseObject.addProperty("message", "Error updating game");
                } finally {
                    s.close();
                }
            }
        }
        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(responseObject));
    }
}
