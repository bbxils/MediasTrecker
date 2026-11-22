package ru.itis.repositories;

import ru.itis.models.MediaGenre;

import java.sql.SQLException;

public interface MediaGenreRepository extends CrudRepository<MediaGenre, Long> {
    void deleteByMediaId(Long mediaId) throws SQLException;
}
