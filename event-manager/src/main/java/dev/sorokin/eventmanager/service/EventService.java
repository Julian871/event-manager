package dev.sorokin.eventmanager.service;


import dev.sorokin.eventmanager.domain.Event;
import dev.sorokin.eventmanager.domain.EventSearch;

import java.util.List;

public interface EventService {

    Event createEvent(Event event);

    void deleteEventById(Long eventId);

    Event getEventById(Long eventId);

    Event updateEventById(Long eventId, Event event);

    List<Event> searchEvent(EventSearch eventSearch);

    List<Event> getMyEvents();
}
