package com.eventura.catalog.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.UUID;

@Entity
@Table(name = "halls")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Hall {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Column(nullable = false)
    private String name;

    @Type(JsonType.class)
    @Column(name = "seat_layout", columnDefinition = "jsonb", nullable = false)
    private JsonNode seatLayout;

    private Integer capacity;
}
