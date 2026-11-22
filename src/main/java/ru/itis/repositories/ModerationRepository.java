package ru.itis.repositories;

import ru.itis.dto.AdminMediaItem;

import java.sql.SQLException;
import java.util.List;

public interface ModerationRepository {
    List<AdminMediaItem> findPending() throws SQLException;
    boolean approve(Long mediaId, Long adminId, int points) throws SQLException;
    boolean reject(Long mediaId, Long adminId) throws SQLException;
}