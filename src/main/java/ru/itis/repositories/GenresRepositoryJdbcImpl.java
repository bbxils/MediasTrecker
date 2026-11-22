package ru.itis.repositories;

import ru.itis.models.Genre;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GenresRepositoryJdbcImpl implements GenresRepository {

    private static final String SQL_SELECT_FROM_GENRES = "SELECT id, name FROM genres";
    DataSource dataSource;

    public GenresRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Genre save(Genre entity) throws SQLException {
        return entity;
    }

    @Override
    public List<Genre> findAll() throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.executeQuery(SQL_SELECT_FROM_GENRES);

        List<Genre> result = new ArrayList<>();
        ResultSet resultSet = statement.getResultSet();
        while (resultSet.next()) {
            Genre genre = Genre.builder()
                    .id(resultSet.getLong("id"))
                    .name(resultSet.getString("name"))
                    .build();
            result.add(genre);
        }
        return result;
    }

    @Override
    public Optional<Genre> findById(Long aLong) throws SQLException {
        return Optional.empty();
    }

    @Override
    public void create(String name) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO genres (name) VALUES (?)")) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public boolean existsByName(String name, long exceptId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT 1 FROM genres WHERE lower(name) = lower(?) AND id <> ?")) {
            preparedStatement.setString(1, name);
            preparedStatement.setLong(2, exceptId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean rename(Long id, String name) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE genres SET name = ? WHERE id = ?")) {
            ps.setString(1, name);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteIfUnused(Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM genres WHERE id = ? " +
                             "AND NOT EXISTS (SELECT 1 FROM media_genres WHERE genre_id = genres.id)")) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }
}
