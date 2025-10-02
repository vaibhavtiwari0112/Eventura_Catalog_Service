package com.eventura.catalog.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTheaterRequest {
    @NotBlank
    private String name;
    private String city;
    private String address;
}