package com.eventura.catalog.dto;


import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateHallRequest {
    @NotNull
    private UUID theaterId;

    @NotBlank
    private String name;

    @NotNull
    private JsonNode seatLayout; // JSON describing rows/seats

    private Integer capacity;
}