package ru.itis.repositories;

import ru.itis.dto.AdminMediaItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModerationRepositoryJdbcImpl implements ModerationRepository {

    private static final String SQL_PENDING =
            "SELECT m.id, m.title, t.name AS type_name, u.username, m.release_year, m.description, m.cover_url " +
                    "FROM media m JOIN media_types t ON t.id = m.type_id JOIN users u ON u.id = m.author_id " +
                    "WHERE m.visibility = 'pending' ORDER BY m.id";

    private static final String SQL_APPROVE =
            "UPDATE media SET visibility = 'public', reviewed_by = ?, reviewed_at = now() " +
                    "WHERE id = ? AND visibility = 'pending' RETURNING author_id";

    private static final String SQL_REJECT =
            "UPDATE media SET visibility = 'rejected', reviewed_by = ?, reviewed_at = now() " +
                    "WHERE id = ? AND visibility = 'pending'";

    private static final String SQL_POINTS =
            "INSERT INTO points_log (user_id, media_id, amount, reason) VALUES (?, ?, ?, 'media_approved')";

    private final DataSource dataSource;

    public ModerationRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<AdminMediaItem> findPending() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL_PENDING);
             ResultSet rs = ps.executeQuery()) {
            List<AdminMediaItem> result = new ArrayList<>();
            while (rs.next()) {
                int year = rs.getInt("release_year");
                result.add(AdminMediaItem.builder()
                        .id(rs.getLong("id"))
                        .title(rs.getString("title"))
                        .typeName(rs.getString("type_name"))
                        .authorName(rs.getString("username"))
                        .releaseYear(rs.wasNull() ? null : year)
                        .description(rs.getString("description"))
                        .coverUrl(rs.getString("cover_url"))
                        .build());
            }
            return result;
        }
    }

    @Override
    public boolean approve(Long mediaId, Long adminId, int points) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                Long authorId = null;
                try (PreparedStatement ps = connection.prepareStatement(SQL_APPROVE)) {
                    ps.setLong(1, adminId);
                    ps.setLong(2, mediaId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {            // уже обработана или не существует
                            connection.rollback();
                            return false;
                        }
                        authorId = rs.getLong("author_id");
                        if (rs.wasNull()) authorId = null;
                    }
                }
                if (authorId != null) {
                    try (PreparedStatement ps = connection.prepareStatement(SQL_POINTS)) {
                        ps.setLong(1, authorId);
                        ps.setLong(2, mediaId);
                        ps.setInt(3, points);
                        ps.executeUpdate();
                    }
                }
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    @Override
    public boolean reject(Long mediaId, Long adminId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL_REJECT)) {
            ps.setLong(1, adminId);
            ps.setLong(2, mediaId);
            return ps.executeUpdate() > 0;
        }
    }
}