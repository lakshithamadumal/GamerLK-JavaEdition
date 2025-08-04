/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Status;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Laky
 */
@WebServlet(name = "AdminUnbanCustomer", urlPatterns = {"/AdminUnbanCustomer"})
public class AdminUnbanCustomer extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        JsonObject obj = new JsonObject();
        if (request.getSession().getAttribute("admin") == null) {
            obj.addProperty("status", false);
            obj.addProperty("message", "Admin not found");
        } else if (id == null || !id.matches("\\d+")) {
            obj.addProperty("status", false);
            obj.addProperty("message", "Invalid user id");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tx = null;
            try {
                tx = s.beginTransaction();
                User u = (User) s.get(User.class, Integer.parseInt(id));
                if (u != null) {
                    Status status = u.getStatus();
                    if (status != null && "Inactive".equalsIgnoreCase(status.getValue())) {
                        Status active = (Status) s.createCriteria(Status.class)
                                .add(Restrictions.eq("value", "Active")).uniqueResult();
                        u.setStatus(active);
                        s.update(u);
                        tx.commit();
                        obj.addProperty("status", true);
                    } else {
                        obj.addProperty("status", false);
                        obj.addProperty("message", "Already Active");
                    }
                } else {
                    obj.addProperty("status", false);
                    obj.addProperty("message", "User not found");
                }
            } catch (Exception e) {
                if (tx != null) {
                    tx.rollback();
                }
                obj.addProperty("status", false);
                obj.addProperty("message", "Error updating user");
            } finally {
                s.close();
            }
        }
        response.setContentType("application/json");
        response.getWriter().write(obj.toString());
    }
}
