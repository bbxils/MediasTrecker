package ru.itis.repositories;

import ru.itis.dto.LibraryItem;
import ru.itis.models.UserMedia;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserMediaRepository extends CrudRepository<UserMedia, Long> {
    List<LibraryItem> findItemsByUser(Long userId) throws SQLException;
    Optional<LibraryItem> findItem(Long userMediaId, Long userId) throws SQLException;
    void update(UserMedia entity) throws SQLException;
    void deleteById(Long id) throws SQLException;
}
