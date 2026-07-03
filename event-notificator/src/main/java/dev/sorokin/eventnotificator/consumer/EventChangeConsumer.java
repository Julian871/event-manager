package dev.sorokin.eventnotificator.consumer;

import dev.sorokin.eventnotificator.service.NotificationService;
import dev.sorokin.kafka.EventChangeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventChangeConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "event-changes",
            groupId = "event-notificator-group"
    )
    public void consume(EventChangeMessage message) {
        log.info("Received message from Kafka: ");
        log.info("eventId: {}", message.getEventId());
        log.info("eventType: {}", message.getEventType());

        try {
            notificationService.processEventChange(message);
            log.info("Message processed and saved to database");
        } catch (Exception e) {
            log.error("Failed to process message", e);
        }
    }
}
