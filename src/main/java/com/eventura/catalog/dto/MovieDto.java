package com.eventura.catalog.dto;


import lombok.*;


import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDto {
    private UUID id;
    private String title;
    private Integer durationMinutes;
    private List<String> genres;
    private String description;
    private String posterUrl;
    private boolean active;
    private Double rating; // ✅ Add this
    private OffsetDateTime createdAt;
}