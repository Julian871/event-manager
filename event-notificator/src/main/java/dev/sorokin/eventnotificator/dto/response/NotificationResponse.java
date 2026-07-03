package dev.sorokin.eventnotificator.dto.response;

import dev.sorokin.eventnotificator.dto.NotificationPayload;
import dev.sorokin.kafka.EventType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long notificationId,
        EventType type,
        Long eventId,
        LocalDateTime createdAt,
        Boolean isRead,
        String message,
        NotificationPayload payload
) { }
