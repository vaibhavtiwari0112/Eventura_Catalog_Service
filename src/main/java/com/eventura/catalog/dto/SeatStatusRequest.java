package com.eventura.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SeatStatusRequest {
    private String status; // e.g. "BOOKED", "AVAILABLE"
}

