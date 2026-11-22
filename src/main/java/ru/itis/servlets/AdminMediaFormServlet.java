package ru.itis.servlets;

import ru.itis.dto.AdminMediaForm;
import ru.itis.exceptions.ValidationException;
import ru.itis.services.AdminMediaService;
import ru.itis.services.FileService;
import ru.itis.utils.Flash;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@WebServlet("/admin/media/form")
@MultipartConfig
public class AdminMediaFormServlet extends HttpServlet {

    private AdminMediaService adminMediaService;
    private FileService fileService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        adminMediaService = (AdminMediaService) config.getServletContext().getAttribute("adminMediaService");
        fileService = (FileService) config.getServletContext().getAttribute("fileService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AdminMediaForm form = new AdminMediaForm();
        String idParam = req.getParameter("id");
        try {
            if (idParam != null) {
                Long id = Parse.readLong(idParam);
                Optional<AdminMediaForm> loaded = id == null ? Optional.empty() : adminMediaService.loadForm(id);
                if (loaded.isEmpty()) {
                    Flash.error(req, "Карточка не найдена");
                    resp.sendRedirect(req.getContextPath() + "/admin/media");
                    return;
                }
                form = loaded.get();
            }
            show(req, resp, form);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long adminId = (Long) req.getSession().getAttribute("userId");
        String[] genres = req.getParameterValues("genre");
        Part part = req.getPart("cover");

        AdminMediaForm form = AdminMediaForm.builder()
                .id(req.getParameter("id"))
                .title(req.getParameter("title"))
                .typeId(req.getParameter("type"))
                .releaseYear(req.getParameter("release_year"))
                .description(req.getParameter("description"))
                .pageCount(req.getParameter("page_count"))
                .seasonsCount(req.getParameter("seasons_count"))
                .episodesCount(req.getParameter("episodes_count"))
                .runtime(req.getParameter("runtime"))
                .genreIds(genres == null ? List.of() : Arrays.asList(genres))
                .build();

        String coverUrl = null;
        if (part != null && part.getSize() > 0) {
            try (InputStream inputStream = part.getInputStream()) {
                coverUrl = fileService.saveFile(
                        inputStream,
                        part.getSubmittedFileName(),
                        part.getContentType(),
                        part.getSize()
                );
            }
        }

        try {
            adminMediaService.save(form, coverUrl, adminId);
            Flash.success(req, "Карточка сохранена");
            resp.sendRedirect(req.getContextPath() + "/admin/media");
        } catch (ValidationException e) {
            req.setAttribute("errors", e.getErrors());
            show(req, resp, form);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void show(HttpServletRequest req, HttpServletResponse resp, AdminMediaForm form)
            throws ServletException, IOException {
        try {
            req.setAttribute("types", adminMediaService.types());
            req.setAttribute("genres", adminMediaService.genres());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.setAttribute("form", form);
        req.getRequestDispatcher("/jsp/admin_media_form.jsp").forward(req, resp);
    }
}