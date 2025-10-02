package com.eventura.catalog.controller;


import com.eventura.catalog.dto.CreateHallRequest;
import com.eventura.catalog.dto.HallDto;
import com.eventura.catalog.dto.SeatStatusRequest;
import com.eventura.catalog.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/catalog/halls")
public class HallController {

    private final CatalogService catalogService;

    public HallController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    // Single hall creation
    @PostMapping
    public ResponseEntity<HallDto> createHall(@Valid @RequestBody CreateHallRequest req) {
        HallDto dto = catalogService.createHall(req);
        return ResponseEntity.created(URI.create("/catalog/halls/" + dto.getId())).body(dto);
    }

    @GetMapping("/{hallId}/seats")
    public ResponseEntity<List<String>> getSeatsForHall(@PathVariable UUID hallId) {
        return ResponseEntity.ok(catalogService.getAllSeatIdsForHall(hallId));
    }

    @GetMapping("/{hallId}")
    public ResponseEntity<HallDto> getHallById(@PathVariable UUID hallId) {
        return ResponseEntity.ok(catalogService.getHallById(hallId));
    }

    @GetMapping("/theater/{theaterId}")
    public ResponseEntity<List<HallDto>> listHallsByTheater(@PathVariable UUID theaterId) {
        return ResponseEntity.ok(catalogService.listHallsByTheater(theaterId));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<HallDto>> createHallsBatch(@Valid @RequestBody List<CreateHallRequest> requests) {
        List<HallDto> dtos = catalogService.createHallsBatch(requests);
        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/{hallId}/seats/{seatId}/status")
    public ResponseEntity<HallDto> updateSeatStatus(
            @PathVariable UUID hallId,
            @PathVariable String seatId,
            @RequestBody SeatStatusRequest request
    ) {
        HallDto dto = catalogService.updateSeatStatus(hallId, seatId, request.getStatus());
        return ResponseEntity.ok(dto);
    }
}
