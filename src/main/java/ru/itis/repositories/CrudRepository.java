package ru.itis.repositories;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {
    T save(T entity) throws SQLException;
    List<T> findAll() throws SQLException;
    Optional<T> findById(ID id) throws SQLException;
}
