package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.domain.Event;
import dev.sorokin.eventmanager.dto.request.EventCreateRequest;
import dev.sorokin.eventmanager.dto.request.EventSearchRequest;
import dev.sorokin.eventmanager.dto.request.EventUpdateRequest;
import dev.sorokin.eventmanager.dto.response.EventResponse;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventCreateRequest request) {
        Event event = eventService.createEvent(eventMapper.toDomainFromCreate(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(eventMapper.toResponseFromEvent(event));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEventById(eventId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long eventId) {
        Event event = eventService.getEventById(eventId);

        return ResponseEntity.status(HttpStatus.OK).body(eventMapper.toResponseFromEvent(event));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long eventId,
                                                     @Valid @RequestBody EventUpdateRequest request) {
        Event event = eventService.updateEventById(eventId, eventMapper.toDomainFromUpdate(request));

        return ResponseEntity.status(HttpStatus.OK).body(eventMapper.toResponseFromEvent(event));
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/my")
    public ResponseEntity<List<EventResponse>> getMyEvents() {
        List<Event> events = eventService.getMyEvents();

        return ResponseEntity.status(HttpStatus.OK).body(eventMapper.toResponseFromEvent(events));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/search")
    public ResponseEntity<List<EventResponse>> searchEvents(@Valid @RequestBody EventSearchRequest request) {
        List<Event> events = eventService.searchEvent(eventMapper.toEventSearchFromRequest(request));

        return ResponseEntity.status(HttpStatus.OK).body(eventMapper.toResponseFromEvent(events));
    }
}
