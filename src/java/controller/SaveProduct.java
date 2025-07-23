package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.Developer;
import hibernate.HibernateUtil;
import hibernate.Mode;
import hibernate.Product;
import hibernate.Requirement;
import hibernate.Status;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author Laky
 */
@MultipartConfig
@WebServlet(name = "SaveProduct", urlPatterns = {"/SaveProduct"})
public class SaveProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
        //Image Uploading
        Part thumbnailImage = request.getPart("thumbnailImage");

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        //Validation
        if (request.getSession().getAttribute("admin") == null) {
            responseObject.addProperty("message", "Admin Not found");
        } else if (gameName.isEmpty()) {
            responseObject.addProperty("message", "Game Name Required");
        } else if (gameDescription.isEmpty()) {
            responseObject.addProperty("message", "Game Description Required");
        } else if (gamePrice.isEmpty()) {
            responseObject.addProperty("message", "Game Price Required");
        } else if (!Util.isPrice(gamePrice)) {
            responseObject.addProperty("message", "Invalid Game Price");
        } else if (gameLink.isEmpty()) {
            responseObject.addProperty("message", "Game Download Link Required");
        } else if (!Util.isLinkValid(gameLink)) {
            responseObject.addProperty("message", "Invalid Game Download Link");
        } else if (gameSize.isEmpty()) {
            responseObject.addProperty("message", "Game Size Required");
        } else if (!Util.isPrice(gameSize)) {
            responseObject.addProperty("message", "Invalid Game Size");
        } else if (gameDate.isEmpty()) {
            responseObject.addProperty("message", "Release date Required");
        } else {

            // Category Validation
            if (!Util.isInteger(gameCategory) || Integer.parseInt(gameCategory) <= 0) {
                responseObject.addProperty("message", "Please select a valid Game Category");
            } else {
                Category category = (Category) s.get(Category.class, Integer.parseInt(gameCategory));
                if (category == null) {
                    responseObject.addProperty("message", "Invalid Game Category");
                } // Mode Validation
                else if (!Util.isInteger(gameMod) || Integer.parseInt(gameMod) <= 0) {
                    responseObject.addProperty("message", "Please select a valid Game Mode");
                } else {
                    Mode mode = (Mode) s.get(Mode.class, Integer.parseInt(gameMod));
                    if (mode == null) {
                        responseObject.addProperty("message", "Invalid Game Mode");
                    } // Developer Validation
                    else if (!Util.isInteger(gameDeveloper) || Integer.parseInt(gameDeveloper) <= 0) {
                        responseObject.addProperty("message", "Please select a valid Developer");
                    } else {
                        Developer developer = (Developer) s.get(Developer.class, Integer.parseInt(gameDeveloper));
                        if (developer == null) {
                            responseObject.addProperty("message", "Invalid Developer");
                        } // Tag Empty Check Only
                        else if (gameTag == null || gameTag.trim().isEmpty()) {
                            responseObject.addProperty("message", "Game Tag Required");
                        } // Minimum Requirement
                        else if (!Util.isInteger(gameMin) || Integer.parseInt(gameMin) <= 0) {
                            responseObject.addProperty("message", "Please select a valid Minimum Requirement");
                        } else {
                            Requirement minRequirement = (Requirement) s.get(Requirement.class, Integer.parseInt(gameMin));
                            if (minRequirement == null) {
                                responseObject.addProperty("message", "Invalid Minimum Requirement");
                            } // Recommended Requirement
                            else if (!Util.isInteger(gameMax) || Integer.parseInt(gameMax) <= 0) {
                                responseObject.addProperty("message", "Please select a valid Recommended Requirement");
                            } else {
                                Requirement maxRequirement = (Requirement) s.get(Requirement.class, Integer.parseInt(gameMax));
                                if (maxRequirement == null) {
                                    responseObject.addProperty("message", "Invalid Recommended Requirement");
                                } // Same ID check
                                else if (Integer.parseInt(gameMin) == (Integer.parseInt(gameMax))) {
                                    responseObject.addProperty("message", "Minimum & Recommended Requirements cannot be the same");
                                } // Thumbnail Image Check
                                else if (thumbnailImage == null || thumbnailImage.getSize() == 0) {
                                    responseObject.addProperty("message", "Thumbnail Image is Required");
                                } else {
                                    String fileName = thumbnailImage.getSubmittedFileName();
                                    if (fileName == null || !fileName.matches("(?i)^.*\\.(jpg|jpeg|png)$")) {
                                        responseObject.addProperty("message", "Thumbnail Image must be a .jpg, .jpeg, or .png file");
                                    } else {

                                        Status status = (Status) s.load(Status.class, 1); //Active

                                        Product p = new Product();
                                        p.setCategory_id(category);
                                        p.setMode_id(mode);
                                        p.setDeveloper_id(developer);
                                        p.setMin_requirement_id(minRequirement);
                                        p.setRec_requirement_id(maxRequirement);

                                        p.setTitle(gameName);
                                        p.setDescription(gameDescription);
                                        p.setPrice(Double.parseDouble(gamePrice));
                                        p.setGame_link(gameLink);
                                        p.setGame_size(Double.parseDouble(gameSize));
                                        p.setRelease_date(Date.valueOf(gameDate));
                                        p.setTag(gameTag);
                                        p.setOffer(0);
                                        p.setStatus_id(status);

                                        p.setCreated_at(new java.util.Date());

                                        int Productid = (int) s.save(p);
                                        s.beginTransaction().commit();
                                        s.close();

                                        String appPath = getServletContext().getRealPath("");
                                        String newPath = appPath.replace("build\\web", "web\\assets\\Games");
                                        File productFolder = new File(newPath, String.valueOf(Productid));
                                        productFolder.mkdir();

                                        File fileImage = new File(productFolder, "thumb-image.jpg");
                                        Files.copy(thumbnailImage.getInputStream(), fileImage.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                        responseObject.addProperty("status", true);
                                        responseObject.addProperty("message", "Game Added Successfully");

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Gson gson = new Gson();
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

}
