package ru.itis.services;

import ru.itis.exceptions.ValidationException;
import ru.itis.models.User;
import ru.itis.repositories.UserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdminUserServiceImpl implements AdminUserService {

    private static final Set<String> ROLES = Set.of("admin", "user");

    private final UserRepository userRepository;

    public AdminUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() throws SQLException {
        return userRepository.findAll();
    }

    @Override
    public void changeRole(Long actorId, Long targetId, String role) throws SQLException {
        if (actorId.equals(targetId)) {
            throw error("Нельзя изменить собственную роль");
        }
        if (role == null || !ROLES.contains(role)) {
            throw error("Недопустимая роль");
        }
        if (!userRepository.updateRole(targetId, role)) {
            throw error("Пользователь не найден");
        }
    }

    @Override
    public void delete(Long actorId, Long targetId) throws SQLException {
        if (actorId.equals(targetId)) {
            throw error("Нельзя удалить самого себя");
        }
        if (!userRepository.deleteWithPrivateCards(targetId)) {
            throw error("Пользователь не найден");
        }
    }

    private static ValidationException error(String message) {
        return new ValidationException(Map.of("form", message));
    }
}