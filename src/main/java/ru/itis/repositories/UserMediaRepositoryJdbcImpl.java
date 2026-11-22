package ru.itis.repositories;

import ru.itis.dto.LibraryItem;
import ru.itis.models.UserMedia;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserMediaRepositoryJdbcImpl implements UserMediaRepository{

    private final DataSource dataSource;

    private static final String SQL_SELECT_ITEMS =
            "SELECT um.id, m.id AS media_id, m.title, m.cover_url, m.description, t.name AS type_name, m.release_year, m.visibility, " +
                    "um.status, um.rating, um.note, um.seasons_watched, um.episodes_watched, um.pages_read, um.chapters_done, " +
                    "m.page_count, m.seasons_count, m.episodes_count, " +
                    "m.runtime AS runtime_minutes, " +
                    "COALESCE((SELECT string_agg(g.name, ', ' ORDER BY g.name) FROM media_genres mg " +
                    "          JOIN genres g ON g.id = mg.genre_id WHERE mg.media_id = m.id), '') AS genres " +
                    "FROM user_media um " +
                    "JOIN media m ON m.id = um.media_id " +
                    "JOIN media_types t ON t.id = m.type_id " +
                    "WHERE um.user_id = ? ";

    private static final String SQL_ITEMS_BY_USER = SQL_SELECT_ITEMS + "ORDER BY um.id DESC";
    private static final String SQL_ITEM_BY_ID = SQL_SELECT_ITEMS + "AND um.id = ?";


    public UserMediaRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public UserMedia save(UserMedia entity) throws SQLException {
        String sql = "INSERT INTO user_media (user_id, media_id, status, rating, note, " +
                     "seasons_watched, episodes_watched, pages_read, chapters_done, completed_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "RETURNING id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, entity.getUserId());
            preparedStatement.setLong(2, entity.getMediaId());
            preparedStatement.setString(3, entity.getStatus());
            setNullableInt(preparedStatement, 4, entity.getRating());
            preparedStatement.setString(5, entity.getNote());
            setNullableInt(preparedStatement, 6, entity.getSeasonsWatched());
            setNullableInt(preparedStatement, 7, entity.getEpisodesWatched());
            setNullableInt(preparedStatement, 8, entity.getPagesRead());
            setNullableInt(preparedStatement, 9, entity.getChaptersDone());
            setNullableTimestamp(preparedStatement, 10, entity.getCompletedAt());

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) entity.setId(rs.getLong("id"));
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения UserMedia", e);
        }
    }

    @Override
    public List<UserMedia> findAll() throws SQLException {
        return List.of();
    }

    @Override
    public Optional<UserMedia> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM user_media WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(UserMedia.builder()
                            .id(rs.getLong("id"))
                            .userId(rs.getLong("user_id"))
                            .mediaId(rs.getLong("media_id"))
                            .status(rs.getString("status"))
                            .rating(getNullableInt(rs, "rating"))
                            .note(rs.getString("note"))
                            .seasonsWatched(getNullableInt(rs, "seasons_watched"))
                            .episodesWatched(getNullableInt(rs, "episodes_watched"))
                            .pagesRead(getNullableInt(rs, "pages_read"))
                            .chaptersDone(getNullableInt(rs, "chapters_done"))
                            .build());
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void update(UserMedia entity) throws SQLException {
        String sql = "UPDATE user_media SET status=?, rating=?, note=?, " +
                "seasons_watched=?, episodes_watched=?, pages_read=?, chapters_done=? " +
                "WHERE id=? AND user_id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entity.getStatus());
            setNullableInt(ps, 2, entity.getRating());
            ps.setString(3, entity.getNote());
            setNullableInt(ps, 4, entity.getSeasonsWatched());
            setNullableInt(ps, 5, entity.getEpisodesWatched());
            setNullableInt(ps, 6, entity.getPagesRead());
            setNullableInt(ps, 7, entity.getChaptersDone());
            ps.setLong(8, entity.getId());
            ps.setLong(9, entity.getUserId());
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM user_media WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<LibraryItem> findItemsByUser(Long userId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL_ITEMS_BY_USER)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<LibraryItem> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(mapItem(rs));
                }
                return result;
            }
        }
    }

    @Override
    public Optional<LibraryItem> findItem(Long userMediaId, Long userId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL_ITEM_BY_ID)) {
            ps.setLong(1, userId);
            ps.setLong(2, userMediaId);       // в WHERE ещё и user_id: чужую запись по id не получить
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapItem(rs)) : Optional.empty();
            }
        }
    }

    private LibraryItem mapItem(ResultSet rs) throws SQLException {
        return LibraryItem.builder()
                .id(rs.getLong("id"))
                .mediaId(rs.getLong("media_id"))
                .title(rs.getString("title"))
                .coverUrl(rs.getString("cover_url"))
                .description(rs.getString("description"))
                .typeName(rs.getString("type_name"))
                .releaseYear(getNullableInt(rs, "release_year"))
                .genres(rs.getString("genres"))
                .status(rs.getString("status"))
                .rating(getNullableInt(rs, "rating"))
                .note(rs.getString("note"))
                .seasonsWatched(getNullableInt(rs, "seasons_watched"))
                .episodesWatched(getNullableInt(rs, "episodes_watched"))
                .pagesRead(getNullableInt(rs, "pages_read"))
                .chaptersDone(getNullableInt(rs, "chapters_done"))
                .pageCount(getNullableInt(rs, "page_count"))
                .seasonsCount(getNullableInt(rs, "seasons_count"))
                .episodesCount(getNullableInt(rs, "episodes_count"))
                .runtime(getNullableInt(rs, "runtime_minutes"))
                .visibility(rs.getString("visibility"))
                .build();
    }

    private void setNullableInt(PreparedStatement preparedStatement, int idx, Integer val) throws SQLException {
        if (val != null) preparedStatement.setInt(idx, val);
        else preparedStatement.setNull(idx, Types.INTEGER);
    }
    private void setNullableTimestamp(PreparedStatement preparedStatement, int idx, java.time.LocalDateTime val) throws SQLException {
        if (val != null) preparedStatement.setTimestamp(idx, Timestamp.valueOf(val));
        else preparedStatement.setNull(idx, Types.TIMESTAMP);
    }

    private static Integer getNullableInt(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }
}
