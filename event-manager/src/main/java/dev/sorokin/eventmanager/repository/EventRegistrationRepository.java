package dev.sorokin.eventmanager.repository;

import dev.sorokin.eventmanager.entity.EventRegistrationEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistrationEntity, Long> {

    Optional<EventRegistrationEntity> findByEventIdAndUserId(Long eventId, Long userId);

    @EntityGraph(attributePaths = {"event", "event.location", "event.user"})
    List<EventRegistrationEntity> findAllByUserLogin(String userLogin);

}
