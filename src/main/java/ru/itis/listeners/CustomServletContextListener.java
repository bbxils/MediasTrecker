package ru.itis.listeners;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itis.repositories.*;
import ru.itis.services.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.io.File;

@WebListener
public class CustomServletContextListener implements ServletContextListener {

    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "WR3ZJDPX";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/semestr_bd";
    private static final String DB_DRIVER = "org.postgresql.Driver";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        DriverManagerDataSource dataSource = new DriverManagerDataSource(DB_URL);
        dataSource.setUsername(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setDriverClassName(DB_DRIVER);

        String uploadDir = System.getProperty("user.home") + "/media-tracker/uploads";

        UserRepository userRepository = new UserRepositoryJdbcImpl(dataSource);
        MediaRepository mediaRepository = new MediaRepositoryJdbcImpl(dataSource);
        GenresRepository genresRepository = new GenresRepositoryJdbcImpl(dataSource);
        MediaTypeRepository mediaTypeRepository = new MediaTypeRepositoryJdbcImpl(dataSource);
        MediaGenreRepository mediaGenreRepository = new MediaGenreRepositoryJdbcImpl(dataSource);
        UserMediaRepository userMediaRepository = new UserMediaRepositoryJdbcImpl(dataSource);
        ModerationRepository moderationRepository = new ModerationRepositoryJdbcImpl(dataSource);
        MediaCardValidator mediaCardValidator = new MediaCardValidator(mediaTypeRepository, genresRepository);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        MediaService mediaService = new MediaServiceImpl(
                mediaRepository, userMediaRepository, mediaGenreRepository, mediaTypeRepository);
        FileService fileService = new FileServiceImpl(uploadDir);
        AuthService authService = new AuthServiceImpl(userRepository, passwordEncoder);
        AdminMediaService adminMediaService = new AdminMediaServiceImpl(
                mediaRepository, mediaTypeRepository, genresRepository, mediaCardValidator, mediaGenreRepository);
        AdminUserService adminUserService = new AdminUserServiceImpl(userRepository);
        ModerationService moderationService = new ModerationServiceImpl(moderationRepository);
        GenreService genreService = new GenreServiceImpl(genresRepository);

        servletContext.setAttribute("mediaService", mediaService);
        servletContext.setAttribute("genresRepository", genresRepository);
        servletContext.setAttribute("mediaRepository", mediaRepository);
        servletContext.setAttribute("mediaTypeRepository", mediaTypeRepository);
        sce.getServletContext().setAttribute("fileService", fileService);
        servletContext.setAttribute("uploadDir", uploadDir);
        servletContext.setAttribute("authService", authService);
        servletContext.setAttribute("adminMediaService", adminMediaService);
        servletContext.setAttribute("adminUserService", adminUserService);
        servletContext.setAttribute("moderationService", moderationService);
        servletContext.setAttribute("genreService", genreService);
        servletContext.setAttribute("userMediaRepository", userMediaRepository);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
