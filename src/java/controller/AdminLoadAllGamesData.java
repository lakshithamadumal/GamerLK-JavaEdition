/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.Developer;
import hibernate.HibernateUtil;
import hibernate.Product;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Criteria;

/**
 *
 * @author Laky
 */
@WebServlet(name = "AdminLoadAllGamesData", urlPatterns = {"/AdminLoadAllGamesData"})
public class AdminLoadAllGamesData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            Criteria criteriaProduct = s.createCriteria(Product.class);
            List<Product> productList = criteriaProduct.list();

            Criteria criteriaCategory = s.createCriteria(Category.class);
            List<Category> categoryList = criteriaCategory.list();

            Criteria criteriaDeveloper = s.createCriteria(Developer.class);
            List<Developer> developerList = criteriaDeveloper.list();

            responseObject.add("productList", gson.toJsonTree(productList));
            responseObject.add("categoryList", gson.toJsonTree(categoryList));
            responseObject.add("developerList", gson.toJsonTree(developerList));
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
