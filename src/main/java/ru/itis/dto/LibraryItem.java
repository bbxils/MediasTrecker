package ru.itis.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryItem {
    private Long id;
    private Long mediaId;
    private String title;
    private String coverUrl;
    private String typeName;
    private Integer releaseYear;
    private String description;
    private String genres;
    private String status;
    private Integer rating;
    private String visibility;
    private String note;
    private Integer seasonsWatched;
    private Integer episodesWatched;
    private Integer pagesRead;
    private Integer chaptersDone;
    private Integer pageCount;
    private Integer seasonsCount;
    private Integer episodesCount;
    private Integer runtime;
}