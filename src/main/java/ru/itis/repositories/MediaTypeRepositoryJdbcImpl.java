package ru.itis.repositories;

import ru.itis.models.MediaType;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MediaTypeRepositoryJdbcImpl implements MediaTypeRepository {

    private static final String SQL_SELECT_FROM_MEDIA_TYPES = "SELECT id, name FROM media_types";
    private static final String SQL_SELECT_BY_ID = "SELECT id, name FROM media_types WHERE id = ?";
    DataSource dataSource;

    public MediaTypeRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public MediaType save(MediaType entity) throws SQLException {
        return null;
    }

    @Override
    public List<MediaType> findAll() throws SQLException {
        List<MediaType> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_FROM_MEDIA_TYPES);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                result.add(MediaType.builder()
                        .id(resultSet.getLong("id"))
                        .name(resultSet.getString("name"))
                        .build());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении типов медиа", e);
        }
        return result;
    }

    @Override
    public Optional<MediaType> findById(Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    private MediaType mapRow(ResultSet resultSet) throws SQLException {
        return MediaType.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
