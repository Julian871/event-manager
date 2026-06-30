package dev.sorokin.eventmanager.repository;

import dev.sorokin.eventmanager.entity.LocationEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    @Override
    @EntityGraph(attributePaths = {"events"})
    Optional<LocationEntity> findById(@NonNull Long id);
}
