package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Inquiry;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "MessageInquiry", urlPatterns = {"/MessageInquiry"})
public class MessageInquiry extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject inquiryJson = gson.fromJson(request.getReader(), JsonObject.class);

        String name = inquiryJson.get("name").getAsString();
        String email = inquiryJson.get("email").getAsString();
        String message = inquiryJson.get("message").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        // Empty validation
        if (name.isEmpty()) {
            responseObject.addProperty("message", "Name Required");
        } else if (email.isEmpty()) {
            responseObject.addProperty("message", "Email Required");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Invalid Email Address");
        } else if (message.isEmpty()) {
            responseObject.addProperty("message", "Message Required");
        } else {
            // Save inquiry
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Inquiry inquiry = new Inquiry();
            inquiry.setName(name);
            inquiry.setEmail(email);
            inquiry.setMessage(message);

            s.save(inquiry);
            s.beginTransaction().commit();
            s.close();

            responseObject.addProperty("status", Boolean.TRUE);
            responseObject.addProperty("message", "Inquiry submitted!");
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

}
