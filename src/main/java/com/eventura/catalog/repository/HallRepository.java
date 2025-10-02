package com.eventura.catalog.repository;


import com.eventura.catalog.domain.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface HallRepository extends JpaRepository<Hall, UUID> {
    List<Hall> findByTheaterId(UUID theaterId);
    Optional<Hall> findById(UUID id);
    boolean existsByTheaterIdAndName(UUID theaterId, String name);
}