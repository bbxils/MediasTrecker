package ru.itis.services;

import ru.itis.models.Genre;

import java.sql.SQLException;
import java.util.List;

public interface GenreService {
    List<Genre> findAll() throws SQLException;
    void create(String name) throws SQLException;
    void rename(Long id, String name) throws SQLException;
    void delete(Long id) throws SQLException;
}