package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Laky
 */
@WebServlet(name = "SendEmail", urlPatterns = {"/SendEmail"})
public class SendEmail extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject SendEmail = gson.fromJson(request.getReader(), JsonObject.class);

        String emailAddress = SendEmail.get("emailAddress").getAsString();
        String emailSubject = SendEmail.get("emailSubject").getAsString();
        String emailBody = SendEmail.get("emailBody").getAsString();
        boolean sendToAll = SendEmail.get("sendToAll").getAsBoolean();

        //Validation
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        if (emailSubject.isEmpty()) {
            responseObject.addProperty("message", "Subject cannot be empty.");
            response.getWriter().print(responseObject.toString());
            return;
        }

        if (emailBody.isEmpty()) {
            responseObject.addProperty("message", "Body cannot be empty.");
            response.getWriter().print(responseObject.toString());
            return;
        }

        if (emailAddress.isEmpty() && !sendToAll) {
            responseObject.addProperty("message", "Either provide an email address or enable 'Send to All'.");
            return;
        }
        
        

    }

}
