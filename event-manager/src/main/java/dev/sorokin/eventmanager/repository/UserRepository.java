package dev.sorokin.eventmanager.repository;

import dev.sorokin.eventmanager.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserAccountEntity, Long> {
    boolean existsByLogin(String login);

    Optional<UserAccountEntity> findByLogin(String login);

    @EntityGraph(attributePaths = {"events", "events.location"})
    Optional<UserAccountEntity> findUserWithEventsByLogin(String login);
}
