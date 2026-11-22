package ru.itis.servlets;

import ru.itis.exceptions.ValidationException;
import ru.itis.services.AdminMediaService;
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

@WebServlet("/admin/media")
public class AdminMediaServlet extends HttpServlet {

    private AdminMediaService adminMediaService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        adminMediaService = (AdminMediaService) config.getServletContext().getAttribute("adminMediaService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("q");
        Long typeId = Parse.readLong(req.getParameter("type"));
        try {
            req.setAttribute("items", adminMediaService.search(query, typeId));
            req.setAttribute("types", adminMediaService.types());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.setAttribute("q", query);
        req.setAttribute("typeId", typeId);
        req.getRequestDispatcher("/jsp/catalog.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = Parse.readLong(req.getParameter("id"));
        try {
            if (id == null || !"delete".equals(req.getParameter("action"))) {
                throw new ValidationException(Map.of("form", "Некорректный запрос"));
            }
            adminMediaService.delete(id);
            Flash.success(req, "Карточка удалена");
        } catch (ValidationException e) {
            Flash.error(req, e);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        resp.sendRedirect("/catalog");
    }
}