package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http. HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

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
        Part cardImage = request.getPart("cardImage");
        Part thumbnailImage = request.getPart("thumbnailImage");

        System.out.println("Game Name: " + gameName);
        System.out.println("Game Description: " + gameDescription);
        System.out.println("Game Price: " + gamePrice);
        System.out.println("Game Link: " + gameLink);
        System.out.println("Game Size: " + gameSize);
        System.out.println("Game Date: " + gameDate);
        System.out.println("Game Category: " + gameCategory);
        System.out.println("Game Mode: " + gameMod);
        System.out.println("Game Developer: " + gameDeveloper);
        System.out.println("Game Tag: " + gameTag);
        System.out.println("Minimum Requirement: " + gameMin);
        System.out.println("Maximum Requirement: " + gameMax);
        System.out.println("Card Image: " + cardImage.getSubmittedFileName());
        System.out.println("Thumbnail Image: " + thumbnailImage.getSubmittedFileName());

    }

}
