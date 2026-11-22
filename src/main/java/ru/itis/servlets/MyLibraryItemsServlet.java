package ru.itis.servlets;

import ru.itis.services.MediaService;
import ru.itis.utils.Json;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/myLibrary/items")
public class MyLibraryItemsServlet extends HttpServlet {

    private MediaService mediaService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        mediaService = (MediaService) config.getServletContext().getAttribute("mediaService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        try {
            Json.write(resp, HttpServletResponse.SC_OK, mediaService.getLibrary(userId));
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}