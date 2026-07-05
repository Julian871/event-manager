package dev.sorokin.eventnotificator.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sorokin.eventnotificator.domain.Notification;
import dev.sorokin.eventnotificator.dto.NotificationPayload;
import dev.sorokin.eventnotificator.dto.request.MarkNotificationsAsReadRequest;
import dev.sorokin.eventnotificator.dto.response.NotificationResponse;
import dev.sorokin.eventnotificator.entity.NotificationEntity;
import dev.sorokin.eventnotificator.entity.NotificationEventPayloadEntity;
import dev.sorokin.kafka.ChangeItem;
import dev.sorokin.kafka.EventChangeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationMapper {

    private final ObjectMapper objectMapper;

    private record ParsedPayload(String eventName, List<ChangeItem> changes) {}

    public Notification toDomain(NotificationEntity entity) {
        NotificationEventPayloadEntity payload = entity.getPayload();
        ParsedPayload parsed = parsePayload(payload);

        return new Notification(
                entity.getId(),
                entity.getUserId(),
                payload.getId(),
                payload.getMessageId(),
                payload.getEventType(),
                payload.getEventId(),
                parsed.eventName,
                payload.getOccurredAt(),
                payload.getChangedById(),
                payload.getOwnerId(),
                parsed.changes,
                entity.getCreatedAt(),
                entity.isRead(),
                entity.getReadAt()
        );
    }

    public List<Notification> toDomainList(List<NotificationEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.id(),
                notification.eventType(),
                notification.eventId(),
                notification.createdAt(),
                notification.isRead(),
                buildMessage(notification),
                toNotificationPayload(notification)
        );
    }

    public List<NotificationResponse> toResponseList(List<Notification> notifications) {
        return notifications.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<Long> toNotificationIdList(MarkNotificationsAsReadRequest request) {
        if (request == null || request.getNotificationIds() == null) {
            return Collections.emptyList();
        }
        return request.getNotificationIds();
    }

    public NotificationEventPayloadEntity toPayloadEntity(EventChangeMessage message) {
        String payloadJson = buildPayloadJson(message);

        return NotificationEventPayloadEntity.builder()
                .messageId(UUID.fromString(message.getMessageId()))
                .eventType(message.getEventType())
                .eventId(message.getEventId())
                .occurredAt(message.getOccurredAt())
                .ownerId(message.getOwnerId())
                .changedById(message.getChangedById())
                .payloadJson(payloadJson)
                .build();
    }

    public NotificationEntity toNotificationEntity(Long userId, NotificationEventPayloadEntity payloadEntity) {
        return NotificationEntity.builder()
                .userId(userId)
                .payload(payloadEntity)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private String buildPayloadJson(EventChangeMessage message) {
        try {
            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("eventName", message.getEventName());
            payloadMap.put("changedById", message.getChangedById());
            payloadMap.put("changes", message.getChanges());

            return objectMapper.writeValueAsString(payloadMap);
        } catch (JsonProcessingException e) {
            log.error("Error building payload JSON", e);
            return "{}";
        }
    }

    private ParsedPayload parsePayload(NotificationEventPayloadEntity payload) {
        try {
            Map<String, Object> map = objectMapper.readValue(payload.getPayloadJson(), Map.class);

            String eventName = (String) map.get("eventName");
            List<ChangeItem> changes = objectMapper.convertValue(
                    map.get("changes"),
                    new TypeReference<>() {
                    }
            );

            return new ParsedPayload(eventName, changes);
        } catch (Exception e) {
            log.error("Error parsing payload", e);
            return new ParsedPayload(null, Collections.emptyList());
        }
    }

    private NotificationPayload toNotificationPayload(Notification notification) {
        return new NotificationPayload(
                notification.messageId(),
                notification.eventType(),
                notification.occurredAt(),
                notification.changedById(),
                notification.ownerId(),
                notification.eventName(),
                notification.changes()
        );
    }

    private String buildMessage(Notification notification) {
        if (notification.changes() == null || notification.changes().isEmpty()) {
            return "Event changed";
        }

        List<String> changedFields = notification.changes().stream()
                .map(ChangeItem::getField)
                .collect(Collectors.toList());

        return String.format("Event changed: %s", String.join(", ", changedFields));
    }
}