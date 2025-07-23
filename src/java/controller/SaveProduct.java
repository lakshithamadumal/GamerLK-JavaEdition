package controller;

import hibernate.Category;
import hibernate.Developer;
import hibernate.HibernateUtil;
import hibernate.Mode;
import hibernate.Product;
import hibernate.Requirement;
import hibernate.Status;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author Laky
 */
@MultipartConfig
@WebServlet(name = "SaveProduct", urlPatterns = {"/SaveProduct"})
public class SaveProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String gameName = request.getParameter("gameName");
        String gameDescription = request.getParameter("gameDescription");
        String gamePrice = request.getParameter("gamePrice");
        String gameLink = request.getParameter("gameLink");
        String gameSize = request.getParameter("gameSize");
        String gameDate = request.getParameter("gameDate");
        String gameCategory = request.getParameter("gameCategory");
        String gameMod = request.getParameter("gameMod");
        String gameDeveloper = request.getParameter("gameDeveloper");
        String gameTag = request.getParameter("gameTag");
        String gameMin = request.getParameter("gameMin");
        String gameMax = request.getParameter("gameMax");
        //Image Uploading
        Part thumbnailImage = request.getPart("thumbnailImage");

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Category category = (Category) s.load(Category.class, Integer.parseInt(gameCategory));
        Mode mode = (Mode) s.load(Mode.class, Integer.parseInt(gameMod));
        Developer developer = (Developer) s.load(Developer.class, Integer.parseInt(gameDeveloper));
        Requirement minRequirement = (Requirement) s.load(Requirement.class, Integer.parseInt(gameMin));
        Requirement maxRequirement = (Requirement) s.load(Requirement.class, Integer.parseInt(gameMax));
        Status status = (Status) s.load(Status.class, 1); //Active

        Product p = new Product();
        p.setCategory_id(category);
        p.setMode_id(mode);
        p.setDeveloper_id(developer);
        p.setMin_requirement_id(minRequirement);
        p.setRec_requirement_id(maxRequirement);

        p.setTitle(gameName);
        p.setDescription(gameDescription);
        p.setPrice(Double.parseDouble(gamePrice));
        p.setGame_link(gameLink);
        p.setGame_size(Double.parseDouble(gameSize));
        p.setRelease_date(Date.valueOf(gameDate));
        p.setTag(gameTag);
        p.setOffer(0);
        p.setStatus_id(status);

        p.setCreated_at(new java.util.Date());

//        s.save(p);
//        s.beginTransaction().commit();
//        s.close();

        String appPath = getServletContext().getRealPath("");
        String newPath = appPath.replace("build\\web", "web\\assets\\Games");
        File f = new File(newPath, "11");
        f.mkdir();

    }

}
