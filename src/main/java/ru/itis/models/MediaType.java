package ru.itis.models;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaType {
    private Long id;
    private String name;
}
