package ru.itis.repositories;

import ru.itis.models.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryJdbcImpl implements UserRepository {

    private static final String SQL_INSERT =
            "INSERT INTO users (username, email, password_hash, role) VALUES (?, ?, ?, ?) RETURNING id";
    private static final String SQL_SELECT_ALL = "SELECT id, username, email, role FROM users ORDER BY id";
    private static final String SQL_SELECT_BY_USERNAME =
            "SELECT id, username, email, password_hash, role FROM users WHERE username = ?";
    private static final String SQL_SELECT_BY_EMAIL =
            "SELECT id, username, email, password_hash, role FROM users WHERE email = ?";
    private static final String SQL_UPDATE_ROLE = "UPDATE users SET role = ? WHERE id = ?";

    private final DataSource dataSource;

    public UserRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public User save(User user) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPasswordHash());
            preparedStatement.setString(4, user.getRole());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                rs.next();
                user.setId(rs.getLong("id"));
            }
            return user;
        }
    }

    @Override
    public List<User> findAll() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<User> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(User.builder()
                        .id(resultSet.getLong("id"))
                        .username(resultSet.getString("username"))
                        .email(resultSet.getString("email"))
                        .role(resultSet.getString("role"))
                        .build());
            }
            return result;
        }
    }


    @Override
    public Optional<User> findById(Long aLong) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) throws SQLException {
        return findOne(SQL_SELECT_BY_USERNAME, username);
    }

    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        return findOne(SQL_SELECT_BY_EMAIL, email);
    }

    @Override
    public boolean updateRole(Long id, String role) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_ROLE)) {
            preparedStatement.setString(1, role);
            preparedStatement.setLong(2, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteWithPrivateCards(Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "DELETE FROM media WHERE author_id = ? AND visibility <> 'public'")) {
                    preparedStatement.setLong(1, id);
                    preparedStatement.executeUpdate();
                }
                int deleted;
                try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
                    preparedStatement.setLong(1, id);
                    deleted = preparedStatement.executeUpdate();
                }
                connection.commit();
                return deleted > 0;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    private Optional<User> findOne(String sql, String value) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, value);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .username(rs.getString("username"))
                .email(rs.getString("email"))
                .passwordHash(rs.getString("password_hash"))
                .role(rs.getString("role"))
                .build();
    }
}