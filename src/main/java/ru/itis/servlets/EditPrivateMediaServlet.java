package ru.itis.servlets;

import ru.itis.models.Media;
import ru.itis.models.MediaGenre;
import ru.itis.repositories.GenresRepository;
import ru.itis.repositories.MediaGenreRepository;
import ru.itis.repositories.MediaRepository;
import ru.itis.repositories.MediaTypeRepository;
import ru.itis.services.FileService;
import ru.itis.utils.Parse;

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
import java.util.List;

@WebServlet("/myLibrary/card/edit-media")
@MultipartConfig
public class EditPrivateMediaServlet extends HttpServlet {

    private MediaRepository mediaRepository;
    private GenresRepository genresRepository;
    private MediaTypeRepository mediaTypeRepository;
    private MediaGenreRepository mediaGenreRepository;
    private FileService fileService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        var ctx = config.getServletContext();
        mediaRepository = (MediaRepository) ctx.getAttribute("mediaRepository");
        genresRepository = (GenresRepository) ctx.getAttribute("genresRepository");
        mediaTypeRepository = (MediaTypeRepository) ctx.getAttribute("mediaTypeRepository");
        mediaGenreRepository = (MediaGenreRepository) ctx.getAttribute("mediaGenreRepository");
        fileService = (FileService) ctx.getAttribute("fileService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        Long mediaId = Parse.readLong(req.getParameter("id"));

        if (mediaId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Media media = mediaRepository.findById(mediaId).orElse(null);

            if (media == null || !"private".equals(media.getVisibility()) || !userId.equals(media.getAuthorId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Вы не можете редактировать эту карточку");
                return;
            }

            List<Long> currentGenreIds = mediaRepository.findGenreIds(mediaId);

            req.setAttribute("media", media);
            req.setAttribute("currentGenreIds", currentGenreIds);
            req.setAttribute("genres", genresRepository.findAll());
            req.setAttribute("types", mediaTypeRepository.findAll());

            req.getRequestDispatcher("/jsp/edit_private_media.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Long userId = (Long) req.getSession().getAttribute("userId");
        Long mediaId = Parse.readLong(req.getParameter("id"));

        if (mediaId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Media media = mediaRepository.findById(mediaId).orElse(null);

            if (media == null || !"private".equals(media.getVisibility()) || !userId.equals(media.getAuthorId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Вы не можете редактировать эту карточку");
                return;
            }

            media.setTitle(req.getParameter("title"));
            media.setDescription(req.getParameter("description"));
            media.setTypeId(Parse.readLong(req.getParameter("type")));
            media.setReleaseYear(Parse.readInt(req.getParameter("release_year")));
            media.setPageCount(Parse.readInt(req.getParameter("page_count")));
            media.setSeasonsCount(Parse.readInt(req.getParameter("seasons_count")));
            media.setEpisodesCount(Parse.readInt(req.getParameter("episodes_count")));
            media.setRuntime(Parse.readInt(req.getParameter("runtime")));

            Part part = req.getPart("cover");
            if (part != null && part.getSize() > 0) {
                try (InputStream is = part.getInputStream()) {
                    String newCoverUrl = fileService.saveFile(is, part.getSubmittedFileName(), part.getContentType(), part.getSize());
                    media.setCoverUrl(newCoverUrl);
                }
            }

            mediaRepository.update(media);
            mediaGenreRepository.deleteByMediaId(mediaId);

            String[] genreParams = req.getParameterValues("genre");
            if (genreParams != null) {
                for (String gId : genreParams) {
                    Long genreId = Parse.readLong(gId);
                    if (genreId != null) {
                        mediaGenreRepository.save(MediaGenre.builder()
                                .mediaId(mediaId)
                                .genreId(genreId)
                                .build());
                    }
                }
            }

            req.getSession().setAttribute("flashSuccess", "Карточка успешно обновлена");
            resp.sendRedirect(req.getContextPath() + "/myLibrary");

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}