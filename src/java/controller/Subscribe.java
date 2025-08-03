/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Subscriber;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Laky
 */
@WebServlet(name = "Subscribe", urlPatterns = {"/Subscribe"})
public class Subscribe extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject SubscribeJson = gson.fromJson(request.getReader(), JsonObject.class);

        String email = SubscribeJson.get("email").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email Required");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Invalid Email Address");
        } else {

            // Save Subscriber
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            //Have A Subscriber
            Criteria criteriaEmail = s.createCriteria(Subscriber.class);
            criteriaEmail.add(Restrictions.eq("email", email));

            if (!criteriaEmail.list().isEmpty()) {
                responseObject.addProperty("message", "Already subscribed!");
            } else {

                Subscriber subscribe = new Subscriber();
                subscribe.setEmail(email);

                s.save(subscribe);
                s.beginTransaction().commit();
                s.close();

                //Send Email
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String emailBody = ""
                                + "<div style='max-width: 500px; margin: 20px auto; background: white; border-radius: 12px; "
                                + "overflow: hidden; box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05); font-family: Outfit, sans-serif;'>"
                                + "<div style='background: linear-gradient(135deg, #df040a, #ff6b27); padding: 30px; text-align: center;'>"
                                + "  <div style='padding: 0 36px 26px 36px;'>"
                                + "    <span style='font-family: Cyberjunkies, Outfit, Arial, sans-serif; letter-spacing: 3px; font-size: 2.15rem; color: #fff;'>"
                                + "      <strong>GAMERLK</strong>"
                                + "    </span>"
                                + "  </div>"
                                + "  <h1 style='color: white; font-size: 24px; margin: 0; font-weight: 700;'>Thanks for Subscribing!</h1>"
                                + "</div>"
                                + "<div style='padding: 30px; text-align: center;'>"
                                + "  <p style='font-size: 16px; line-height: 1.6; margin-bottom: 20px;'>"
                                + "    Strap in, Gamer.<br />You’ve joined the squad. Time to conquer worlds!"
                                + "  </p>"
                                + "  <a href='http://localhost:8080/Gamerlk/en/pages/game-store.html' style='display: inline-block; background: linear-gradient(135deg, #df040a, #ff6b27); "
                                + "color: white; text-decoration: none; padding: 12px 30px; border-radius: 6px; font-weight: 600; "
                                + "font-size: 16px; margin: 20px 0; transition: all 0.3s ease;'>START PLAYING</a>"
                                + "  <p style='font-size: 14px; color: #666; margin-top: 30px;'>"
                                + "    The Gamerlk Team"
                                + "  </p>"
                                + "</div>"
                                + "<div style='background: #141415; padding: 40px 20px; display: flex; flex-direction: column; align-items: center; justify-content: center; "
                                + "color: rgba(255, 255, 255, 0.7); font-size: 12px; text-align: center;'>"
                                + "  <p style='margin: 0;'>© 2025 <strong>Gamerlk</strong>. All rights reserved.</p>"
                                + "</div>"
                                + "</div>";

                        Mail.sendMail(email, "Thanks for Subscribing!", emailBody);
                    }
                }).start();

                responseObject.addProperty("status", Boolean.TRUE);
                responseObject.addProperty("message", "Thanks for subscribing!");
            }
        }
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

}
