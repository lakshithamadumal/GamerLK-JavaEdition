package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Status;
import hibernate.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Mail;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Laky
 */
@WebServlet(name = "VerifyAccount", urlPatterns = {"/VerifyAccount"})
public class VerifyAccount extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        HttpSession ses = request.getSession();//Get Session on SignUp

        if (ses.getAttribute("email") == null) {//Check Email Have A session
            responseObject.addProperty("message", "Email not Found");
        } else {
            String email = ses.getAttribute("email").toString();//Get Session have email

            //Get Verification Code in Fontend
            JsonObject verification = gson.fromJson(request.getReader(), JsonObject.class);
            String verificationCode = verification.get("verificationCode").getAsString();
            if (verificationCode.isEmpty()) {
                responseObject.addProperty("message", "Verification Code Required");
            } else {

                //Create Session Factory
                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();

                Criteria criteriaUser = s.createCriteria(User.class);

                //Like a AND Search
                Criterion criterionEmail = Restrictions.eq("email", email);
                Criterion criterionVerificationCode = Restrictions.eq("verification", verificationCode);

                criteriaUser.add(criterionEmail);
                criteriaUser.add(criterionVerificationCode);

                if (criteriaUser.list().isEmpty()) {//Email එකට ගැලපෙන්නෙ නැ Verify Code එක
                    responseObject.addProperty("message", "Invalid Verification Code");
                } else {
                    //Status is Processing to Active
                    Criteria criteriaStatus = s.createCriteria(Status.class);
                    Criterion criterionStatus = Restrictions.eq("value", "Active");
                    criteriaStatus.add(criterionStatus);

                    Status processingStatus = (Status) criteriaStatus.uniqueResult(); // get the matching "Active" status

                    User user = (User) criteriaUser.list().get(0);
                    user.setStatus(processingStatus); //Change to Status to Active

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
                                    + "  <h1 style='color: white; font-size: 24px; margin: 0; font-weight: 700;'>Welcome to the Squad!</h1>"
                                    + "</div>"
                                    + "<div style='padding: 30px; text-align: center;'>"
                                    + "  <p style='font-size: 16px; line-height: 1.6; margin-bottom: 10px;'>"
                                    + "Hey " + email + ",<br /><br />Welcome to <strong>GAMERLK</strong> the ultimate hub for gamers like you."
                                    + "We're hyped to have you onboard!"
                                    + "  </p>"
                                    + "  <div style='margin: 20px 0; font-size: 18px; font-weight: 700; color: #ff6b27; "
                                    + "background: rgba(255, 107, 39, 0.1); padding: 12px 20px; border-radius: 8px; display: inline-block;'>"
                                    + "Get ready to unlock the full experience!"
                                    + "  </div>"
                                    + "  <p style='font-size: 14px; line-height: 1.6; margin-top: 20px;'>"
                                    + "Start exploring events, tournaments, merch, and exclusive content made just for the gaming legends. "
                                    + "If you have any questions or need help, our team is just one click away."
                                    + "  </p>"
                                    + "  <a href='http://localhost:8080/Gamerlk/en/pages/login.html' style='display: inline-block; margin-top: 25px; background-color: #df040a; "
                                    + "color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;'>"
                                    + "Log In & Start Gaming"
                                    + "  </a>"
                                    + "</div>"
                                    + "<div style='background: #141415; padding: 40px 20px; display: flex; flex-direction: column; align-items: center; justify-content: center; "
                                    + "color: rgba(255, 255, 255, 0.7); font-size: 12px; text-align: center;'>"
                                    + "  <p style='margin: 0;'>© 2025 <strong>Gamerlk</strong>. All rights reserved.</p>"
                                    + "</div>"
                                    + "</div>";

                            Mail.sendMail(email, "Welcome to GAMERLK – You're Officially In", emailBody);

                        }
                    }).start();

                    s.close();

                    //Store user in the session
                    ses.setAttribute("user", user);

                    responseObject.addProperty("status", Boolean.TRUE);
                    responseObject.addProperty("message", "Verification Successful");

                }
            }
        }
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }
}
