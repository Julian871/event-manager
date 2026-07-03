package dev.sorokin.eventmanager.sheduler;

import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.enums.EventStatus;
import dev.sorokin.eventmanager.kafka.EventChangeSender;
import dev.sorokin.eventmanager.repository.EventRegistrationRepository;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.kafka.ChangeItem;
import dev.sorokin.kafka.EventChangeMessage;
import dev.sorokin.kafka.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventStatusScheduler {

    private final EventRepository eventRepository;
    private final EventChangeSender eventChangeSender;
    private final EventRegistrationRepository eventRegistrationRepository;


    @Scheduled(cron = "${event.stats.cron}")
    public void updateEventsStatus() {
        log.info("EventStatusScheduler started");

        List<EventEntity> eventsToStart = eventRepository.findEventsToStart();
        for (EventEntity event : eventsToStart) {
            EventStatus oldStatus = event.getStatus();
            event.setStatus(EventStatus.STARTED);
            eventRepository.save(event);

            sendStatusChangeMessage(event, oldStatus);
        }

        List<EventEntity> eventsToFinish = eventRepository.findEventsToFinish();
        for (EventEntity event : eventsToFinish) {
            EventStatus oldStatus = event.getStatus();
            event.setStatus(EventStatus.FINISHED);
            eventRepository.save(event);

            sendStatusChangeMessage(event, oldStatus);
        }


        log.info("EventStatusScheduledUpdater finished");
    }

    private void sendStatusChangeMessage(EventEntity event, EventStatus oldStatus) {
        List<ChangeItem> changes = List.of(
                ChangeItem.builder()
                        .field("status")
                        .oldValue(oldStatus.name())
                        .newValue(event.getStatus().name())
                        .build()
        );

        List<Long> subscribers = eventRegistrationRepository.findByEventId(event.getId())
                .stream()
                .map(r -> r.getUser().getId())
                .collect(Collectors.toList());

        EventChangeMessage message = EventChangeMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .eventType(EventType.EVENT_STATUS_CHANGED)
                .eventId(event.getId())
                .eventName(event.getName())
                .occurredAt(LocalDateTime.now())
                .ownerId(event.getUser().getId())
                .changedById(null)
                .subscribers(subscribers)
                .changes(changes)
                .comment("Status changed by scheduler")
                .build();

        // 4. Отправляем
        eventChangeSender.sendEventChanges(message);
        log.info("Sent status change message for eventId={}: {} → {}",
                event.getId(), oldStatus, event.getStatus());
    }
}
