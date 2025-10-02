package com.eventura.catalog.repository;


import com.eventura.catalog.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {
    @Override
    Optional<Movie> findById(UUID uuid);

    List<Movie> findByTitle(String title);

    boolean existsByTitle(String title);
}