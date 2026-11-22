package ru.itis.services;

import ru.itis.dto.AdminMediaForm;
import ru.itis.dto.AdminMediaItem;
import ru.itis.models.Genre;
import ru.itis.models.MediaType;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AdminMediaService {
    List<AdminMediaItem> search(String query, Long typeId) throws SQLException;
    Optional<AdminMediaForm> loadForm(Long id) throws SQLException;
    void save(AdminMediaForm form, String newCoverUrl, Long adminId) throws SQLException;
    void delete(Long id) throws SQLException;
    List<MediaType> types() throws SQLException;
    List<Genre> genres() throws SQLException;
}