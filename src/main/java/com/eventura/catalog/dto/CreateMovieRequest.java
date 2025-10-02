package com.eventura.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CreateMovieRequest {
    @NotBlank
    private String title;

    private Integer durationMinutes;

    @NotNull
    private List<String> genres; // ✅ List instead of String

    private String description;
    private String posterUrl;

    // Getters & setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
}
