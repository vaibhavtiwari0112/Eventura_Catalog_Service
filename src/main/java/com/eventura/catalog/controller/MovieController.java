package com.eventura.catalog.controller;


import com.eventura.catalog.dto.CreateMovieRequest;
import com.eventura.catalog.dto.MovieDto;
import com.eventura.catalog.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/catalog/movies")
public class MovieController {

    private final CatalogService catalogService;

    public MovieController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    // Single movie creation
    @PostMapping
    public ResponseEntity<MovieDto> createMovie(@Valid @RequestBody CreateMovieRequest req) {
        MovieDto dto = catalogService.createMovie(req);
        return ResponseEntity.created(URI.create("/catalog/movies/" + dto.getId())).body(dto);
    }

    // Batch movie creation
    @PostMapping("/batch")
    public ResponseEntity<List<MovieDto>> createMoviesBatch(@Valid @RequestBody List<CreateMovieRequest> requests) {
        List<MovieDto> dtos = catalogService.createMoviesBatch(requests);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public ResponseEntity<List<MovieDto>> listMovies() {
        return ResponseEntity.ok(catalogService.listMovies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovie(@PathVariable UUID id) {
        return catalogService.getMovieById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
