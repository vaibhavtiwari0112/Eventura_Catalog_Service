package com.eventura.catalog.repository;


import com.eventura.catalog.domain.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;


@Repository
public interface TheaterRepository extends JpaRepository<Theater, UUID> {
    Optional<Theater> findById(UUID id);

    boolean existsByNameAndCity(String name, String city);
}