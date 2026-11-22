package ru.itis.servlets;

import ru.itis.models.UserMedia;
import ru.itis.repositories.UserMediaRepository;
import ru.itis.utils.Parse;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/myLibrary/add")
public class AddToLibraryServlet extends HttpServlet {

    private UserMediaRepository userMediaRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        userMediaRepository = (UserMediaRepository) config.getServletContext().getAttribute("userMediaRepository");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Long userId = (Long) req.getSession().getAttribute("userId");

        // Собираем данные из формы
        UserMedia userMedia = UserMedia.builder()
                .userId(userId)
                .mediaId(Parse.readLong(req.getParameter("mediaId")))
                .status(req.getParameter("status"))
                .rating(Parse.readInt(req.getParameter("rating")))
                .note(req.getParameter("note"))
                .seasonsWatched(Parse.readInt(req.getParameter("seasonsWatched")))
                .episodesWatched(Parse.readInt(req.getParameter("episodesWatched")))
                .pagesRead(Parse.readInt(req.getParameter("pagesRead")))
                .build();

        try {
            userMediaRepository.save(userMedia);
            req.getSession().setAttribute("flashSuccess", "Произведение добавлено в вашу библиотеку!");
            resp.sendRedirect(req.getContextPath() + "/catalog");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}