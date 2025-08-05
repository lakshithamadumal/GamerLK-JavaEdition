package controller;

import com.google.gson.Gson;
import hibernate.Community;
import hibernate.Admin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * Admin-only Community Chat POST endpoint
 */
@WebServlet(name = "AdminCommunityChat", urlPatterns = {"/AdminCommunityChat"})
public class AdminCommunityChat extends HttpServlet {

    private static final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession httpSession = request.getSession(false);
        Admin admin = (httpSession != null) ? (Admin) httpSession.getAttribute("admin") : null;

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (admin == null) {
            out.print("{\"status\":false,\"msg\":\"Not logged in as admin\"}");
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
            c.setUser_id(null); // Mark as admin message
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