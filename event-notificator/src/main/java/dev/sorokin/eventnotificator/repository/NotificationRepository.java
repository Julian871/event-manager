package dev.sorokin.eventnotificator.repository;

import dev.sorokin.eventnotificator.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query("""
SELECT n FROM NotificationEntity n
JOIN FETCH n.payload p
WHERE n.userId = :userId AND n.isRead = false
ORDER BY n.createdAt DESC
""")
    List<NotificationEntity> findUnreadByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("""
UPDATE NotificationEntity n SET n.isRead = true, n.readAt = :readAt
WHERE n.id IN :notificationIds AND n.userId = :userId AND n.isRead = false
""")
    int markAsRead(@Param("notificationIds") List<Long> notificationIds,
                   @Param("userId") Long userId,
                   @Param("readAt") LocalDateTime readAt);

    @Modifying
    @Transactional
    @Query("DELETE FROM NotificationEntity n WHERE n.createdAt < :cutoffDate")
    int deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Modifying
    @Transactional
    @Query("DELETE FROM NotificationEventPayloadEntity p WHERE NOT EXISTS " +
            "(SELECT 1 FROM NotificationEntity n WHERE n.payload.id = p.id)")
    int deleteOrphanPayloads();
}

