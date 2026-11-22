package ru.itis.servlets;

import ru.itis.exceptions.ValidationException;
import ru.itis.services.ModerationService;
import ru.itis.utils.Parse;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/moderation")
public class AdminModerationServlet extends HttpServlet {

    private ModerationService moderationService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        moderationService = (ModerationService) config.getServletContext().getAttribute("moderationService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("items", moderationService.pending());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/jsp/admin_moderation.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long adminId = (Long) req.getSession().getAttribute("userId");
        Long mediaId = Parse.readLong(req.getParameter("id"));
        String action = req.getParameter("action");

        if (mediaId == null || action == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            if ("approve".equals(action)) {
                moderationService.approve(mediaId, adminId);
            } else if ("reject".equals(action)) {
                moderationService.reject(mediaId, adminId);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.setContentType("text/plain;charset=UTF-8");
            resp.getWriter().write(String.join("; ", e.getErrors().values()));
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}