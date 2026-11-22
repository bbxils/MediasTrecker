package ru.itis.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminMediaForm {
    private String id;
    private String title;
    private String typeId;
    private String releaseYear;
    private String description;
    private String pageCount;
    private String seasonsCount;
    private String episodesCount;
    private String runtime;
    private List<String> genreIds;
}
