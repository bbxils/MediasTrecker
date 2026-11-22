package ru.itis.services;

import ru.itis.dto.LibraryItem;
import ru.itis.dto.PersonalMediaForm;
import ru.itis.exceptions.ValidationException;
import ru.itis.models.*;
import ru.itis.repositories.MediaGenreRepository;
import ru.itis.repositories.MediaRepository;
import ru.itis.repositories.MediaTypeRepository;
import ru.itis.repositories.UserMediaRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;

public class MediaServiceImpl implements MediaService {

    private static final int MAX_YEAR = Year.now().getValue() + 10;
    private static final int MAX_COUNT = 100_000;

    private final MediaRepository mediaRepository;
    private final UserMediaRepository userMediaRepository;
    private final MediaGenreRepository mediaGenreRepository;
    private final MediaTypeRepository mediaTypeRepository;

    public MediaServiceImpl(MediaRepository mediaRepository,
                            UserMediaRepository userMediaRepository,
                            MediaGenreRepository mediaGenreRepository,
                            MediaTypeRepository mediaTypeRepository) {
        this.mediaRepository = mediaRepository;
        this.userMediaRepository = userMediaRepository;
        this.mediaGenreRepository = mediaGenreRepository;
        this.mediaTypeRepository = mediaTypeRepository;
    }

    @Override
    public LibraryItem addPersonalMedia(PersonalMediaForm form, Long userId) throws SQLException {

        Map<String, String> errors = new LinkedHashMap<>();

        String title = blankToNull(form.getTitle());
        if (title == null) {
            errors.put("title", "Укажите название");
        } else if (title.length() > 100) {
            errors.put("title", "Не длиннее 100 символов");
        }

        String visibility = form.isSuggestToCatalog() ? "pending" : "private";
        String description = blankToNull(form.getDescription());
        String note = blankToNull(form.getNote());

        Long typeId = readLong(form.getTypeId());
        MediaType type = typeId == null ? null : mediaTypeRepository.findById(typeId).orElse(null);
        if (type == null) {
            errors.put("type", "Выберите тип");
        }

        List<String> allowedStatuses = List.of("planned", "in_progress", "completed", "dropped");
        String status = form.getStatus();
        if (status == null || !allowedStatuses.contains(status)) {
            errors.put("status", "Выберите статус");
        }

        Integer releaseYear = null;
        if (blankToNull(form.getReleaseYear()) == null) {
            errors.put("releaseYear", "Укажите год выпуска");
        } else {
            releaseYear = readInt(form.getReleaseYear(), "releaseYear", 1455, MAX_YEAR, errors);
        }

        Integer rating = readInt(form.getRating(), "rating", 1, 10, errors);

        Integer pageCount = readInt(form.getPageCount(), "pageCount", 0, MAX_COUNT, errors);
        Integer seasonsCount = readInt(form.getSeasonsCount(), "seasonsCount", 1, MAX_COUNT, errors);
        Integer episodesCount = readInt(form.getEpisodesCount(), "episodesCount", 1, MAX_COUNT, errors);
        Integer runtime = readInt(form.getRuntime(), "runtime", 1, MAX_COUNT, errors);
        Integer seasonsWatched = readInt(form.getSeasonsWatched(), "seasonsWatched", 0, MAX_COUNT, errors);
        Integer episodesWatched = readInt(form.getEpisodesWatched(), "episodesWatched", 0, MAX_COUNT, errors);
        Integer pagesRead = readInt(form.getPagesRead(), "pagesRead", 0, MAX_COUNT, errors);
        Integer chaptersDone = readInt(form.getChaptersDone(), "chaptersDone", 0, MAX_COUNT, errors);

        List<Long> genreIds = new ArrayList<>();
        if (form.getGenreIds() != null) {
            for (String genreId : form.getGenreIds()) {
                Long id = readLong(genreId);
                if (id == null) {
                    errors.put("genre", "Некорректный жанр");
                } else {
                    genreIds.add(id);
                }
            }
        }

        if (genreIds.isEmpty()) {
            errors.put("genre", "Выберите хотя бы один жанр");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        Media media = Media.builder()
                .title(title)
                .visibility(visibility)
                .authorId(userId)
                .coverUrl(form.getCoverUrl())
                .description(description)
                .typeId(typeId)
                .releaseYear(releaseYear)
                .seasonsCount(seasonsCount)
                .episodesCount(episodesCount)
                .pageCount(pageCount)
                .runtime(runtime)
                .build();
        Media savedMedia = mediaRepository.save(media);

        LocalDateTime completedAt = "completed".equals(status) ? LocalDateTime.now() : null;

        UserMedia userMedia = UserMedia.builder()
                .userId(userId)
                .mediaId(savedMedia.getId())
                .status(status)
                .rating(rating)
                .note(note)
                .seasonsWatched(seasonsWatched)
                .episodesWatched(episodesWatched)
                .pagesRead(pagesRead)
                .chaptersDone(chaptersDone)
                .completedAt(completedAt)
                .build();

        UserMedia savedUserMedia = userMediaRepository.save(userMedia);

        for (Long genreId : genreIds) {
            MediaGenre mediaGenre = MediaGenre.builder()
                    .genreId(genreId)
                    .mediaId(savedMedia.getId())
                    .build();
            mediaGenreRepository.save(mediaGenre);
        }
        return userMediaRepository.findItem(savedUserMedia.getId(), userId)
                .orElseThrow(() -> new IllegalStateException("Сохранённая запись не найдена"));
    }

    @Override
    public List<LibraryItem> getLibrary(Long userId) throws SQLException {
        return userMediaRepository.findItemsByUser(userId);
    }

    @Override
    public Optional<LibraryItem> getItem(Long id, Long userId) throws SQLException {
        return userMediaRepository.findItem(id, userId);
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private static Integer readInt(String s, String field, int min, int max, Map<String, String> errors) {
        String v = blankToNull(s);
        if (v == null) return null;
        try {
            int n = Integer.parseInt(v);
            if (n < min || n > max) {
                errors.put(field, "Допустимо от " + min + " до " + max);
                return null;
            }
            return n;
        } catch (NumberFormatException e) {
            errors.put(field, "Нужно целое число");
            return null;
        }
    }

    private static Long readLong(String s) {
        String v = blankToNull(s);
        if (v == null) return null;
        try {
            return Long.valueOf(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
