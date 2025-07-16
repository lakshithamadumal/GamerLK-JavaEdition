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
