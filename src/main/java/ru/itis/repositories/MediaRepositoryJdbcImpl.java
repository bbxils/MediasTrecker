package ru.itis.repositories;

import ru.itis.dto.AdminMediaItem;
import ru.itis.models.Media;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MediaRepositoryJdbcImpl implements MediaRepository {

    DataSource dataSource;

    private final static String SQL_INSERT_INTO_MEDIA =
            "INSERT INTO media (title, type_id, cover_url, release_year, description, " +
                    "page_count, seasons_count, episodes_count, runtime, visibility, author_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "RETURNING id";

    private static final String SQL_ITEMS_BASE =
            "SELECT m.id, m.title, t.name AS type_name, m.release_year, m.cover_url, " +
                    "(SELECT count(*) FROM user_media um WHERE um.media_id = m.id) AS users_count, " +
                    "COALESCE((SELECT string_agg(g.name, ', ' ORDER BY g.name) FROM media_genres mg " +
                    "JOIN genres g ON g.id = mg.genre_id WHERE mg.media_id = m.id), '') AS genres " +
                    "FROM media m JOIN media_types t ON t.id = m.type_id WHERE m.visibility = 'public'";

    private static final String SQL_SELECT_PUBLIC_CARD =
            "SELECT m.id, m.title, m.type_id, t.name AS type_name, m.cover_url, m.release_year, m.description, " +
                    "m.page_count, m.seasons_count, m.episodes_count, m.runtime " +
                    "FROM media m " +
                    "JOIN media_types t ON t.id = m.type_id " +
                    "WHERE m.id = ? AND m.visibility = 'public'";

    private static final String SQL_INSERT_PUBLIC =
            "INSERT INTO media (title, type_id, cover_url, release_year, description, " +
                    "page_count, seasons_count, episodes_count, runtime, visibility, author_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'public', ?) RETURNING id";

    private static final String SQL_UPDATE_PUBLIC =
            "UPDATE media SET title = ?, type_id = ?, cover_url = ?, release_year = ?, description = ?, " +
                    "page_count = ?, seasons_count = ?, episodes_count = ?, runtime = ? " +
                    "WHERE id = ? AND visibility = 'public'";

    public MediaRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Media> findAll() throws SQLException {
        return List.of();
    }

    @Override
    public Optional<Media> findById(Long id) throws SQLException {
        String sql = "SELECT m.*, t.name AS type_name FROM media m " +
                "JOIN media_types t ON t.id = m.type_id WHERE m.id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(Media.builder()
                            .id(rs.getLong("id"))
                            .title(rs.getString("title"))
                            .typeId(rs.getLong("type_id"))
                            .typeName(rs.getString("type_name"))
                            .coverUrl(rs.getString("cover_url"))
                            .releaseYear(nullableInt(rs, "release_year"))
                            .description(rs.getString("description"))
                            .pageCount(nullableInt(rs, "page_count"))
                            .seasonsCount(nullableInt(rs, "seasons_count"))
                            .episodesCount(nullableInt(rs, "episodes_count"))
                            .runtime(nullableInt(rs, "runtime"))
                            .visibility(rs.getString("visibility"))
                            .authorId(rs.getLong("author_id")) // <-- ВАЖНО
                            .build());
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Media> findByType(Long typeId) throws SQLException {
        return List.of();
    }

    @Override
    public List<Media> findAllPublic() throws SQLException {
        String sql = "SELECT m.id, m.title, m.type_id, t.name AS type_name, m.cover_url, m.release_year, " +
                     "m.description, m.page_count, m.seasons_count, m.episodes_count, m.runtime, " +
                     "COALESCE((SELECT string_agg(g.name, ', ' ORDER BY g.name) " +
                     "FROM media_genres mg JOIN genres g ON g.id = mg.genre_id " +
                     "WHERE mg.media_id = m.id), '') AS genres " +
                     "FROM media m " +
                     "JOIN media_types t ON t.id = m.type_id " +
                     "WHERE m.visibility = 'public' " +
                     "ORDER BY m.title ";

        List<Media> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(Media.builder()
                        .id(rs.getLong("id"))
                        .title(rs.getString("title"))
                        .typeId(rs.getLong("type_id"))
                        .typeName(rs.getString("type_name")) // <-- Наш новый typeName
                        .coverUrl(rs.getString("cover_url"))
                        .releaseYear(nullableInt(rs, "release_year"))
                        .description(rs.getString("description"))
                        .pageCount(nullableInt(rs, "page_count"))
                        .seasonsCount(nullableInt(rs, "seasons_count"))
                        .episodesCount(nullableInt(rs, "episodes_count"))
                        .runtime(nullableInt(rs, "runtime"))
                        .visibility("public")
                        .build());
            }
        }
        return result;
    }

    @Override
    public List<AdminMediaItem> findPublicItems(String query, Long typeId) throws SQLException {
        StringBuilder sql = new StringBuilder(SQL_ITEMS_BASE);
        List<Object> params = new ArrayList<>();
        if (query != null) {
            sql.append(" AND lower(m.title) LIKE lower(?)");
            params.add("%" + query + "%");
        }
        if (typeId != null) {
            sql.append(" AND m.type_id = ?");
            params.add(typeId);
        }
        sql.append(" ORDER BY m.title");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<AdminMediaItem> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(AdminMediaItem.builder()
                            .id(rs.getLong("id"))
                            .title(rs.getString("title"))
                            .typeName(rs.getString("type_name"))
                            .releaseYear(nullableInt(rs, "release_year"))
                            .usersCount(rs.getInt("users_count"))
                            .genres(rs.getString("genres"))
                            .coverUrl(rs.getString("cover_url"))
                            .build());
                }
                return result;
            }
        }
    }

    @Override
    public Optional<Media> findPublicCard(Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL_SELECT_PUBLIC_CARD)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(Media.builder()
                        .id(rs.getLong("id"))
                        .title(rs.getString("title"))
                        .typeId(rs.getLong("type_id"))
                        .typeName(rs.getString("type_name"))
                        .coverUrl(rs.getString("cover_url"))
                        .releaseYear(nullableInt(rs, "release_year"))
                        .description(rs.getString("description"))
                        .pageCount(nullableInt(rs, "page_count"))
                        .seasonsCount(nullableInt(rs, "seasons_count"))
                        .episodesCount(nullableInt(rs, "episodes_count"))
                        .runtime(nullableInt(rs, "runtime"))
                        .visibility("public")
                        .build());
            }
        }
    }

    @Override
    public List<Long> findGenreIds(Long mediaId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT genre_id FROM media_genres WHERE media_id = ?")) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Long> result = new ArrayList<>();
                while (rs.next()) result.add(rs.getLong("genre_id"));
                return result;
            }
        }
    }

    @Override
    public Long createPublic(Media media, List<Long> genreIds) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                Long id;
                try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT_PUBLIC)) {
                    bindCard(ps, media);
                    ps.setObject(10, media.getAuthorId(), Types.BIGINT);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        id = rs.getLong(1);
                    }
                }
                insertGenres(connection, id, genreIds);
                connection.commit();
                return id;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    @Override
    public void update(Media entity) throws SQLException {
        String sql = "UPDATE media SET title = ?, type_id = ?, cover_url = ?, release_year = ?, " +
                "description = ?, page_count = ?, seasons_count = ?, episodes_count = ?, runtime = ? " +
                "WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getTitle());
            ps.setLong(2, entity.getTypeId());
            ps.setString(3, entity.getCoverUrl());
            ps.setObject(4, entity.getReleaseYear(), Types.INTEGER);
            ps.setString(5, entity.getDescription());
            ps.setObject(6, entity.getPageCount(), Types.INTEGER);
            ps.setObject(7, entity.getSeasonsCount(), Types.INTEGER);
            ps.setObject(8, entity.getEpisodesCount(), Types.INTEGER);
            ps.setObject(9, entity.getRuntime(), Types.INTEGER);
            ps.setLong(10, entity.getId());

            ps.executeUpdate();
        }
    }

    @Override
    public boolean deletePublicIfUnused(Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM media WHERE id = ? AND visibility = 'public' " +
                             "AND NOT EXISTS (SELECT 1 FROM user_media WHERE media_id = media.id)")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private void bindCard(PreparedStatement ps, Media m) throws SQLException {
        ps.setString(1, m.getTitle());
        ps.setLong(2, m.getTypeId());
        ps.setString(3, m.getCoverUrl());
        ps.setObject(4, m.getReleaseYear(), Types.INTEGER);
        ps.setString(5, m.getDescription());
        ps.setObject(6, m.getPageCount(), Types.INTEGER);
        ps.setObject(7, m.getSeasonsCount(), Types.INTEGER);
        ps.setObject(8, m.getEpisodesCount(), Types.INTEGER);
        ps.setObject(9, m.getRuntime(), Types.INTEGER);
    }

    private void insertGenres(Connection connection, Long mediaId, List<Long> genreIds) throws SQLException {
        if (genreIds == null || genreIds.isEmpty()) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO media_genres (media_id, genre_id) VALUES (?, ?)")) {
            for (Long genreId : genreIds) {
                ps.setLong(1, mediaId);
                ps.setLong(2, genreId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static Integer nullableInt(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    @Override
    public Media save(Media entity) throws SQLException {
        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_INTO_MEDIA)) {
            preparedStatement.setString(1, entity.getTitle());
            preparedStatement.setLong(2, entity.getTypeId());
            preparedStatement.setString(3, entity.getCoverUrl());
            preparedStatement.setInt(4, entity.getReleaseYear());
            preparedStatement.setString(5, entity.getDescription());
            setNullableInt(preparedStatement, 6, entity.getPageCount());
            setNullableInt(preparedStatement, 7, entity.getSeasonsCount());
            setNullableInt(preparedStatement, 8, entity.getEpisodesCount());
            setNullableInt(preparedStatement, 9, entity.getRuntime());
            preparedStatement.setString(10, entity.getVisibility() != null ? entity.getVisibility() : "private");
            setNullableLong(preparedStatement, 11, entity.getAuthorId());

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    entity.setId(rs.getLong("id"));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения медиа", e);
        }
    }

    private void setNullableInt(PreparedStatement preparedStatement, int idx, Integer val) throws SQLException {
        if (val != null) preparedStatement.setInt(idx, val);
        else preparedStatement.setNull(idx, Types.INTEGER);
    }
    private void setNullableLong(PreparedStatement preparedStatement, int idx, Long val) throws SQLException {
        if (val != null) preparedStatement.setLong(idx, val);
        else preparedStatement.setNull(idx, Types.BIGINT);
    }
}
