package ru.itis.services;

import ru.itis.dto.AdminMediaItem;
import ru.itis.exceptions.ValidationException;
import ru.itis.repositories.ModerationRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ModerationServiceImpl implements ModerationService {

    private static final int POINTS_MEDIA_APPROVED = 50;
    private static final String ALREADY_DONE = "Заявка уже обработана или не найдена";

    private final ModerationRepository moderationRepository;

    public ModerationServiceImpl(ModerationRepository moderationRepository) {
        this.moderationRepository = moderationRepository;
    }

    @Override
    public List<AdminMediaItem> pending() throws SQLException {
        return moderationRepository.findPending();
    }

    @Override
    public void approve(Long mediaId, Long adminId) throws SQLException {
        if (!moderationRepository.approve(mediaId, adminId, POINTS_MEDIA_APPROVED)) {
            throw new ValidationException(Map.of("form", ALREADY_DONE));
        }
    }

    @Override
    public void reject(Long mediaId, Long adminId) throws SQLException {
        if (!moderationRepository.reject(mediaId, adminId)) {
            throw new ValidationException(Map.of("form", ALREADY_DONE));
        }
    }
}