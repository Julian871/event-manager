package dev.sorokin.eventnotificator.entity;

import dev.sorokin.kafka.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_event_payloads")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventPayloadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false, unique = true)
    private UUID messageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "changed_by_id")
    private Long changedById;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_json", nullable = false, columnDefinition = "jsonb")
    private String payloadJson;
}
