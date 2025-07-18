package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Laky
 */
@WebServlet(name = "ResetPWGetEmail", urlPatterns = {"/ResetPWGetEmail"})
public class ResetPWGetEmail extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject PWGetEmail = gson.fromJson(request.getReader(), JsonObject.class);
        String email = PWGetEmail.get("email").getAsString();

        //Validation
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email Required Here");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Invalid Email Address");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            //Search row at User in table 
            Criteria criteriaUser = s.createCriteria(User.class);
            Criterion criterionEmail = Restrictions.eq("email", email);

            criteriaUser.add(criterionEmail);

            if (criteriaUser.list().isEmpty()) {
                responseObject.addProperty("message", "Incorrect User Credentials");
            } else {

                User u = (User) criteriaUser.list().get(0);
                responseObject.addProperty("status", Boolean.TRUE);
                HttpSession ses = request.getSession();

                if (u.getStatus().getValue().equals("Processing")) {//Not Verify and Suspended
                    //Session Managemnrt
                    ses.setAttribute("email", email);
                    //Session Managemnrt
                    responseObject.addProperty("message", "Not Verified User");

                } else {

                    ses.setAttribute("email", email);
                    responseObject.addProperty("message", "User Found");

                    //Get Verify Code
                    String verificationCode = Util.generateCode();
                    u.setVerification(verificationCode);

                    s.update(u);
                    s.beginTransaction().commit();

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
                                    + "  <h1 style='color: white; font-size: 24px; margin: 0; font-weight: 700;'>Reset Your Password</h1>"
                                    + "</div>"
                                    + "<div style='padding: 30px; text-align: center;'>"
                                    + "  <p style='font-size: 16px; line-height: 1.6; margin-bottom: 10px;'>"
                                    + "Hello " + email + ",<br />We received a request to reset your password. Use the code below to proceed:"
                                    + "  </p>"
                                    + "  <div style='margin: 25px 0; font-size: 32px; font-weight: 700; letter-spacing: 5px; color: #df040a; "
                                    + "background: rgba(223, 4, 10, 0.1); padding: 15px; border-radius: 8px; display: inline-block;'>"
                                    + verificationCode
                                    + "  </div>"
                                    + "  <p style='font-size: 16px; line-height: 1.6; margin-bottom: 20px;'>"
                                    + "    Enter this code on the password reset page to create a new password."
                                    + "  </p>"
                                    + "  <p style='font-size: 14px; color: #666; margin-top: 30px;'>"
                                    + "    If you didn't request a password reset, please ignore this email or contact our support team."
                                    + "  </p>"
                                    + "</div>"
                                    + "<div style='background: #141415; padding: 40px 20px; display: flex; flex-direction: column; align-items: center; justify-content: center; "
                                    + "color: rgba(255, 255, 255, 0.7); font-size: 12px; text-align: center;'>"
                                    + "  <p style='margin: 0;'>© 2025 <strong>Gamerlk</strong>. All rights reserved.</p>"
                                    + "</div>"
                                    + "</div>";

                            Mail.sendMail(email, "Reset Your Gamerlk Password", emailBody);
                        }
                    }).start();

                }
            }
            s.close();

        }
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

}
