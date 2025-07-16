package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Status;
import hibernate.User;
import java.io.IOException;
import java.util.Date;
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
@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject user = gson.fromJson(request.getReader(), JsonObject.class);

        String firstName = user.get("firstName").getAsString();
        String lastName = user.get("lastName").getAsString();
        String email = user.get("email").getAsString();
        String password = user.get("password").getAsString();

        //Validation
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        if (firstName.isEmpty()) {
            responseObject.addProperty("message", "First Name Required");
        } else if (lastName.isEmpty()) {
            responseObject.addProperty("message", "Last Name Required");
        } else if (email.isEmpty()) {
            responseObject.addProperty("message", "Email Required");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Invalid Email");
        } else if (password.isEmpty()) {
            responseObject.addProperty("message", "Password Required");
        } else if (!Util.isPasswordValid(password)) {
            responseObject.addProperty("message", "Password Not Strong Enough");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            //Have A User
            Criteria criteriaEmail = s.createCriteria(User.class);
            criteriaEmail.add(Restrictions.eq("email", email));

            if (!criteriaEmail.list().isEmpty()) {
                responseObject.addProperty("message", "Email Already Registered");
            } else {

                //Status is Active
                Criteria criteriaStatus = s.createCriteria(Status.class);
                Criterion criterionStatus = Restrictions.eq("value", "Processing");
                criteriaStatus.add(criterionStatus);

                Status processingStatus = (Status) criteriaStatus.uniqueResult(); // get the matching "Processing" status

                User u = new User();
                u.setFirst_name(firstName);
                u.setLast_name(lastName);
                u.setEmail(email);
                u.setPassword(password);
                u.setCreated_at(new Date());
                u.setStatus(processingStatus);

                //Get Verify Code
                String verificationCode = Util.generateCode();
                u.setVerification(verificationCode);

                s.save(u);
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
                                + "  <h1 style='color: white; font-size: 24px; margin: 0; font-weight: 700;'>Your Verification Code</h1>"
                                + "</div>"
                                + "<div style='padding: 30px; text-align: center;'>"
                                + "  <p style='font-size: 16px; line-height: 1.6; margin-bottom: 10px;'>"
                                + "Hello " + firstName + ",<br />Here's your 6-digit verification code:"
                                + "  </p>"
                                + "  <div style='margin: 25px 0; font-size: 32px; font-weight: 700; letter-spacing: 5px; color: #df040a; "
                                + "background: rgba(223, 4, 10, 0.1); padding: 15px; border-radius: 8px; display: inline-block;'>"
                                + verificationCode
                                + "  </div>"
                                + "  <p style='font-size: 16px; line-height: 1.6; margin-bottom: 20px;'>"
                                + "    Enter this code in the verification page to complete your account setup."
                                + "  </p>"
                                + "  <p style='font-size: 14px; color: #666; margin-top: 30px;'>"
                                + "    If you didn't request this, please ignore this email."
                                + "  </p>"
                                + "</div>"
                                + "<div style='background: #141415; padding: 40px 20px; display: flex; flex-direction: column; align-items: center; justify-content: center; "
                                + "color: rgba(255, 255, 255, 0.7); font-size: 12px; text-align: center;'>"
                                + "  <p style='margin: 0;'>© 2025 <strong>Gamerlk</strong>. All rights reserved.</p>"
                                + "</div>"
                                + "</div>";

                        Mail.sendMail(email, "Gamerlk Verification Code", emailBody);
                    }
                }).start();

                //Session Managemnrt to Get Email to Verify
                HttpSession ses = request.getSession();
                ses.setAttribute("email", email);
                //Session Managemnrt

                //Change Status and Success
                responseObject.addProperty("status", Boolean.TRUE);
                responseObject.addProperty("message", "Registration successful!");
            }
            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);

    }
}
