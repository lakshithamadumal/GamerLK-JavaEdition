package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.*;

@WebServlet(name = "AdminLoadAllDashboardData", urlPatterns = {"/AdminLoadAllDashboardData"})
public class AdminLoadAllDashboardData extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject obj = new JsonObject();
        obj.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            // --- Today Earning ---
            double todayEarning = 0.0;
            Date today = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String todayStr = sdf.format(today);

            Criteria orderItemCriteria = s.createCriteria(OrderItem.class);
            List<OrderItem> orderItems = orderItemCriteria.list();

            for (OrderItem item : orderItems) {
                Orders order = item.getOrders_id();
                if (order != null && order.getCreated_at() != null) {
                    String orderDate = sdf.format(order.getCreated_at());
                    if (orderDate.equals(todayStr)) {
                        Product p = item.getProduct_id();
                        if (p != null) todayEarning += p.getPrice();
                    }
                }
            }
            obj.addProperty("todayEarning", todayEarning);

            // --- Total Earning ---
            double totalEarning = 0.0;
            for (OrderItem item : orderItems) {
                Product p = item.getProduct_id();
                if (p != null) totalEarning += p.getPrice();
            }
            obj.addProperty("totalEarning", totalEarning);

            // --- Selling Games (distinct product ids in OrderItem) ---
            Set<Integer> sellingGameIds = new HashSet<>();
            for (OrderItem item : orderItems) {
                Product p = item.getProduct_id();
                if (p != null) sellingGameIds.add(p.getId());
            }
            obj.addProperty("sellingGames", sellingGameIds.size());

            // --- Total Customer ---
            Criteria userCriteria = s.createCriteria(User.class);
            int totalCustomer = userCriteria.list().size();
            obj.addProperty("totalCustomer", totalCustomer);

            // --- Monthly Sales Chart (last 12 months) ---
            Map<String, Integer> monthlySales = new LinkedHashMap<>();
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat monthFormat = new SimpleDateFormat("MM/yyyy");
            for (int i = 11; i >= 0; i--) {
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, -i);
                String key = monthFormat.format(cal.getTime());
                monthlySales.put(key, 0);
            }
            for (OrderItem item : orderItems) {
                Orders order = item.getOrders_id();
                if (order != null && order.getCreated_at() != null) {
                    String key = monthFormat.format(order.getCreated_at());
                    if (monthlySales.containsKey(key)) {
                        monthlySales.put(key, monthlySales.get(key) + 1);
                    }
                }
            }
            JsonArray monthlySalesArr = new JsonArray();
            for (String key : monthlySales.keySet()) {
                JsonObject m = new JsonObject();
                m.addProperty("month", key);
                m.addProperty("count", monthlySales.get(key));
                monthlySalesArr.add(m);
            }
            obj.add("monthlySales", monthlySalesArr);

            // --- Top Selling Games (top 4 by sales count) ---
            Map<Integer, Integer> gameSalesCount = new HashMap<>();
            Map<Integer, Double> gameRevenue = new HashMap<>();
            Map<Integer, Product> productMap = new HashMap<>();
            for (OrderItem item : orderItems) {
                Product p = item.getProduct_id();
                if (p != null) {
                    int pid = p.getId();
                    gameSalesCount.put(pid, gameSalesCount.getOrDefault(pid, 0) + 1);
                    gameRevenue.put(pid, gameRevenue.getOrDefault(pid, 0.0) + p.getPrice());
                    productMap.put(pid, p);
                }
            }
            List<Map.Entry<Integer, Integer>> sortedGames = new ArrayList<>(gameSalesCount.entrySet());
            sortedGames.sort((a, b) -> b.getValue() - a.getValue());
            JsonArray topGamesArr = new JsonArray();
            for (int i = 0; i < Math.min(4, sortedGames.size()); i++) {
                int pid = sortedGames.get(i).getKey();
                Product p = productMap.get(pid);
                JsonObject g = new JsonObject();
                g.addProperty("title", p.getTitle());
                g.addProperty("id", p.getId());
                g.addProperty("developer", p.getDeveloper_id() != null ? p.getDeveloper_id().getName() : "-");
                g.addProperty("sold", gameSalesCount.get(pid));
                g.addProperty("revenue", gameRevenue.get(pid));
                topGamesArr.add(g);
            }
            obj.add("topGames", topGamesArr);

            // --- Recent Orders (last 4 orders) ---
            Criteria orderCriteria = s.createCriteria(Orders.class);
            orderCriteria.addOrder(org.hibernate.criterion.Order.desc("created_at"));
            orderCriteria.setMaxResults(4);
            List<Orders> recentOrders = orderCriteria.list();
            JsonArray recentOrdersArr = new JsonArray();
            for (Orders order : recentOrders) {
                JsonObject o = new JsonObject();
                o.addProperty("id", order.getId());
                User u = order.getUser_id();
                o.addProperty("customer", u != null ? u.getFirst_name() + " " + u.getLast_name() : "-");
                o.addProperty("date", sdf.format(order.getCreated_at()));
                // Total price for this order
                Criteria oiCrit = s.createCriteria(OrderItem.class);
                oiCrit.add(org.hibernate.criterion.Restrictions.eq("orders_id", order));
                List<OrderItem> items = oiCrit.list();
                double price = 0.0;
                JsonArray gamesArr = new JsonArray();
                for (OrderItem item : items) {
                    Product p = item.getProduct_id();
                    if (p != null) {
                        price += p.getPrice();
                        JsonObject gameObj = new JsonObject();
                        gameObj.addProperty("title", p.getTitle());
                        gameObj.addProperty("price", p.getPrice());
                        gamesArr.add(gameObj);
                    }
                }
                o.addProperty("price", price);
                o.add("games", gamesArr);
                o.addProperty("status", "Completed");
                recentOrdersArr.add(o);
            }
            obj.add("recentOrders", recentOrdersArr);

            // --- Top Customers (top 4 by games owned) ---
            Criteria allUserCriteria = s.createCriteria(User.class);
            List<User> allUsers = allUserCriteria.list();
            Map<Integer, Integer> userGameCount = new HashMap<>();
            Map<Integer, Double> userSpend = new HashMap<>();
            Map<Integer, User> userMap = new HashMap<>();
            for (User u : allUsers) {
                Criteria userOrderCrit = s.createCriteria(Orders.class);
                userOrderCrit.add(org.hibernate.criterion.Restrictions.eq("user_id", u));
                List<Orders> userOrders = userOrderCrit.list();
                int count = 0;
                double spend = 0.0;
                for (Orders order : userOrders) {
                    Criteria userOrderItemCrit = s.createCriteria(OrderItem.class);
                    userOrderItemCrit.add(org.hibernate.criterion.Restrictions.eq("orders_id", order));
                    List<OrderItem> userOrderItems = userOrderItemCrit.list();
                    count += userOrderItems.size();
                    for (OrderItem item : userOrderItems) {
                        Product p = item.getProduct_id();
                        if (p != null) spend += p.getPrice();
                    }
                }
                userGameCount.put(u.getId(), count);
                userSpend.put(u.getId(), spend);
                userMap.put(u.getId(), u);
            }
            List<Map.Entry<Integer, Integer>> sortedUsers = new ArrayList<>(userGameCount.entrySet());
            sortedUsers.sort((a, b) -> b.getValue() - a.getValue());
            JsonArray topCustomersArr = new JsonArray();
            for (int i = 0; i < Math.min(4, sortedUsers.size()); i++) {
                int uid = sortedUsers.get(i).getKey();
                User u = userMap.get(uid);
                JsonObject c = new JsonObject();
                c.addProperty("name", u.getFirst_name() + " " + u.getLast_name());
                c.addProperty("registerDate", sdf.format(u.getCreated_at()));
                c.addProperty("spend", userSpend.get(uid));
                c.addProperty("owned", userGameCount.get(uid));
                topCustomersArr.add(c);
            }
            obj.add("topCustomers", topCustomersArr);

            obj.addProperty("status", true);
        } catch (Exception e) {
            obj.addProperty("status", false);
            obj.addProperty("message", "Error loading dashboard data!");
        } finally {
            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(obj));
    }
}