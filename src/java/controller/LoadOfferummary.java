package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.PayHere;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Laky
 */
@WebServlet(name = "LoadOfferummary", urlPatterns = {"/LoadOfferummary"})
public class LoadOfferummary extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        Gson gson = new Gson();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String productId = request.getParameter("productId");

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        Transaction tr = s.beginTransaction();

        if (user == null) {
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Login Required");
        } else if (user.getStatus() != null && "Inactive".equalsIgnoreCase(user.getStatus().getValue())) {
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Suspended Account");
        } else if (productId != null && productId.matches("\\d+")) {
            try {
                // 1. Product එක Offer status එකද කියලා බලන්න
                Criteria criteria = s.createCriteria(Product.class);
                criteria.add(Restrictions.eq("id", Integer.parseInt(productId)));
                criteria.createAlias("status_id", "status");
                criteria.add(Restrictions.eq("status.value", "Offer"));
                Product product = (Product) criteria.uniqueResult();

                if (product == null) {
                    responseObject.addProperty("status", false);
                    responseObject.addProperty("message", "Offer product not found!");
                } else {
                    // 2. Offer % එකෙන් real price එක ගණනය කරන්න
                    double price = product.getPrice();
                    int offer = product.getOffer();
                    double realPrice = price - (price * offer / 100.0);

                    // 3. Orders, OrderItem, PayHere logic (ඔයාගේ project එකේ තියෙන විදියට)
                    Orders orders = new Orders();
                    orders.setCreated_at(new java.util.Date());
                    orders.setUser_id(user);
                    int orderId = (int) s.save(orders);

                    Status status = (Status) s.load(Status.class, 4);

                    OrderItem orderItem = new OrderItem();
                    orderItem.setRating(0);
                    orderItem.setProduct_id(product);
                    orderItem.setStatus_id(status);
                    orderItem.setOrders_id(orders);
                    s.save(orderItem);

                    tr.commit();

                    // 4. PayHere process
                    double totalPriceUSD = realPrice * 300;
                    String merahantID = "1231373";
                    String merchantSecret = "MTU5MDE2NzYwNjMxNzYwMjA3MTQyNjAyMjAwMTM0MTA4NTc5MjQ=";
                    String orderID = "#000" + orderId;
                    String currency = "LKR";
                    String formattedAmount = new DecimalFormat("0.00").format(totalPriceUSD);
                    String merchantSecretMD5 = PayHere.generateMD5(merchantSecret);
                    String hash = PayHere.generateMD5(merahantID + orderID + formattedAmount + currency + merchantSecretMD5);

                    JsonObject payHereJson = new JsonObject();
                    payHereJson.addProperty("sandbox", true);
                    payHereJson.addProperty("merchant_id", merahantID);
                    payHereJson.addProperty("return_url", "");
                    payHereJson.addProperty("cancel_url", "");
                    payHereJson.addProperty("notify_url", "https://33c0de43a61f.ngrok-free.app/GhostGamezHouse/VerifyPayments");
                    payHereJson.addProperty("order_id", orderID);
                    payHereJson.addProperty("items", product.getTitle());
                    payHereJson.addProperty("amount", formattedAmount);
                    payHereJson.addProperty("currency", currency);
                    payHereJson.addProperty("hash", hash);
                    payHereJson.addProperty("first_name", user.getFirst_name());
                    payHereJson.addProperty("last_name", user.getLast_name());
                    payHereJson.addProperty("email", user.getEmail());
                    payHereJson.addProperty("phone", "0771234567");
                    payHereJson.addProperty("address", "123 Street, City");
                    payHereJson.addProperty("city", "Colombo");
                    payHereJson.addProperty("country", "Sri Lanka");

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Checkout completed");
                    responseObject.add("payhereJson", gson.toJsonTree(payHereJson));
                    responseObject.addProperty("realPrice", realPrice);
                }
            } catch (Exception e) {
                tr.rollback();
                responseObject.addProperty("status", false);
                responseObject.addProperty("message", "Error processing offer checkout!");
            } finally {
                s.close();
            }
        } else {
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Invalid product id");
        }
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
