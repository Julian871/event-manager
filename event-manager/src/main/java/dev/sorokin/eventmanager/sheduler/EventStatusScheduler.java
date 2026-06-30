package dev.sorokin.eventmanager.sheduler;

import dev.sorokin.eventmanager.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class EventStatusScheduler {

    private final static Logger log = LoggerFactory.getLogger(EventStatusScheduler.class);

    private final EventRepository eventRepository;

    public EventStatusScheduler(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(cron = "${event.stats.cron}")
    public void updateEventsStatus() {
        log.info("EventStatusScheduler started");

        eventRepository.updateEventStatusToStart();

        eventRepository.updateEventStatusToFinish();
        log.info("EventStatusScheduledUpdater finished");
    }
}
