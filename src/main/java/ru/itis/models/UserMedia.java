package ru.itis.models;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMedia {
    private Long id;
    private Long userId;
    private Long mediaId;
    private String status;
    private Integer rating;
    private String note;
    private Integer seasonsWatched;
    private Integer episodesWatched;
    private Integer pagesRead;
    private Integer chaptersDone;
    private LocalDateTime completedAt;
}
