package dev.sorokin.eventnotificator.dto;


import dev.sorokin.kafka.ChangeItem;
import dev.sorokin.kafka.EventType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record NotificationPayload(
        UUID messageId,
        EventType eventType,
        LocalDateTime occurredAt,
        Long changeById,
        Long ownerId,
        String eventName,
        List<ChangeItem> changes
) {}
