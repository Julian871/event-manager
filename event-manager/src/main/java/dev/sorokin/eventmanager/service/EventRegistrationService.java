package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.domain.Event;

import java.util.List;

public interface EventRegistrationService {

    void eventRegistration(Long eventId);

    void cancelEventRegistration(Long eventId);

    List<Event> getMyRegistrations();
}
