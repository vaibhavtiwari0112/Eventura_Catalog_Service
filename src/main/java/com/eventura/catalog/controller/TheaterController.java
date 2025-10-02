package com.eventura.catalog.controller;


import com.eventura.catalog.dto.CreateTheaterRequest;
import com.eventura.catalog.dto.TheaterDto;
import com.eventura.catalog.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/catalog/theatres")
public class TheaterController {

    private final CatalogService catalogService;

    public TheaterController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    // Single theater creation
    @PostMapping
    public ResponseEntity<TheaterDto> createTheater(@Valid @RequestBody CreateTheaterRequest req) {
        TheaterDto dto = catalogService.createTheater(req);
        return ResponseEntity.created(URI.create("/catalog/theaters/" + dto.getId())).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<TheaterDto>> listTheaters() {
        return ResponseEntity.ok(catalogService.listTheaters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheaterDto> getTheater(@PathVariable UUID id) {
        return catalogService.getTheaterById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/batch")
    public ResponseEntity<List<TheaterDto>> createTheatersBatch(@Valid @RequestBody List<CreateTheaterRequest> requests) {
        List<TheaterDto> dtos = catalogService.createTheatersBatch(requests);
        return ResponseEntity.ok(dtos);
    }
}
