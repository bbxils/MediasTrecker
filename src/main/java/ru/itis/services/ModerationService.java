package ru.itis.services;

import ru.itis.dto.AdminMediaItem;

import java.sql.SQLException;
import java.util.List;

public interface ModerationService {
    List<AdminMediaItem> pending() throws SQLException;
    void approve(Long mediaId, Long adminId) throws SQLException;
    void reject(Long mediaId, Long adminId) throws SQLException;
}