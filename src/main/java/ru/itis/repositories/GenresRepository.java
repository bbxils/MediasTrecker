package ru.itis.repositories;

import ru.itis.models.Genre;

import java.sql.SQLException;

public interface GenresRepository extends CrudRepository<Genre, Long> {
    boolean existsByName(String name, long exceptId) throws SQLException;
    boolean rename(Long id, String name) throws SQLException;
    boolean deleteIfUnused(Long id) throws SQLException;
    void create(String name) throws SQLException;
}
