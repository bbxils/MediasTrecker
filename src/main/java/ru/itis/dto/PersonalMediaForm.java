package ru.itis.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalMediaForm {
    private String title;
    private String coverUrl;
    private String typeId;
    private List<String> genreIds;
    private String releaseYear;
    private String description;
    private String pageCount;
    private String seasonsCount;
    private String episodesCount;
    private String runtime;
    private String status;
    private String rating;
    private String note;
    private String seasonsWatched;
    private String episodesWatched;
    private String pagesRead;
    private String chaptersDone;
    private boolean suggestToCatalog;
}