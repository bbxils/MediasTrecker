package ru.itis.repositories;

import ru.itis.models.MediaGenre;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MediaGenreRepositoryJdbcImpl implements MediaGenreRepository {

    DataSource dataSource;

    public MediaGenreRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public MediaGenre save(MediaGenre entity) {
        String sql = "INSERT INTO media_genres (media_id, genre_id) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
            prepareStatement.setLong(1, entity.getMediaId());
            prepareStatement.setLong(2, entity.getGenreId());
            prepareStatement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения MediaGenre", e);
        }
    }

    @Override
    public List<MediaGenre> findAll() throws SQLException {
        return List.of();
    }

    @Override
    public Optional<MediaGenre> findById(Long aLong) throws SQLException {
        return Optional.empty();
    }

    public void deleteByMediaId(Long mediaId) throws SQLException {
        String sql = "DELETE FROM media_genres WHERE media_id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            ps.executeUpdate();
        }
    }
}
