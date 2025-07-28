package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Mode;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Criteria;

@WebServlet(name = "AdminLoadAllModeData", urlPatterns = {"/AdminLoadAllModeData"})
public class AdminLoadAllModeData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            Criteria criteriaMode = s.createCriteria(Mode.class);
            List<Mode> modeList = criteriaMode.list();

            responseObject.add("modeList", gson.toJsonTree(modeList));
            responseObject.addProperty("status", true);

        } catch (Exception e) {
            responseObject.addProperty("message", "Error loading data!");
        } finally {
            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
