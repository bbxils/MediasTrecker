package ru.itis.servlets;

import ru.itis.models.Media;
import ru.itis.repositories.MediaRepository;
import ru.itis.utils.Parse;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/catalog/card")
public class CatalogCardServlet extends HttpServlet {

    private MediaRepository mediaRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        mediaRepository = (MediaRepository) config.getServletContext().getAttribute("mediaRepository");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = Parse.readLong(req.getParameter("id"));
        try {
            Media media = id == null ? null : mediaRepository.findPublicCard(id).orElse(null);
            if (media == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            req.setAttribute("media", media);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/jsp/catalog_card.jsp").forward(req, resp);
    }
}