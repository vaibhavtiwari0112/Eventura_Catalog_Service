package com.eventura.catalog.domain;


import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;


import lombok.*;


@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String title;

    private Integer durationMinutes;

    @Column(columnDefinition = "text")
    private String genres; // comma-separated

    @Column(columnDefinition = "text")
    private String description;

    private String posterUrl;

    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private OffsetDateTime createdAt;
}