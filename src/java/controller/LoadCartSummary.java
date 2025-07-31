package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.OrderItem;
import hibernate.Orders;
import hibernate.Product;
import hibernate.Status;
import hibernate.User;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.PayHere;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadCartSummary", urlPatterns = {"/LoadCartSummary"})
public class LoadCartSummary extends HttpServlet {

    private static final int RATING_DEFAULT_VALUE = 0;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        Gson gson = new Gson();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        Transaction tr = s.beginTransaction();

        if (user != null) {

//            Criteria criteriaCart = s.createCriteria(Cart.class);
//            criteriaCart.add(Restrictions.eq("user_id", user));
//            List<Cart> cartList = criteriaCart.list();
//
//            JsonArray cartItemsArray = new JsonArray();
//            double totalPrice = 0.0;
//
//            for (Cart cart : cartList) {
//                Product p = cart.getProduct_id();
//
//                JsonObject itemObj = new JsonObject();
//                itemObj.addProperty("id", p.getId());
//                itemObj.addProperty("name", p.getTitle());
//                itemObj.addProperty("price", p.getPrice());
//
//                totalPrice += p.getPrice();
//                cartItemsArray.add(itemObj);
//            }
//
////            responseObject.addProperty("status", true);
//            responseObject.add("cartItems", cartItemsArray);
//            responseObject.addProperty("totalPrice", totalPrice);
//            responseObject.addProperty("userEmail", user.getEmail()); // Optional
            processCheckout(s, tr, user, responseObject);

            s.close();
        } else {

            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "User not found");
        }
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

    private void processCheckout(Session s,
            Transaction tr,
            User user,
            JsonObject responseObject) {

        try {

            Orders orders = new Orders();
            orders.setCreated_at(new java.util.Date());
            orders.setUser_id(user);

            int orderId = (int) s.save(orders);

            Criteria criteriaCart = s.createCriteria(Cart.class);
            criteriaCart.add(Restrictions.eq("user_id", user));
            List<Cart> cartList = criteriaCart.list();

//          ArrayList<JsonObject> cartItemList = new ArrayList<>();
            double totalPrice = 0.0;

            Status status = (Status) s.load(Status.class, 4);

            for (Cart cart : cartList) {

                OrderItem orderItem = new OrderItem();

                Product product = cart.getProduct_id();
                totalPrice += product.getPrice();

                orderItem.setRating(LoadCartSummary.RATING_DEFAULT_VALUE); //0
                orderItem.setProduct_id(product);
                orderItem.setStatus_id(status);
                orderItem.setOrders_id(orders);

                s.save(orderItem);

                s.delete(cart);

//                JsonObject items = new JsonObject();
//                items.addProperty("id", product.getId());
//                items.addProperty("name", product.getTitle());
//                items.addProperty("price", product.getPrice());
//                cartItemList.add(items);
            }

            tr.commit();

            double totalPriceUSD = totalPrice * 300;

            //PayHere process
            String merahantID = "1231373"; // <-- your Merchant ID
            String merchantSecret = "MTU5MDE2NzYwNjMxNzYwMjA3MTQyNjAyMjAwMTM0MTA4NTc5MjQ="; // <-- your Merchant Secret
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
            payHereJson.addProperty("items", "Gamerlk game Purchase");
//            payHereJson.addProperty("items", items);
            payHereJson.addProperty("amount", formattedAmount);
            payHereJson.addProperty("currency", currency);
            payHereJson.addProperty("hash", hash);

            payHereJson.addProperty("first_name", user.getFirst_name());
            payHereJson.addProperty("last_name", user.getLast_name());
            payHereJson.addProperty("email", user.getEmail());

            payHereJson.addProperty("phone", "0771234567"); // Add dummy
            payHereJson.addProperty("address", "123 Street, City");
            payHereJson.addProperty("city", "Colombo");
            payHereJson.addProperty("country", "Sri Lanka");

            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Cechkout completed");
            responseObject.add("payhereJson", new Gson().toJsonTree(payHereJson));

        } catch (Exception e) {
            tr.rollback();
        }

    }

}
