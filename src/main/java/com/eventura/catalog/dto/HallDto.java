package com.eventura.catalog.dto;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;


import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HallDto {
    private UUID id;
    private UUID theaterId;
    private String name;
    private JsonNode seatLayout; // JSON
    private Integer capacity;
}