package dev.sorokin.eventmanager.repository;

import dev.sorokin.eventmanager.entity.EventRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRegistrationRepository extends JpaRepository<EventRegistrationEntity, Long> {
}
