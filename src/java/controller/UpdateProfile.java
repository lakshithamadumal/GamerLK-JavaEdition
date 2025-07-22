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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Laky
 */
@WebServlet(name = "UpdateProfile", urlPatterns = {"/UpdateProfile"})
public class UpdateProfile extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject user = gson.fromJson(request.getReader(), JsonObject.class);

        String email = user.get("email").getAsString();
        String newPassword = user.get("newPassword").getAsString();
        String conformPassword = user.get("conformPassword").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false); // default fail

        // Check if at least one password field is filled
        if (newPassword.isEmpty() && conformPassword.isEmpty()) {
            responseObject.addProperty("message", "Nothing to update.");
        } else if (newPassword.isEmpty() || conformPassword.isEmpty()) {
            responseObject.addProperty("message", "Both password fields are required.");
        } else if (!newPassword.equals(conformPassword)) {
            responseObject.addProperty("message", "Passwords do not match.");
        } else {
            // Proceed with password update
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria criteria = s.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", email));
            User u = (User) criteria.uniqueResult();

            if (u != null) {
                s.beginTransaction();
                u.setPassword(conformPassword); 
                s.update(u);
                s.getTransaction().commit();

                // invalidate current session
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "User Profile Updated!");

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
                                + "  <h1 style='color: white; font-size: 24px; margin: 0; font-weight: 700;'>Profile Updated Successfully</h1>"
                                + "</div>"
                                + "<div style='padding: 30px; text-align: center;'>"
                                + "  <p style='font-size: 16px; line-height: 1.6; margin-bottom: 10px;'>"
                                + "Hello " + email + ",<br />Your profile details have been <strong>successfully updated</strong>. If this was you, no action is required."
                                + "  </p>"
                                + "  <div style='margin: 25px 0; font-size: 18px; font-weight: 700; color: #df040a; "
                                + "background: rgba(223, 4, 10, 0.1); padding: 15px; border-radius: 8px; display: inline-block;'>"
                                + "Profile Update Confirmed"
                                + "  </div>"
                                + "  <p style='font-size: 14px; color: #666; margin-top: 30px;'>"
                                + "If this wasn't you, please review your account settings and contact our support team immediately."
                                + "  </p>"
                                + "</div>"
                                + "<div style='background: #141415; padding: 40px 20px; display: flex; flex-direction: column; align-items: center; justify-content: center; "
                                + "color: rgba(255, 255, 255, 0.7); font-size: 12px; text-align: center;'>"
                                + "  <p style='margin: 0;'>© 2025 <strong>Gamerlk</strong>. All rights reserved.</p>"
                                + "</div>"
                                + "</div>";

                        Mail.sendMail(email, "Your Gamerlk Profile Has Been Updated", emailBody);
                    }
                }).start();
                s.close();
            }
        }
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(responseObject.toString());
        out.flush();
    }

}
