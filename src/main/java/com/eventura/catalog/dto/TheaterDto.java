package com.eventura.catalog.dto;


import lombok.*;


import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheaterDto {
    private UUID id;
    private String name;
    private String city;
    private String address;
}