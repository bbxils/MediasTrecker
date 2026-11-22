package ru.itis.services;

import ru.itis.exceptions.ValidationException;
import ru.itis.models.Genre;
import ru.itis.repositories.GenresRepository;
import ru.itis.utils.Parse;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class GenreServiceImpl implements GenreService {

    private final GenresRepository genresRepository;

    public GenreServiceImpl(GenresRepository genresRepository) {
        this.genresRepository = genresRepository;
    }

    @Override
    public List<Genre> findAll() throws SQLException {
        return genresRepository.findAll();
    }

    @Override
    public void create(String rawName) throws SQLException {
        genresRepository.create(checkName(rawName, 0));
    }

    @Override
    public void rename(Long id, String rawName) throws SQLException {
        String name = checkName(rawName, id);
        if (!genresRepository.rename(id, name)) {
            throw error("Жанр не найден");
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        if (!genresRepository.deleteIfUnused(id)) {
            throw error("Нельзя удалить: жанр используется в карточках (или не найден)");
        }
    }

    private String checkName(String rawName, long exceptId) throws SQLException {
        String name = Parse.blankToNull(rawName);
        if (name == null || name.length() > 50) {
            throw error("Название жанра: от 1 до 50 символов");
        }
        if (genresRepository.existsByName(name, exceptId)) {
            throw error("Такой жанр уже есть");
        }
        return name;
    }

    private static ValidationException error(String message) {
        return new ValidationException(Map.of("form", message));
    }
}