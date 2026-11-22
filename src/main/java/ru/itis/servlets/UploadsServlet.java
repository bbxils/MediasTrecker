package ru.itis.servlets;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet("/uploads/*")
public class UploadsServlet extends HttpServlet {

    private Path uploadDir;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String dir = (String) config.getServletContext().getAttribute("uploadDir");
        uploadDir = Paths.get(dir).toAbsolutePath().normalize();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String name = pathInfo == null ? "" : pathInfo.substring(1);

        if (name.isEmpty() || name.contains("/") || name.contains("\\") || name.contains("..")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Path file = uploadDir.resolve(name);
        if (!Files.isRegularFile(file)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String mime = getServletContext().getMimeType(name);
        resp.setContentType(mime != null ? mime : "application/octet-stream");
        resp.setContentLengthLong(Files.size(file));
        Files.copy(file, resp.getOutputStream());
    }
}