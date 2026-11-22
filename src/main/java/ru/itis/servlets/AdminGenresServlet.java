package ru.itis.servlets;

import ru.itis.exceptions.ValidationException;
import ru.itis.services.GenreService;
import ru.itis.utils.Flash;
import ru.itis.utils.Parse;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/admin/genres")
public class AdminGenresServlet extends HttpServlet {

    private GenreService genreService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        genreService = (GenreService) config.getServletContext().getAttribute("genreService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("genres", genreService.findAll());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/jsp/admin_genres.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        Long id = Parse.readLong(req.getParameter("id"));
        try {
            if (action == null) {
                throw new ValidationException(Map.of("form", "Некорректный запрос"));
            }
            switch (action) {
                case "create":
                    genreService.create(req.getParameter("name"));
                    Flash.success(req, "Жанр добавлен");
                    break;
                case "rename":
                    requireId(id);
                    genreService.rename(id, req.getParameter("name"));
                    Flash.success(req, "Жанр переименован");
                    break;
                case "delete":
                    requireId(id);
                    genreService.delete(id);
                    Flash.success(req, "Жанр удалён");
                    break;
                default:
                    throw new ValidationException(Map.of("form", "Неизвестное действие"));
            }
        } catch (ValidationException e) {
            Flash.error(req, e);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/genres");
    }

    private static void requireId(Long id) {
        if (id == null) {
            throw new ValidationException(Map.of("form", "Некорректный запрос"));
        }
    }
}