package ru.itis.services;

import ru.itis.dto.AdminMediaForm;
import ru.itis.dto.AdminMediaItem;
import ru.itis.exceptions.ValidationException;
import ru.itis.models.Genre;
import ru.itis.models.Media;
import ru.itis.models.MediaGenre;
import ru.itis.models.MediaType;
import ru.itis.repositories.GenresRepository;
import ru.itis.repositories.MediaGenreRepository;
import ru.itis.repositories.MediaRepository;
import ru.itis.repositories.MediaTypeRepository;
import ru.itis.utils.Parse;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminMediaServiceImpl implements AdminMediaService {

    private final MediaRepository mediaRepository;
    private final MediaTypeRepository mediaTypeRepository;
    private final GenresRepository genresRepository;
    private final MediaCardValidator validator;
    private final MediaGenreRepository mediaGenreRepository;

    public AdminMediaServiceImpl(MediaRepository mediaRepository, MediaTypeRepository mediaTypeRepository,
                                 GenresRepository genresRepository, MediaCardValidator validator,
                                 MediaGenreRepository mediaGenreRepository) {
        this.mediaRepository = mediaRepository;
        this.mediaTypeRepository = mediaTypeRepository;
        this.genresRepository = genresRepository;
        this.validator = validator;
        this.mediaGenreRepository = mediaGenreRepository;
    }

    @Override
    public List<AdminMediaItem> search(String query, Long typeId) throws SQLException {
        return mediaRepository.findPublicItems(Parse.blankToNull(query), typeId);
    }

    @Override
    public Optional<AdminMediaForm> loadForm(Long id) throws SQLException {
        return mediaRepository.findPublicCard(id).map(m -> {
            try {
                List<String> genreIds = mediaRepository.findGenreIds(id).stream()
                        .map(String::valueOf).collect(Collectors.toList());
                return AdminMediaForm.builder()
                        .id(String.valueOf(m.getId()))
                        .title(m.getTitle())
                        .typeId(String.valueOf(m.getTypeId()))
                        .releaseYear(str(m.getReleaseYear()))
                        .description(m.getDescription())
                        .pageCount(str(m.getPageCount()))
                        .seasonsCount(str(m.getSeasonsCount()))
                        .episodesCount(str(m.getEpisodesCount()))
                        .runtime(str(m.getRuntime()))
                        .genreIds(genreIds)
                        .build();
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    @Override
    public void save(AdminMediaForm form, String newCoverUrl, Long adminId) throws SQLException {
        Map<String, String> errors = new LinkedHashMap<>();
        Media media = validator.validate(form, errors);
        List<Long> genreIds = validator.validateGenres(form, errors);

        Long id = Parse.readLong(form.getId());
        Media existing = null;

        if (Parse.blankToNull(form.getId()) != null) {
            existing = id == null ? null : mediaRepository.findPublicCard(id).orElse(null);
            if (existing == null) {
                errors.put("form", "Карточка не найдена");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        if (existing == null) {
            media.setCoverUrl(newCoverUrl);
            media.setAuthorId(adminId);
            mediaRepository.createPublic(media, genreIds);
        } else {
            media.setId(id);
            media.setCoverUrl(newCoverUrl != null ? newCoverUrl : existing.getCoverUrl());

            updateMediaWithGenres(media, genreIds);
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        if (!mediaRepository.deletePublicIfUnused(id)) {
            throw new ValidationException(Map.of("form",
                    "Нельзя удалить: произведение есть в чьей-то библиотеке (или уже удалено)"));
        }
    }

    public void updateMediaWithGenres(Media media, List<Long> genreIds) throws SQLException {
        mediaRepository.update(media); // Обновляем только таблицу media
        mediaGenreRepository.deleteByMediaId(media.getId()); // Удаляем старые жанры
        for (Long genreId : genreIds) {
            mediaGenreRepository.save(MediaGenre.builder()
                    .mediaId(media.getId())
                    .genreId(genreId)
                    .build());
        }
    }

    @Override
    public List<MediaType> types() throws SQLException {
        return mediaTypeRepository.findAll();
    }

    @Override
    public List<Genre> genres() throws SQLException {
        return genresRepository.findAll();
    }

    private static String str(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}