package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.PayHere;

/**
 *
 * @author Laky
 */
@WebServlet(name = "VerifyPayments", urlPatterns = {"/VerifyPayments"})
public class VerifyPayments extends HttpServlet {

    private static final int PAYHERE_SUCCESS = 2;

//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//        String merchant_id = request.getParameter("merchant_id");
//        String order_id = request.getParameter("order_id");
//        String payhere_amount = request.getParameter("payhere_amount");
//        String payhere_currency = request.getParameter("payhere_currency");
//        String status_code = request.getParameter("status_code");
//        String md5sig = request.getParameter("md5sig");
//
//        String merchantSecret = "MTUzMTgxMzQzNDE2MTgzMzg3MDQwODMzNjU2MzgzMTgxOTk2MjY1";
//        String merchantSecretMD5 = PayHere.generateMD5(merchantSecret);
//        String hash = PayHere.generateMD5(merchant_id + order_id + payhere_amount + payhere_currency + merchantSecretMD5);
//
//        if (md5sig.equals(hash) && Integer.parseInt(status_code) == VerifyPayments.PAYHERE_SUCCESS) {
//            System.out.println("Payment Completed. Order Id:" + order_id);
//            String orderId = order_id.substring(3);
//            System.out.println(orderId); // 1
//        }
//
//    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String merchant_id = request.getParameter("merchant_id");
        String order_id = request.getParameter("order_id");
        String payhere_amount = request.getParameter("payhere_amount");
        String payhere_currency = request.getParameter("payhere_currency");
        String status_code = request.getParameter("status_code");
        String md5sig = request.getParameter("md5sig");

        String merchantSecret = "MTU5MDE2NzYwNjMxNzYwMjA3MTQyNjAyMjAwMTM0MTA4NTc5MjQ="; // <-- your Merchant Secret
        String merchantSecretMD5 = PayHere.generateMD5(merchantSecret);
        String expectedHash = PayHere.generateMD5(merchant_id + order_id + payhere_amount + payhere_currency + merchantSecretMD5);

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        if (md5sig.equals(expectedHash) && Integer.parseInt(status_code) == PAYHERE_SUCCESS) {
            String orderId = order_id.startsWith("OID") ? order_id.substring(3) : order_id;
            System.out.println("Payment Verified. Order ID: " + orderId);

            // TODO: Update DB status to PAID
            out.println("Payment Verified Successfully. Order ID: " + orderId);
        } else {
            System.out.println("Payment Verification Failed for Order ID: " + order_id);
            out.println("Payment Verification Failed.");
        }
    }

}
