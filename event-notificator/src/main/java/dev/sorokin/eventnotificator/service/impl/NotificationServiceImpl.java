package dev.sorokin.eventnotificator.service.impl;

import dev.sorokin.eventnotificator.domain.Notification;
import dev.sorokin.eventnotificator.entity.NotificationEntity;
import dev.sorokin.eventnotificator.entity.NotificationEventPayloadEntity;
import dev.sorokin.eventnotificator.mapper.NotificationMapper;
import dev.sorokin.eventnotificator.repository.NotificationEventPayloadRepository;
import dev.sorokin.eventnotificator.repository.NotificationRepository;
import dev.sorokin.eventnotificator.service.NotificationCacheService;
import dev.sorokin.eventnotificator.service.NotificationService;
import dev.sorokin.eventnotificator.util.SecurityUtil;
import dev.sorokin.kafka.EventChangeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationEventPayloadRepository payloadRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final SecurityUtil securityUtil;
    private final NotificationCacheService cacheService;

    @Override
    @Transactional
    public void processEventChange(EventChangeMessage message) {

        UUID messageId = UUID.fromString(message.getMessageId());
        if (payloadRepository.existsByMessageId(messageId)) {
            log.warn("Message already processed: {}", messageId);
            return;
        }

        NotificationEventPayloadEntity payloadEntity = notificationMapper.toPayloadEntity(message);
        payloadRepository.save(payloadEntity);

        List<NotificationEntity> notifications = message.getSubscribers()
                .stream()
                .map(subscriberId -> {
                    NotificationEntity notification = notificationMapper.toNotificationEntity(subscriberId, payloadEntity);

                    cacheService.incrementUnreadCounter(notification.getUserId());

                    return notification;
                })
                .toList();

        notificationRepository.saveAll(notifications);
        log.info("Created {} notifications for eventId={}", notifications.size(), message.getEventId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications() {
        Long userId = securityUtil.getCurrentUserId();

        log.info("Current user ID: {}", userId);
        List<NotificationEntity> notifications = notificationRepository.findUnreadByUserId(userId);

        return notificationMapper.toDomainList(notifications);
    }

    @Override
    @Transactional
    public void markAsRead(List<Long> notificationIds) {
        Long userId = securityUtil.getCurrentUserId();

        int updatedCount = notificationRepository.markAsRead(notificationIds, userId, LocalDateTime.now());
        log.info("Marked {} notifications as read for userId={}", updatedCount, userId);

        Long count = notificationRepository.countByUserIdAndIsReadyFalse(userId);
        cacheService.saveCountUnreadNotification(userId, count);
    }
}
