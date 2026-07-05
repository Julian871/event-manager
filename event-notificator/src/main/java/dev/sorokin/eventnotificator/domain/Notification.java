package dev.sorokin.eventnotificator.domain;

import dev.sorokin.kafka.ChangeItem;
import dev.sorokin.kafka.EventType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Notification(
        Long id,
        Long userId,
        Long payloadId,
        UUID messageId,
        EventType eventType,
        Long eventId,
        String eventName,
        LocalDateTime occurredAt,
        Long changedById,
        Long ownerId,
        List<ChangeItem> changes,
        LocalDateTime createdAt,
        Boolean isRead,
        LocalDateTime readAt
) {}