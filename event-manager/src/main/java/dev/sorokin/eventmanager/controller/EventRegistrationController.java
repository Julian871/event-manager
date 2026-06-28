package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.domain.Event;
import dev.sorokin.eventmanager.dto.response.EventResponse;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.service.EventRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/registrations")
@RequiredArgsConstructor
public class EventRegistrationController {

    private final EventRegistrationService eventRegistrationService;
    private final EventMapper eventMapper;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/{eventId}")
    public ResponseEntity<Void> eventRegistration(@PathVariable Long eventId) {
        eventRegistrationService.eventRegistration(eventId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/cancel/{eventId}")
    public ResponseEntity<Void> cancelEventRegistration(@PathVariable Long eventId) {
        eventRegistrationService.cancelEventRegistration(eventId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/my")
    public ResponseEntity<List<EventResponse>> getMyEventRegistration() {
        List<Event> events = eventRegistrationService.getMyRegistrations();

        return ResponseEntity.status(HttpStatus.OK).body(eventMapper.toResponseFromEvent(events));
    }
}
