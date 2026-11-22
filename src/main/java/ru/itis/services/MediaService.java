package ru.itis.services;

import ru.itis.dto.LibraryItem;
import ru.itis.dto.PersonalMediaForm;
import ru.itis.exceptions.ValidationException;
import ru.itis.models.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MediaService {
    LibraryItem addPersonalMedia(PersonalMediaForm form, Long userId) throws SQLException;
    List<LibraryItem> getLibrary(Long userId) throws SQLException;
    Optional<LibraryItem> getItem(Long id, Long userId) throws SQLException;
}
