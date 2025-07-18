package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "ResetPassword", urlPatterns = {"/ResetPassword"})
public class ResetPassword extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject Reset = gson.fromJson(request.getReader(), JsonObject.class);
        String password = Reset.get("confirmPass").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        HttpSession ses = request.getSession(false); // false to avoid creating session if doesn't exist

        if (ses == null || ses.getAttribute("email") == null) {
            responseObject.addProperty("message", "Email Not Found");
        } else {
            String email = ses.getAttribute("email").toString();

            if (password.isEmpty()) {
                responseObject.addProperty("message", "Password Required Here");
            } else {
                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();

                Criteria criteriaReset = s.createCriteria(User.class);
                Criterion criterionReset = Restrictions.eq("email", email);
                criteriaReset.add(criterionReset);

                if (criteriaReset.list().isEmpty()) {
                    responseObject.addProperty("message", "User Not Found");
                } else {
                    User u = (User) criteriaReset.list().get(0);
                    u.setPassword(password);
                    s.beginTransaction();
                    s.update(u);
                    s.getTransaction().commit();

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
                                    + "  <h1 style='color: white; font-size: 24px; margin: 0; font-weight: 700;'>Password Reset Successful</h1>"
                                    + "</div>"
                                    + "<div style='padding: 30px; text-align: center;'>"
                                    + "  <p style='font-size: 16px; line-height: 1.6; margin-bottom: 10px;'>"
                                    + "Hello " + email + ",<br />Your password has been <strong>successfully reset</strong>. You can now log in using your new password."
                                    + "  </p>"
                                    + "  <div style='margin: 25px 0; font-size: 18px; font-weight: 700; color: #df040a; "
                                    + "background: rgba(223, 4, 10, 0.1); padding: 15px; border-radius: 8px; display: inline-block;'>"
                                    + "Password Reset Complete"
                                    + "  </div>"
                                    + "  <p style='font-size: 14px; color: #666; margin-top: 30px;'>"
                                    + "If this wasn't you, please reset your password again immediately and contact our support team."
                                    + "  </p>"
                                    + "</div>"
                                    + "<div style='background: #141415; padding: 40px 20px; display: flex; flex-direction: column; align-items: center; justify-content: center; "
                                    + "color: rgba(255, 255, 255, 0.7); font-size: 12px; text-align: center;'>"
                                    + "  <p style='margin: 0;'>© 2025 <strong>Gamerlk</strong>. All rights reserved.</p>"
                                    + "</div>"
                                    + "</div>";

                            Mail.sendMail(email, "Your Gamerlk Password Has Been Reset", emailBody);

                        }
                    }).start();

                    s.close();

                    responseObject.addProperty("status", Boolean.TRUE);
                    responseObject.addProperty("message", "Password Reset Successful");
                }
            }
        }

        // ✅ SEND JSON RESPONSE
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(responseObject.toString());
        out.flush();
    }

}
