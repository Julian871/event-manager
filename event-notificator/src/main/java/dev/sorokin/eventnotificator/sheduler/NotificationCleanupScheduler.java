package dev.sorokin.eventnotificator.sheduler;

import dev.sorokin.eventnotificator.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationCleanupScheduler {

    private final NotificationRepository notificationRepository;

    @Scheduled(cron = "${notification.cleanup.cron}")
    public void cleanupOldNotifications() {
        log.info("Cleanup of old notifications started");

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);

        int deletedNotifications = notificationRepository.deleteOldNotifications(cutoffDate);
        log.info("Deleted {} old notifications", deletedNotifications);

        int deletedPayloads = notificationRepository.deleteOrphanPayloads();
        log.info("Deleted {} payloads", deletedPayloads);

        log.info("Cleanup of old notifications finished");
    }
}
