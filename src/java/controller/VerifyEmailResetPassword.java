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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Laky
 */
@WebServlet(name = "VerifyEmailResetPassword", urlPatterns = {"/VerifyEmailResetPassword"})
public class VerifyEmailResetPassword extends HttpServlet {

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

                //Search
                Criterion criterionEmail = Restrictions.eq("email", email);
                Criterion criterionVerificationCode = Restrictions.eq("verification", verificationCode);

                criteriaUser.add(criterionEmail);
                criteriaUser.add(criterionVerificationCode);

                if (criteriaUser.list().isEmpty()) {//Email එකට ගැලපෙන්නෙ නැ Verify Code එක
                    responseObject.addProperty("message", "Invalid Verification Code");
                } else {
                    responseObject.addProperty("status", Boolean.TRUE);

                    ses.setAttribute("email", email);
                    responseObject.addProperty("message", "Verification Success");
                }

                s.close();
            }
        }
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }
}
