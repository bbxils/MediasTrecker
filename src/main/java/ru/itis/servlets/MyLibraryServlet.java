package ru.itis.servlets;

import ru.itis.dto.PersonalMediaForm;
import ru.itis.exceptions.ValidationException;
import ru.itis.repositories.GenresRepository;
import ru.itis.repositories.MediaTypeRepository;
import ru.itis.services.FileService;
import ru.itis.services.MediaService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/myLibrary")
@MultipartConfig
public class MyLibraryServlet extends HttpServlet {

    private GenresRepository genresRepository;
    private MediaTypeRepository mediaTypeRepository;
    private MediaService mediaService;
    private FileService fileService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        genresRepository = (GenresRepository) config.getServletContext().getAttribute("genresRepository");
        mediaService = (MediaService) config.getServletContext().getAttribute("mediaService");
        mediaTypeRepository = (MediaTypeRepository) config.getServletContext().getAttribute("mediaTypeRepository");
        fileService = (FileService) config.getServletContext().getAttribute("fileService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        try {
            req.setAttribute("genreList", genresRepository.findAll());
            req.setAttribute("mediaTypeList", mediaTypeRepository.findAll());
            req.setAttribute("libraryItems", mediaService.getLibrary(userId));
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/jsp/my_library.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Long userId = (Long) req.getSession().getAttribute("userId");

        String coverUrl = saveCover(req);

        String[] genres = req.getParameterValues("genre");

        PersonalMediaForm form = PersonalMediaForm.builder()
                .title(req.getParameter("title"))
                .coverUrl(coverUrl)
                .typeId(req.getParameter("type"))
                .genreIds(genres == null ? List.of() : Arrays.asList(genres))
                .releaseYear(req.getParameter("release_year"))
                .description(req.getParameter("description"))
                .pageCount(req.getParameter("page_count"))
                .seasonsCount(req.getParameter("seasons_count"))
                .episodesCount(req.getParameter("episodes_count"))
                .runtime(req.getParameter("runtime"))
                .status(req.getParameter("status"))
                .rating(req.getParameter("rating"))
                .note(req.getParameter("note"))
                .seasonsWatched(req.getParameter("seasons_watched"))
                .episodesWatched(req.getParameter("episodes_watched"))
                .pagesRead(req.getParameter("pages_read"))
                .chaptersDone(req.getParameter("chapters_done"))
                .suggestToCatalog("true".equals(req.getParameter("suggestToCatalog")))
                .build();

        try {
            mediaService.addPersonalMedia(form, userId);
            req.getSession().setAttribute("flashSuccess", "Произведение добавлено в библиотеку");
            resp.sendRedirect(req.getContextPath() + "/myLibrary");
        } catch (ValidationException e) {
            req.setAttribute("errors", e.getErrors());
            req.setAttribute("form", form);
            doGet(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private String saveCover(HttpServletRequest req) throws IOException, ServletException {
        Part part = req.getPart("cover");
        if (part == null || part.getSize() == 0) {
            return null;
        }
        try (InputStream in = part.getInputStream()) {
            return fileService.saveFile(in, part.getSubmittedFileName(), part.getContentType(), part.getSize());
        }
    }
}