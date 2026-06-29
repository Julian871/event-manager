package dev.sorokin.eventmanager.service.impl;

import dev.sorokin.eventmanager.domain.Event;
import dev.sorokin.eventmanager.domain.EventSearch;
import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.entity.UserAccountEntity;
import dev.sorokin.eventmanager.enums.EventStatus;
import dev.sorokin.eventmanager.enums.UserRole;
import dev.sorokin.eventmanager.exception.ApiException;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.repository.LocationRepository;
import dev.sorokin.eventmanager.repository.UserRepository;
import dev.sorokin.eventmanager.service.EventService;
import dev.sorokin.eventmanager.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper;
    private final SecurityUtil securityUtil;


    @Override
    public Event createEvent(Event event) {
        String login = securityUtil.getCurrentUserLogin();

        UserAccountEntity userAccountEntity = userRepository.findByLogin(login).orElseThrow(
                () -> new ApiException("Owner not found", HttpStatus.NOT_FOUND)
        );

        LocationEntity locationEntity = locationRepository.findById(event.locationId()).orElseThrow(
                () -> new ApiException("Location not found", HttpStatus.NOT_FOUND)
        );

        if(locationEntity.getCapacity() < event.maxPlaces())
            throw new ApiException("Capacity is insufficient", HttpStatus.BAD_REQUEST);

        EventEntity entity = eventMapper.toEntityFromCreate(event, userAccountEntity, locationEntity);
        eventRepository.save(entity);

        return eventMapper.toEventFromEntity(entity, userAccountEntity.getId(), event.locationId());
    }

    @Override
    public void deleteEventById(Long eventId) {
        EventEntity event = eventRepository.findById(eventId).orElseThrow(
                () -> new ApiException("Event not found", HttpStatus.NOT_FOUND)
        );

        checkCredentials(event.getUser().getLogin());

        if(event.getStatus() == EventStatus.STARTED)
            throw new ApiException("The event has already started", HttpStatus.BAD_REQUEST);

        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);
    }

    @Override
    public Event getEventById(Long eventId) {
        EventEntity event = eventRepository.findById(eventId).orElseThrow(
                () -> new ApiException("Event not found", HttpStatus.NOT_FOUND)
        );

        return eventMapper.toEventFromEntity(event, event.getUser().getId(), event.getLocation().getId());
    }

    @Override
    @Transactional
    public Event updateEventById(Long eventId, Event event) {

        EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(
                () -> new ApiException("Event not found", HttpStatus.NOT_FOUND)
        );

        checkCredentials(eventEntity.getUser().getLogin());

        LocationEntity locationEntity = null;

        if (event.locationId() != null) {
            locationEntity = locationRepository.findById(event.locationId()).orElseThrow(
                    () -> new ApiException("Location not found", HttpStatus.NOT_FOUND)
            );

            if (event.maxPlaces() != null && locationEntity.getCapacity() < event.maxPlaces()) {
                throw new ApiException("Capacity is insufficient", HttpStatus.BAD_REQUEST);
            }
        }

        EventEntity updatedEvent = eventMapper.toEntityFromUpdate(event, eventEntity, locationEntity);

        eventRepository.save(updatedEvent);

        return eventMapper.toEventFromEntity(
                updatedEvent,
                updatedEvent.getUser().getId(),
                updatedEvent.getLocation().getId()
        );
    }

    @Override
    public List<Event> searchEvent(EventSearch eventSearch) {

        List<EventEntity> events = eventRepository.searchEvents(
                eventSearch.name(),
                eventSearch.minPlaces(),
                eventSearch.maxPlaces(),
                eventSearch.dateStartAfter(),
                eventSearch.dateStartBefore(),
                eventSearch.costMin(),
                eventSearch.costMax(),
                eventSearch.durationMin(),
                eventSearch.durationMax(),
                eventSearch.locationId(),
                eventSearch.eventStatus() != null ? eventSearch.eventStatus().name() : null
        );
        return events.stream()
                .map(event -> eventMapper.toEventFromEntity(
                        event,
                        event.getUser().getId(),
                        event.getLocation().getId()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getMyEvents() {
        String login = securityUtil.getCurrentUserLogin();
        UserAccountEntity user = userRepository.findUserWithEventsByLogin(login).orElseThrow(
                () -> new ApiException("User not found", HttpStatus.NOT_FOUND)
        );

        return user.getEvents()
                .stream()
                .map(event -> eventMapper.toEventFromEntity(
                        event,
                        user.getId(),
                        event.getLocation() != null ? event.getLocation().getId() : null
                ))
                .collect(Collectors.toList());
    }

    private void checkCredentials(String login) {
        String userLogin = securityUtil.getCurrentUserLogin();
        String userRole = securityUtil.getCurrentUserRole();

        if(
                !login.equals(userLogin)
                        && !userRole.equals(UserRole.ADMIN.name()))
            throw new ApiException("Action is not available", HttpStatus.FORBIDDEN);
    }
}
