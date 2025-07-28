package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Subscriber;
import hibernate.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Laky
 */
@WebServlet(name = "AdminLoadAllSubscriberData", urlPatterns = {"/AdminLoadAllSubscriberData"})
public class AdminLoadAllSubscriberData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            Criteria criteriaSubscriber = s.createCriteria(Subscriber.class);
            List<Subscriber> subscriberList = criteriaSubscriber.list();

            JsonArray subscriberArray = new JsonArray();

            for (Subscriber sub : subscriberList) {
                JsonObject subObj = new JsonObject();
                String email = sub.getEmail(); // FIXED

                subObj.addProperty("id", sub.getId());
                subObj.addProperty("email", email);

                // Check if user exists with this email
                Criteria criteriaUser = s.createCriteria(User.class);
                criteriaUser.add(Restrictions.eq("email", email));
                boolean hasAccount = criteriaUser.uniqueResult() != null;

                subObj.addProperty("hasAccount", hasAccount ? "Yes" : "No");

                subscriberArray.add(subObj);
            }

            responseObject.add("subscriberList", subscriberArray);
            responseObject.addProperty("status", true);

        } catch (Exception e) {
            responseObject.addProperty("message", "Error loading data!");
        } finally {
            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
