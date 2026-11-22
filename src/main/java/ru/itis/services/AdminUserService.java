package ru.itis.services;

import ru.itis.models.User;

import java.sql.SQLException;
import java.util.List;

public interface AdminUserService {
    List<User> findAll() throws SQLException;
    void changeRole(Long actorId, Long targetId, String role) throws SQLException;
    void delete(Long actorId, Long targetId) throws SQLException;
}