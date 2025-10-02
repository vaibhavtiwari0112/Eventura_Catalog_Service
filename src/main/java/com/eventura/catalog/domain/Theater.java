package com.eventura.catalog.domain;


import jakarta.persistence.*;
import java.util.UUID;


import lombok.*;


@Entity
@Table(name = "theaters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theater {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String city;

    @Column(columnDefinition = "text")
    private String address;
}