package com.eventura.catalog.service;

import com.eventura.catalog.domain.Hall;
import com.eventura.catalog.domain.Movie;
import com.eventura.catalog.domain.Theater;
import com.eventura.catalog.dto.*;
import com.eventura.catalog.kafka.KafkaEventPublisher;
import com.eventura.catalog.repository.HallRepository;
import com.eventura.catalog.repository.MovieRepository;
import com.eventura.catalog.repository.TheaterRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CatalogService {

    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final HallRepository hallRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    public CatalogService(MovieRepository movieRepository,
                          TheaterRepository theaterRepository,
                          HallRepository hallRepository,
                          KafkaEventPublisher kafkaEventPublisher) {
        this.movieRepository = movieRepository;
        this.theaterRepository = theaterRepository;
        this.hallRepository = hallRepository;
        this.kafkaEventPublisher = kafkaEventPublisher;
    }

    // ------------------- Movies -------------------

    @Transactional
    public MovieDto createMovie(CreateMovieRequest req) {
        // Check for uniqueness first
        List<Movie> existing = movieRepository.findByTitle(req.getTitle());
        if (!existing.isEmpty()) {
            throw new IllegalArgumentException("Movie with this title already exists");
        }

        Movie m = new Movie();
        m.setTitle(req.getTitle());
        m.setDurationMinutes(req.getDurationMinutes());
        m.setGenres(req.getGenres() != null ? String.join(",", req.getGenres()) : null);
        m.setDescription(req.getDescription());
        m.setPosterUrl(req.getPosterUrl());
        m.setActive(true);
        m.setCreatedAt(OffsetDateTime.now()); // manually set timestamp

        Movie saved = movieRepository.save(m);

        // TODO : update when kafka added
//        kafkaEventPublisher.publish("movie.created",
//                Map.of("id", saved.getId().toString(), "title", saved.getTitle()));

        return toDto(saved);
    }


    @Transactional(readOnly = true)
    public List<MovieDto> listMovies() {
        return movieRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<MovieDto> getMovieById(UUID id) {
        return movieRepository.findById(id).map(this::toDto);
    }

    // ------------------- Batch Creation with skipped duplicates -------------------
    @Transactional
    public List<MovieDto> createMoviesBatch(List<CreateMovieRequest> requests) {
        List<MovieDto> savedMovies = new ArrayList<>();

        for (CreateMovieRequest req : requests) {
            // Skip duplicates
            if (movieRepository.existsByTitle(req.getTitle())) {
                continue;
            }
            MovieDto dto = createMovie(req); // use existing single creation method
            savedMovies.add(dto);
        }

        return savedMovies;
    }


    // ------------------- Theaters -------------------
    @Transactional
    public TheaterDto createTheater(CreateTheaterRequest req) {
        Theater theater = Theater.builder()
                .name(req.getName())
                .city(req.getCity())
                .address(req.getAddress())
                .build();

        Theater saved = theaterRepository.save(theater);


        // TODO : update when kafka added
//        kafkaEventPublisher.publish("theater.created",
//                Map.of("id", saved.getId().toString(), "name", saved.getName()));

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<TheaterDto> listTheaters() {
        return theaterRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<TheaterDto> getTheaterById(UUID id) {
        return theaterRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public List<TheaterDto> createTheatersBatch(List<CreateTheaterRequest> requests) {
        List<TheaterDto> savedTheaters = new ArrayList<>();

        for (CreateTheaterRequest req : requests) {
            // Skip duplicate theater by name + city
            if (theaterRepository.existsByNameAndCity(req.getName(), req.getCity())) {
                continue;
            }

            TheaterDto dto = createTheater(req); // existing single creation method
            savedTheaters.add(dto);
        }

        return savedTheaters;
    }

    // ------------------- Halls -------------------

    @Transactional
    public HallDto createHall(CreateHallRequest req) {
        Theater theater = theaterRepository.findById(req.getTheaterId())
                .orElseThrow(() -> new NoSuchElementException("Theater not found: " + req.getTheaterId()));

        ObjectNode seatLayout = (ObjectNode) req.getSeatLayout();

        // Ensure "seats" array exists
        ArrayNode seats;
        if (seatLayout.has("seats")) {
            seats = (ArrayNode) seatLayout.get("seats");
        } else {
            seats = seatLayout.putArray("seats");
        }

        // Collect existing seat IDs if any were provided manually
        Set<String> existingSeatIds = new HashSet<>();
        for (JsonNode seat : seats) {
            existingSeatIds.add(seat.get("id").asText());
            if (!seat.has("status")) {
                ((ObjectNode) seat).put("status", "AVAILABLE");
            }
        }

        // Auto-generate missing seats from rows × columns
        ArrayNode rows = (ArrayNode) seatLayout.get("rows");
        int columns = seatLayout.get("columns").asInt();

        for (JsonNode rowNode : rows) {
            String row = rowNode.asText();
            for (int col = 1; col <= columns; col++) {
                String seatId = row + col;
                if (!existingSeatIds.contains(seatId)) {
                    ObjectNode newSeat = seats.addObject();
                    newSeat.put("id", seatId);
                    newSeat.put("type", "REGULAR");
                    newSeat.put("status", "AVAILABLE");
                }
            }
        }

        Hall hall = Hall.builder()
                .theater(theater)
                .name(req.getName())
                .seatLayout(seatLayout)
                .capacity(req.getCapacity())
                .build();

        Hall saved = hallRepository.save(hall);

        // TODO : update when kafka added

//        kafkaEventPublisher.publish(
//                "hall.created",
//                Map.of(
//                        "id", saved.getId().toString(),
//                        "theaterId", theater.getId().toString(),
//                        "name", saved.getName(),
//                        "capacity", saved.getCapacity(),
//                        "seats", getAllSeatIdsForHall(saved.getId())
//                )
//        );

        return toDto(saved);
    }

    @Transactional
    public HallDto updateSeatStatus(UUID hallId, String seatId, String newStatus) {
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new NoSuchElementException("Hall not found: " + hallId));

        ArrayNode seats = (ArrayNode) hall.getSeatLayout().get("seats");
        for (JsonNode seat : seats) {
            if (seat.get("id").asText().equals(seatId)) {
                ((ObjectNode) seat).put("status", newStatus);
                break;
            }
        }

        hall.setSeatLayout(hall.getSeatLayout()); // re-assign JSON
        Hall saved = hallRepository.save(hall);

        // TODO : update when kafka added
//        kafkaEventPublisher.publish("seat.updated",
//                Map.of("hallId", saved.getId().toString(), "seatId", seatId, "status", newStatus));

        return toDto(saved);
    }


    @Transactional(readOnly = true)
    public HallDto getHallById(UUID hallId) {
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new NoSuchElementException("Hall not found: " + hallId));
        return toDto(hall);
    }

    @Transactional(readOnly = true)
    public List<HallDto> listHallsByTheater(UUID theaterId) {
        return hallRepository.findByTheaterId(theaterId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<HallDto> createHallsBatch(List<CreateHallRequest> requests) {
        List<HallDto> savedHalls = new ArrayList<>();

        for (CreateHallRequest req : requests) {
            if (hallRepository.existsByTheaterIdAndName(req.getTheaterId(), req.getName())) {
                continue;
            }

            HallDto dto = createHall(req); // existing single creation method
            savedHalls.add(dto);
        }

        return savedHalls;
    }

    // ------------------- Seat Availability -------------------
    /**
     * Returns all seat IDs of a hall as a flat list.
     * Used by ShowService/BookingService to check availability.
     */
    @Transactional(readOnly = true)
    public List<String> getAllSeatIdsForHall(UUID hallId) {
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new NoSuchElementException("Hall not found: " + hallId));

        JsonNode layout = hall.getSeatLayout();
        if (layout == null || !layout.has("seats")) return List.of();

        List<String> seatIds = new ArrayList<>();
        for (JsonNode seat : layout.get("seats")) {
            seatIds.add(seat.get("id").asText());
        }
        return seatIds;
    }

    // ------------------- DTO Mappers -------------------
    private MovieDto toDto(Movie m) {
        return MovieDto.builder()
                .id(m.getId())
                .title(m.getTitle())
                .durationMinutes(m.getDurationMinutes())
                .genres(m.getGenres() != null ? Arrays.asList(m.getGenres().split(",")) : List.of())
                .description(m.getDescription())
                .posterUrl(m.getPosterUrl())
                .active(m.isActive())
                .createdAt(m.getCreatedAt())
                .build();
    }

    private TheaterDto toDto(Theater t) {
        return TheaterDto.builder()
                .id(t.getId())
                .name(t.getName())
                .city(t.getCity())
                .address(t.getAddress())
                .build();
    }

    private HallDto toDto(Hall h) {
        return HallDto.builder()
                .id(h.getId())
                .theaterId(h.getTheater().getId())
                .name(h.getName())
                .seatLayout(h.getSeatLayout())
                .capacity(h.getCapacity())
                .build();
    }
}
