package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Subscriber;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;

/**
 *
 * @author Laky
 */
@WebServlet(name = "SingleSendEmail", urlPatterns = {"/SingleSendEmail"})
public class SingleSendEmail extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject sendEmail = gson.fromJson(request.getReader(), JsonObject.class);

        String email = sendEmail.get("Singleemail").getAsString();
        String emailSubject = sendEmail.get("SingleemailSubject").getAsString();
        String emailBodyContent = sendEmail.get("SingleemailBody").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email cannot be empty.");
            response.getWriter().print(responseObject.toString());
            return;
        }

        if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Invalid Email Address");
            response.getWriter().print(responseObject.toString());
            return;
        }

        if (emailSubject.isEmpty()) {
            responseObject.addProperty("message", "Subject cannot be empty.");
            response.getWriter().print(responseObject.toString());
            return;
        }

        if (emailBodyContent.isEmpty()) {
            responseObject.addProperty("message", "Body cannot be empty.");
            response.getWriter().print(responseObject.toString());
            return;
        }

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        //Send Email
        new Thread(new Runnable() {
            @Override
            public void run() {
                String emailBody = ""
                        + "<div style='max-width: 500px; margin: 20px auto; background: white; border-radius: 12px; "
                        + "overflow: hidden; box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05); font-family: Outfit, sans-serif;'>"
                        + "<div style='background: linear-gradient(135deg, #df040a, #ff6b27); padding: 30px; text-align: center;'>"
                        + "  <div style='padding: 0 36px 26px 36px;'>"
                        + "    <span style='font-family: Cyberjunkies, Outfit, Arial, sans-serif; letter-spacing: 3px; font-size: 2.15rem; color: #fff;'>"
                        + "      <strong>GAMERLK</strong>"
                        + "    </span>"
                        + "  </div>"
                        + "  <h1 style='color: white; font-size: 24px; margin: 0; font-weight: 700;'>" + emailSubject + "</h1>"
                        + "</div>"
                        + "<div style='padding: 30px; text-align: center;'>"
                        + "  <p style='font-size: 16px; line-height: 1.6; margin-bottom: 10px;'>" + emailBodyContent + "</p>"
                        + "  <a href='http://localhost:8080/Gamerlk/en/pages/login.html' style='display: inline-block; margin-top: 25px; background-color: #df040a; "
                        + "color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;'>"
                        + "Log In & Start Gaming"
                        + "  </a>"
                        + "</div>"
                        + "<div style='background: #141415; padding: 40px 20px; display: flex; flex-direction: column; align-items: center; justify-content: center; "
                        + "color: rgba(255, 255, 255, 0.7); font-size: 12px; text-align: center;'>"
                        + "  <p style='margin: 0;'>© 2025 <strong>GAMERLK</strong>. All rights reserved.</p>"
                        + "</div>"
                        + "</div>";

                Mail.sendMail(email, emailSubject, emailBody);
            }
        }).start();

        s.close();

        responseObject.addProperty("status", Boolean.TRUE);
        responseObject.addProperty("message", "Emails are being sent.");
        response.getWriter().print(responseObject.toString());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"GET not supported. Use POST.\"}");
    }
}
