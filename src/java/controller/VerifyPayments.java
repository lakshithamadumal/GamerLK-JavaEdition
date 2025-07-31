package controller;

import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.OrderItem;
import hibernate.Orders;
import hibernate.Status;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.PayHere;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author Laky
 */
@WebServlet(name = "VerifyPayments", urlPatterns = {"/VerifyPayments"})
public class VerifyPayments extends HttpServlet {

    private static final int PAYHERE_SUCCESS = 2;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String merchant_id = request.getParameter("merchant_id");
        String order_id = request.getParameter("order_id");
        String payhere_amount = request.getParameter("payhere_amount");
        String payhere_currency = request.getParameter("payhere_currency");
        String status_code = request.getParameter("status_code");
        String md5sig = request.getParameter("md5sig");

        String merchantSecret = "MTU5MDE2NzYwNjMxNzYwMjA3MTQyNjAyMjAwMTM0MTA4NTc5MjQ=";
        String merchantSecretMD5 = PayHere.generateMD5(merchantSecret);
        String expectedHash = PayHere.generateMD5(merchant_id + order_id + payhere_amount + payhere_currency + merchantSecretMD5);

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        if (md5sig.equals(expectedHash) && Integer.parseInt(status_code) == PAYHERE_SUCCESS) {
            // Payment verified, now add Orders & OrderItem, remove from Cart
            try {
                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();
                Transaction tr = s.beginTransaction();


                out.println("Payment Verified Successfully. Order ID: " + order_id);
            } catch (Exception e) {
                out.println("Payment Verified but DB update failed.");
            }
        } else {
            out.println("Payment Verification Failed.");
        }
    }

}
