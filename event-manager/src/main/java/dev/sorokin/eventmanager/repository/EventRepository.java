package dev.sorokin.eventmanager.repository;

import dev.sorokin.eventmanager.entity.EventEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @EntityGraph(attributePaths = {"user", "location"})
    Optional<EventEntity> findById(@NonNull Long id);

    @Query(value = """
            SELECT * FROM events e
            WHERE (:name IS NULL OR e.name ILIKE CONCAT('%', :name, '%'))
            AND (:minPlaces IS NULL OR e.max_places >= :minPlaces)
            AND (:maxPlaces IS NULL OR e.max_places <= :maxPlaces)
            AND (:dateStartAfter IS NULL OR e.start_at >= CAST(:dateStartAfter AS timestamp))
            AND (:dateStartBefore IS NULL OR e.start_at <= CAST(:dateStartBefore AS timestamp))
            AND (:costMin IS NULL OR e.cost >= :costMin)
            AND (:costMax IS NULL OR e.cost <= :costMax)
            AND (:durationMin IS NULL OR e.duration_minutes >= :durationMin)
            AND (:durationMax IS NULL OR e.duration_minutes <= :durationMax)
            AND (:locationId IS NULL OR e.location_id = :locationId)
            AND (:eventStatus IS NULL OR e.status = CAST(:eventStatus AS varchar))
            """, nativeQuery = true)
    List<EventEntity> searchEvents(
            @Param("name") String name,
            @Param("minPlaces") Integer minPlaces,
            @Param("maxPlaces") Integer maxPlaces,
            @Param("dateStartAfter") String dateStartAfter,
            @Param("dateStartBefore") String dateStartBefore,
            @Param("costMin") Integer costMin,
            @Param("costMax") Integer costMax,
            @Param("durationMin") Integer durationMin,
            @Param("durationMax") Integer durationMax,
            @Param("locationId") Long locationId,
            @Param("eventStatus") String eventStatus
    );
}
