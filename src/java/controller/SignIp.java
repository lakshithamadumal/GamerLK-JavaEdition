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
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SignIp", urlPatterns = {"/SignIp"})
public class SignIp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject SignIn = gson.fromJson(request.getReader(), JsonObject.class);

        String email = SignIn.get("email").getAsString();
        String password = SignIn.get("password").getAsString();

        //Validation
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email Required");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Invalid Email");
        } else if (password.isEmpty()) {
            responseObject.addProperty("message", "Password Required");
        } else {
            //Search User
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            //Search row at User in table 
            Criteria criteriaUser = s.createCriteria(User.class);
            Criterion criterionEmail = Restrictions.eq("email", email);
            Criterion criterionPassword = Restrictions.eq("password", password);

            criteriaUser.add(criterionEmail);
            criteriaUser.add(criterionPassword);

            if (criteriaUser.list().isEmpty()) {
                responseObject.addProperty("message", "Invalid User");
            } else {
                User u = (User) criteriaUser.list().get(0);
                responseObject.addProperty("status", Boolean.TRUE);
                HttpSession ses = request.getSession();

                if (true) {//Not Verify and Suspended

                    //Codessssssss
                    
                    //Session Managemnrt
                    ses.setAttribute("email", email);
                    //Session Managemnrt
                    responseObject.addProperty("message", "Not Verified User");

                } else {
                    ses.setAttribute("user", u);
                    responseObject.addProperty("message", "Successful Login");
                }
            }
            s.close();
        }
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

}
