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
@WebServlet(name = "ResendCodeForPW", urlPatterns = {"/ResendCodeForPW"})
public class ResendCodeForPW extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        HttpSession session = request.getSession();

        if (session != null && session.getAttribute("email") != null) {
            String email = session.getAttribute("email").toString();
            responseObject.addProperty("message", "Email found");
            SessionFactory sf = hibernate.HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            //search user email
            Criteria c1 = s.createCriteria(User.class);
            Criterion crt1 = Restrictions.eq("email", email);

            c1.add(crt1);

            if (c1.list().isEmpty()) {
                responseObject.addProperty("message", "User not found");
            } else {

                User user = (User) c1.list().get(0);

                responseObject.addProperty("status", true);

                session.setAttribute("email", email);

                //Get Verify Code
                String verificationCode = Util.generateCode();
                user.setVerification(verificationCode);

                s.update(user);
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

                responseObject.addProperty("message", "User found");
                session.setAttribute("lastCodeSentTime", System.currentTimeMillis());

            }
            s.close();

        } else {
            responseObject.addProperty("message", "Email not found");
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);

    }
}
