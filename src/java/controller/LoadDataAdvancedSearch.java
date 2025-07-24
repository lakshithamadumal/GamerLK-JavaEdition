package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.Developer;
import hibernate.HibernateUtil;
import hibernate.Mode;
import hibernate.Requirement;
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

/**
 *
 * @author Laky
 */
@WebServlet(name = "LoadDataAdvancedSearch", urlPatterns = {"/LoadDataAdvancedSearch"})
public class LoadDataAdvancedSearch extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        //Search Category
        Criteria criteriaCategory = s.createCriteria(Category.class);
        List<Category> categoryList = criteriaCategory.list();


        //Search Mode
        Criteria criteriaMode = s.createCriteria(Mode.class);
        List<Mode> modeList = criteriaMode.list();

        s.close();

        Gson gson = new Gson();

        responseObject.add("categoryList", gson.toJsonTree(categoryList));
        responseObject.add("modeList", gson.toJsonTree(modeList));

        responseObject.addProperty("status", Boolean.TRUE);

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
