package controller;

import hibernate.HibernateUtil;
import hibernate.Orders;
import hibernate.User;
import model.Mail;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "SendPurchaseEmailServlet", urlPatterns = {"/SendPurchaseEmail"})
public class SendPurchaseEmailServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String orderIdStr = request.getParameter("orderId");
        System.out.println("Received orderId: " + orderIdStr);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            Orders order = (Orders) s.get(Orders.class, Integer.valueOf(orderIdStr));
            System.out.println("Order object: " + order);

            if (order == null) {
                response.setStatus(404);
                response.getWriter().write("Order not found");
                return;
            }
            User user = order.getUser_id();
            System.out.println("User object: " + user);
            System.out.println("User email: " + user.getEmail());
            System.out.println("User name: " + user.getFirst_name() + " " + user.getLast_name());

            String emailBody = ""
                    + "<div style='max-width:600px;margin:20px auto;background:white;border-radius:12px;overflow:hidden;box-shadow:0 5px 15px rgba(0,0,0,0.05);font-family:Outfit,sans-serif;'>"
                    + "<div style='background:linear-gradient(135deg,#df040a,#ff6b27);padding:30px;text-align:center;color:white;'>"
                    + "<span style='font-family:Cyberjunkies,Outfit,Arial,sans-serif;letter-spacing:3px;font-size:2.15rem;color:#fff;'><strong>GAMERLK</strong></span>"
                    + "<h1 style='color:white;font-size:24px;margin:0;font-weight:700;'>Thank You For Your Purchase!</h1>"
                    + "</div>"
                    + "<div style='padding:30px;'>"
                    + "<p>Hi " + user.getFirst_name() + ",</p>"
                    + "<p>Your order has been confirmed and your games are ready to play.</p>"
                    // Stylish Grid with Details
                    + "<div style='display:grid;grid-template-columns:repeat(2,1fr);gap:20px;margin-bottom:30px;'>"
                    + "<div style='background:#f9f9f9;border-radius:8px;padding:15px;'>"
                    + "<h3 style='color:#ff6b27;font-size:14px;text-transform:uppercase;letter-spacing:1px;margin-top:0;'>Status</h3>"
                    + "<p style='font-size:16px;font-weight:600;margin-bottom:0;'>Completed</p></div>"
                    + "<div style='background:#f9f9f9;border-radius:8px;padding:15px;'>"
                    + "<h3 style='color:#ff6b27;font-size:14px;text-transform:uppercase;letter-spacing:1px;margin-top:0;'>Order ID</h3>"
                    + "<p style='font-size:16px;font-weight:600;margin-bottom:0;'>GKLK-2025-" + order.getId() + "</p></div>"
                    + "<div style='background:#f9f9f9;border-radius:8px;padding:15px;'>"
                    + "<h3 style='color:#ff6b27;font-size:14px;text-transform:uppercase;letter-spacing:1px;margin-top:0;'>Name</h3>"
                    + "<p style='font-size:16px;font-weight:600;margin-bottom:0;'>" + user.getFirst_name() + " " + user.getLast_name() + "</p></div>"
                    + "<div style='background:#f9f9f9;border-radius:8px;padding:15px;'>"
                    + "<h3 style='color:#ff6b27;font-size:14px;text-transform:uppercase;letter-spacing:1px;margin-top:0;'>Email</h3>"
                    + "<p style='font-size:16px;font-weight:600;margin-bottom:0;'>" + user.getEmail() + "</p></div>"
                    + "</div>"
                    // Note instead of invoice
                    + "<div style='margin-bottom:30px;'>"
                    + "<h2 style='font-size:18px;margin-bottom:10px;color:#141415;'>Note:</h2>"
                    + "<p style='font-size:15px;color:#333;'>You can view your purchased games in your <strong>Library</strong>.</p>"
                    + "</div>"
                    // CTA Button
                    + "<div style='text-align:center;margin-top:30px;'>"
                    + "<a href='#' style='display:inline-block;background:linear-gradient(135deg,#df040a,#ff6b27);color:white;text-decoration:none;padding:14px 30px;border-radius:6px;font-weight:600;font-size:16px;margin:20px 0;'>START PLAYING NOW</a>"
                    + "</div>"
                    + "</div>"
                    // Footer
                    + "<div style='background:#141415;padding:20px;text-align:center;color:rgba(255,255,255,0.7);font-size:12px;'>"
                    + "<p>© 2025 Gamerlk. All rights reserved.</p>"
                    + "<p>Having trouble with your order? <a href='#' style='color:#ff6b27;'>Contact Support</a></p>"
                    + "</div>"
                    + "</div>";

            new Thread(() -> {
                Mail.sendMail(user.getEmail(), "Your Gamerlk Purchase Confirmation", emailBody);
            }).start();

            response.setContentType("application/json");
            response.getWriter().write("{\"status\":true,\"message\":\"Email sent\"}");
        } finally {
            s.close();
        }
    }
}
