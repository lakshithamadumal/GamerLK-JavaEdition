package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItem;
import hibernate.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Laky
 */
@WebServlet(name = "LoadDownloads", urlPatterns = {"/LoadDownloads"})
public class LoadDownloads extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            // Get all OrderItems for this user (via Orders)
            Criteria criteriaOrderItem = s.createCriteria(OrderItem.class, "orderItem")
                    .createAlias("orders_id", "orders")
                    .add(Restrictions.eq("orders.user_id", user));
            List<OrderItem> downloadList = criteriaOrderItem.list();

            if (downloadList.isEmpty()) {
                responseObject.addProperty("message", "Your Downloads is Empty");
            } else {
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Downloads successfully loaded");
                responseObject.add("downloadItems", gson.toJsonTree(downloadList));
            }
            s.close();
        }
        response.setContentType("application/json");
        String responseText = gson.toJson(responseObject);
        response.getWriter().write(responseText);
    }
}
