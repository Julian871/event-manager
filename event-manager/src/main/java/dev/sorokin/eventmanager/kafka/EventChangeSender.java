package dev.sorokin.eventmanager.kafka;

import dev.sorokin.kafka.EventChangeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventChangeSender {

    private final KafkaTemplate<String, EventChangeMessage> kafkaTemplate;
    private static final String TOPIC = "event-changes";

    public void sendEventChanges(EventChangeMessage message) {
        log.info("Sending event change: eventId={}, type={}",
                message.getEventId(), message.getEventType());

        var result = kafkaTemplate.send(
                TOPIC,
                message.getMessageId(),
                message
        );

        result.thenAccept(
                sendResult -> log.info("Send successful"));
    }
}
