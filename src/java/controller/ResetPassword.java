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
            } else if (!Util.isPasswordValid(password)) {
                responseObject.addProperty("message", "Password must be between 1 to 8 characters");
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
