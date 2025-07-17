package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Admin;
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

/**
 *
 * @author Laky
 */
@WebServlet(name = "AdminSignIn", urlPatterns = {"/AdminSignIn"})
public class AdminSignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject AdminSignIn = gson.fromJson(request.getReader(), JsonObject.class);

        String email = AdminSignIn.get("email").getAsString();
        String password = AdminSignIn.get("password").getAsString();

        //Validation
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email Required Here");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Invalid Email Address");
        } else if (password.isEmpty()) {
            responseObject.addProperty("message", "Password Required Here");
        } else {

            //Search User
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            //Search row at Adimin in table 
            Criteria criteriaAdmin = s.createCriteria(Admin.class);
            Criterion criterionEmail = Restrictions.eq("email", email);
            Criterion criterionPassword = Restrictions.eq("password", password);

            criteriaAdmin.add(criterionEmail);
            criteriaAdmin.add(criterionPassword);

            if (criteriaAdmin.list().isEmpty()) {
                responseObject.addProperty("message", "Incorrect Admin Credentials");
            } else {
                Admin a = (Admin) criteriaAdmin.list().get(0);
                responseObject.addProperty("status", Boolean.TRUE);
                HttpSession ses = request.getSession();
            }
            s.close();
        }
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }
}
