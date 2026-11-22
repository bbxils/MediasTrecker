package ru.itis.models;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Media {
    private Long id;
    private String title;
    private Long typeId;
    private String typeName;
    private String coverUrl;
    private Integer releaseYear;
    private String description;
    private Integer pageCount;
    private Integer seasonsCount;
    private Integer episodesCount;
    private Integer runtime;
    private String visibility;
    private Long authorId;
    private Long reviewedBy;
    private LocalDateTime reviewedAt;
}
