package controller;

import com.google.gson.Gson;
import hibernate.Community;
import hibernate.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.cfg.Configuration;

/**
*
* @author Laky
*/
@WebServlet(name = "CommunityChat", urlPatterns = {"/CommunityChat"})
public class CommunityChat extends HttpServlet {

   private static final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
       Session session = sessionFactory.openSession();
       response.setContentType("application/json");
       PrintWriter out = response.getWriter();

       try {
           Criteria criteria = session.createCriteria(Community.class);
           criteria.addOrder(Order.asc("sent_at"));
           List<Community> messages = criteria.list();

           Gson gson = new Gson();
           out.print(gson.toJson(messages));
       } finally {
           session.close();
           out.close();
       }
   }

   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
       HttpSession httpSession = request.getSession(false);
       User user = (httpSession != null) ? (User) httpSession.getAttribute("user") : null;

       // --- Admin session support: if admin logged, use Gamerlk@admin user ---
       if (user == null && httpSession != null && httpSession.getAttribute("admin") != null) {
           hibernate.Admin admin = (hibernate.Admin) httpSession.getAttribute("admin");
           // Find User entity with Gamerlk@admin email
           Session session = sessionFactory.openSession();
           try {
               org.hibernate.Criteria userCriteria = session.createCriteria(hibernate.User.class)
                   .add(org.hibernate.criterion.Restrictions.eq("email", "Gamerlk@admin"));
               user = (User) userCriteria.uniqueResult();
           } finally {
               session.close();
           }
       }

       response.setContentType("application/json");
       PrintWriter out = response.getWriter();

       if (user == null) {
           out.print("{\"status\":false,\"msg\":\"Not logged in\"}");
           out.close();
           return;
       }

       StringBuilder sb = new StringBuilder();
       BufferedReader reader = request.getReader();
       String line;
       while ((line = reader.readLine()) != null) sb.append(line);

       Gson gson = new Gson();
       MessagePayload payload = gson.fromJson(sb.toString(), MessagePayload.class);

       if (payload == null || payload.message == null || payload.message.trim().isEmpty()) {
           out.print("{\"status\":false,\"msg\":\"Empty message\"}");
           out.close();
           return;
       }

       Session session = sessionFactory.openSession();
       Transaction tx = null;
       try {
           tx = session.beginTransaction();
           Community c = new Community();
           c.setMessage(payload.message);
           c.setSent_at(new Date());
           c.setUser_id(user);
           session.save(c);
           tx.commit();
           out.print("{\"status\":true}");
       } catch (Exception e) {
           if (tx != null) tx.rollback();
           out.print("{\"status\":false,\"msg\":\"DB error\"}");
       } finally {
           session.close();
           out.close();
       }
   }

   private static class MessagePayload {
       String message;
   }
}
