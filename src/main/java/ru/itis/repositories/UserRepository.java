package ru.itis.repositories;

import ru.itis.models.User;

import java.sql.SQLException;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username) throws SQLException;
    Optional<User> findByEmail(String email) throws SQLException;
    boolean updateRole(Long id, String role) throws SQLException;
    boolean deleteWithPrivateCards(Long id) throws SQLException;
}
