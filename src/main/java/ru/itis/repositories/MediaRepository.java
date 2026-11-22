package ru.itis.repositories;

import ru.itis.dto.AdminMediaItem;
import ru.itis.models.Media;
import ru.itis.models.MediaGenre;
import ru.itis.models.UserMedia;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MediaRepository extends CrudRepository<Media, Long> {
    List<Media> findByType(Long typeId) throws SQLException;
    List<AdminMediaItem> findPublicItems(String query, Long typeId) throws SQLException;
    Optional<Media> findPublicCard(Long id) throws SQLException;
    List<Long> findGenreIds(Long mediaId) throws SQLException;
    Long createPublic(Media media, List<Long> genreIds) throws SQLException;
    boolean deletePublicIfUnused(Long id) throws SQLException;
    List<Media> findAllPublic() throws SQLException;
    void update(Media entity) throws SQLException;
}
