package ru.itis.models;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaGenre {
    private Long mediaId;
    private Long genreId;
}
