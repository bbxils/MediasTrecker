package ru.itis.servlets;

import ru.itis.dto.LibraryItem;
import ru.itis.models.UserMedia;
import ru.itis.repositories.UserMediaRepository;
import ru.itis.services.MediaService;
import ru.itis.utils.Parse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/myLibrary/card")
public class MyLibraryCardServlet extends HttpServlet {

    private MediaService mediaService;
    private UserMediaRepository userMediaRepository;

    @Override
    public void init() {
        mediaService = (MediaService) getServletContext().getAttribute("mediaService");
        userMediaRepository = (UserMediaRepository) getServletContext().getAttribute("userMediaRepository");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        Long id = Parse.readLong(req.getParameter("id"));
        boolean isEdit = "true".equals(req.getParameter("edit"));

        try {
            Optional<LibraryItem> item = id == null ? Optional.empty() : userMediaRepository.findItem(id, userId);
            if (item.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            req.setAttribute("item", item.get());

            if (isEdit) {
                UserMedia userMedia = userMediaRepository.findById(id).orElseThrow();
                req.setAttribute("userMedia", userMedia);
                req.getRequestDispatcher("/jsp/edit_library_card.jsp").forward(req, resp);
            } else {
                req.getRequestDispatcher("/jsp/card.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Long userId = (Long) req.getSession().getAttribute("userId");
        Long id = Parse.readLong(req.getParameter("id"));

        if (id == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            UserMedia userMedia = userMediaRepository.findById(id).orElse(null);
            if (userMedia == null || !userMedia.getUserId().equals(userId)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            if ("delete".equals(req.getParameter("action"))) {
                userMediaRepository.deleteById(id);
                req.getSession().setAttribute("flashSuccess", "Карточка удалена из библиотеки");
            } else {
                userMedia.setStatus(req.getParameter("status"));
                userMedia.setRating(Parse.readInt(req.getParameter("rating")));
                userMedia.setNote(req.getParameter("note"));
                userMedia.setSeasonsWatched(Parse.readInt(req.getParameter("seasonsWatched")));
                userMedia.setEpisodesWatched(Parse.readInt(req.getParameter("episodesWatched")));
                userMedia.setPagesRead(Parse.readInt(req.getParameter("pagesRead")));
                userMedia.setChaptersDone(Parse.readInt(req.getParameter("chaptersDone")));

                userMediaRepository.update(userMedia);
                req.getSession().setAttribute("flashSuccess", "Изменения сохранены");
            }
            resp.sendRedirect(req.getContextPath() + "/myLibrary");

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}