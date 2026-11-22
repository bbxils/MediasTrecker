package ru.itis.services;

import ru.itis.dto.AdminMediaForm;
import ru.itis.models.Genre;
import ru.itis.models.Media;
import ru.itis.repositories.GenresRepository;
import ru.itis.repositories.MediaTypeRepository;
import ru.itis.utils.Parse;

import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MediaCardValidator {

    private static final int MAX_YEAR = Year.now().getValue() + 10;
    private static final int MAX_COUNT = 100_000;
    private static final int MAX_TEXT = 2000;

    private final MediaTypeRepository mediaTypeRepository;
    private final GenresRepository genresRepository;

    public MediaCardValidator(MediaTypeRepository mediaTypeRepository, GenresRepository genresRepository) {
        this.mediaTypeRepository = mediaTypeRepository;
        this.genresRepository = genresRepository;
    }

    public Media validate(AdminMediaForm form, Map<String, String> errors) throws SQLException {
        String title = Parse.blankToNull(form.getTitle());
        if (title == null) {
            errors.put("title", "Укажите название");
        } else if (title.length() > 100) {
            errors.put("title", "Не длиннее 100 символов");
        }

        String description = Parse.blankToNull(form.getDescription());
        if (description != null && description.length() > MAX_TEXT) {
            errors.put("description", "Не длиннее " + MAX_TEXT + " символов");
        }

        Long typeId = Parse.readLong(form.getTypeId());
        if (typeId == null || mediaTypeRepository.findById(typeId).isEmpty()) {
            errors.put("type", "Выберите тип");
        }

        Integer releaseYear = Parse.readInt(form.getReleaseYear(), "releaseYear", 1455, MAX_YEAR, errors);
        if (releaseYear == null && !errors.containsKey("releaseYear")) {
            errors.put("releaseYear", "Укажите год выпуска");
        }

        return Media.builder()
                .title(title)
                .typeId(typeId)
                .releaseYear(releaseYear)
                .description(description)
                .pageCount(Parse.readInt(form.getPageCount(), "pageCount", 1, MAX_COUNT, errors))
                .seasonsCount(Parse.readInt(form.getSeasonsCount(), "seasonsCount", 1, MAX_COUNT, errors))
                .episodesCount(Parse.readInt(form.getEpisodesCount(), "episodesCount", 1, MAX_COUNT, errors))
                .runtime(Parse.readInt(form.getRuntime(), "runtime", 1, MAX_COUNT, errors))
                .visibility("public")
                .build();
    }

    public List<Long> validateGenres(AdminMediaForm form, Map<String, String> errors) throws SQLException {
        List<Long> result = new ArrayList<>();
        if (form.getGenreIds() == null) return result;

        Set<Long> existing = genresRepository.findAll().stream()
                .map(Genre::getId).collect(Collectors.toSet());
        for (String raw : form.getGenreIds()) {
            Long id = Parse.readLong(raw);
            if (id == null || !existing.contains(id)) {
                errors.put("genre", "Некорректный жанр");
            } else {
                result.add(id);
            }
        }
        return result;
    }
}