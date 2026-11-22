package ru.itis.servlets;

import ru.itis.exceptions.ValidationException;
import ru.itis.services.AdminUserService;
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

@WebServlet("/admin/users")
public class AdminUsersServlet extends HttpServlet {

    private AdminUserService adminUserService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        adminUserService = (AdminUserService) config.getServletContext().getAttribute("adminUserService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("users", adminUserService.findAll());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/jsp/admin_users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long actorId = (Long) req.getSession().getAttribute("userId");
        Long targetId = Parse.readLong(req.getParameter("id"));
        String action = req.getParameter("action");
        try {
            if (targetId == null || action == null) {
                throw new ValidationException(Map.of("form", "Некорректный запрос"));
            }
            switch (action) {
                case "role":
                    adminUserService.changeRole(actorId, targetId, req.getParameter("role"));
                    Flash.success(req, "Роль изменена");
                    break;
                case "delete":
                    adminUserService.delete(actorId, targetId);
                    Flash.success(req, "Пользователь удалён");
                    break;
                default:
                    throw new ValidationException(Map.of("form", "Неизвестное действие"));
            }
        } catch (ValidationException e) {
            Flash.error(req, e);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }
}