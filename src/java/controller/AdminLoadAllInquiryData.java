package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Inquiry;
import hibernate.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.*;

@WebServlet(name = "AdminLoadAllInquiryData", urlPatterns = {"/AdminLoadAllInquiryData"})
public class AdminLoadAllInquiryData extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            Criteria criteria = s.createCriteria(Inquiry.class);
            List<Inquiry> inquiryList = criteria.list();

            JsonArray inquiryArray = new JsonArray();

            for (Inquiry inquiry : inquiryList) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", inquiry.getId());
                obj.addProperty("name", inquiry.getName());
                obj.addProperty("email", inquiry.getEmail());
                obj.addProperty("message", inquiry.getMessage());

                // Check if user exists for this email
                Criteria userCriteria = s.createCriteria(User.class);
                userCriteria.add(org.hibernate.criterion.Restrictions.eq("email", inquiry.getEmail()));
                boolean hasAccount = !userCriteria.list().isEmpty();
                obj.addProperty("hasAccount", hasAccount);

                inquiryArray.add(obj);
            }

            responseObject.add("inquiryList", inquiryArray);
            responseObject.addProperty("status", true);

        } catch (Exception e) {
            responseObject.addProperty("message", "Error loading inquiry data!");
        } finally {
            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}