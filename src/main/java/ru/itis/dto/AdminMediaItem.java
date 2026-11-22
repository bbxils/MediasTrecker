package ru.itis.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminMediaItem {
    private Long id;
    private String title;
    private String typeName;
    private Integer releaseYear;
    private int usersCount;
    private String genres;
    private String coverUrl;
    private String authorName;
    private String description;
}
