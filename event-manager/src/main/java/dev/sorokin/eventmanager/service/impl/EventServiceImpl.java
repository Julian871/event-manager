package dev.sorokin.eventmanager.service.impl;

import dev.sorokin.eventmanager.domain.Event;
import dev.sorokin.eventmanager.domain.EventSearch;
import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.entity.UserAccountEntity;
import dev.sorokin.eventmanager.enums.EventStatus;
import dev.sorokin.eventmanager.enums.UserRole;
import dev.sorokin.eventmanager.exception.ApiException;
import dev.sorokin.eventmanager.kafka.EventChangeSender;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.repository.EventRegistrationRepository;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.repository.LocationRepository;
import dev.sorokin.eventmanager.repository.UserRepository;
import dev.sorokin.eventmanager.service.EventService;
import dev.sorokin.eventmanager.util.SecurityUtil;
import dev.sorokin.kafka.ChangeItem;
import dev.sorokin.kafka.EventChangeMessage;
import dev.sorokin.kafka.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static dev.sorokin.eventmanager.redis.CacheConstants.CACHE_VALUE_EVENT;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper;
    private final SecurityUtil securityUtil;
    private final EventChangeSender eventChangeSender;


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

    @CacheEvict(
            value = CACHE_VALUE_EVENT,
            key = "'id:' + #eventId"
    )
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

    @Cacheable(
            value = CACHE_VALUE_EVENT,
            key = "'id:' + #eventId"
    )
    @Override
    public Event getEventById(Long eventId) {
        EventEntity event = eventRepository.findById(eventId).orElseThrow(
                () -> new ApiException("Event not found", HttpStatus.NOT_FOUND)
        );

        return eventMapper.toEventFromEntity(event, event.getUser().getId(), event.getLocation().getId());
    }

    //TODO: Outbox Pattern for production
    @CacheEvict(
            value = CACHE_VALUE_EVENT,
            key = "'id:' + #eventId"
    )
    @Override
    @Transactional
    public Event updateEventById(Long eventId, Event event) {

        EventEntity oldEventEntity = eventRepository.findById(eventId).orElseThrow(
                () -> new ApiException("Event not found", HttpStatus.NOT_FOUND)
        );

        checkCredentials(oldEventEntity.getUser().getLogin());

        if(oldEventEntity.getStatus() != EventStatus.WAIT_START)
            throw new ApiException("Event started or finished or canceled", HttpStatus.BAD_REQUEST);

        LocationEntity locationEntity = null;

        if (event.locationId() != null) {
            locationEntity = locationRepository.findById(event.locationId()).orElseThrow(
                    () -> new ApiException("Location not found", HttpStatus.NOT_FOUND)
            );

            if (event.maxPlaces() != null && locationEntity.getCapacity() < event.maxPlaces()) {
                throw new ApiException("Capacity is insufficient", HttpStatus.BAD_REQUEST);
            }
        }

        EventEntity oldEventCopy = copyEventEntity(oldEventEntity);

        EventEntity updatedEvent = eventMapper.toEntityFromUpdate(event, oldEventEntity, locationEntity);

        eventRepository.save(updatedEvent);

        List<ChangeItem> changes = collectChanges(oldEventCopy, updatedEvent);

        if(!changes.isEmpty()) {
            List<Long> subscribers = getSubscribers(eventId);

            Long currentUserId = getCurrentUserId();

            EventChangeMessage message = EventChangeMessage.builder()
                    .messageId(UUID.randomUUID().toString())
                    .eventType(EventType.EVENT_UPDATED)
                    .eventId(eventId)
                    .eventName(updatedEvent.getName())
                    .occurredAt(LocalDateTime.now())
                    .ownerId(updatedEvent.getUser().getId())
                    .changedById(currentUserId)
                    .subscribers(subscribers)
                    .changes(changes)
                    .comment("Event updated")
                    .build();

            eventChangeSender.sendEventChanges(message);
        }

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

    private EventEntity copyEventEntity(EventEntity original) {
        EventEntity copy = new EventEntity();
        copy.setId(original.getId());
        copy.setName(original.getName());
        copy.setStartAt(original.getStartAt());
        copy.setDurationMinutes(original.getDurationMinutes());
        copy.setMaxPlaces(original.getMaxPlaces());
        copy.setOccupiedPlaces(original.getOccupiedPlaces());
        copy.setCost(original.getCost());
        copy.setStatus(original.getStatus());
        copy.setUser(original.getUser());
        copy.setLocation(original.getLocation());
        return copy;
    }

    private List<ChangeItem> collectChanges(EventEntity oldEvent, EventEntity newEvent) {
        List<ChangeItem> changes = new ArrayList<>();

        if (!Objects.equals(oldEvent.getName(), newEvent.getName())) {
            changes.add(ChangeItem.builder()
                    .field("name")
                    .oldValue(oldEvent.getName())
                    .newValue(newEvent.getName())
                    .build());
        }

        if (!Objects.equals(oldEvent.getStartAt(), newEvent.getStartAt())) {
            changes.add(ChangeItem.builder()
                    .field("date")
                    .oldValue(oldEvent.getStartAt() != null ? oldEvent.getStartAt().toString() : null)
                    .newValue(newEvent.getStartAt() != null ? newEvent.getStartAt().toString() : null)
                    .build());
        }

        if (!Objects.equals(oldEvent.getMaxPlaces(), newEvent.getMaxPlaces())) {
            changes.add(ChangeItem.builder()
                    .field("maxPlaces")
                    .oldValue(String.valueOf(oldEvent.getMaxPlaces()))
                    .newValue(String.valueOf(newEvent.getMaxPlaces()))
                    .build());
        }

        if (!Objects.equals(oldEvent.getCost(), newEvent.getCost())) {
            changes.add(ChangeItem.builder()
                    .field("cost")
                    .oldValue(String.valueOf(oldEvent.getCost()))
                    .newValue(String.valueOf(newEvent.getCost()))
                    .build());
        }

        if (!Objects.equals(oldEvent.getDurationMinutes(), newEvent.getDurationMinutes())) {
            changes.add(ChangeItem.builder()
                    .field("duration")
                    .oldValue(String.valueOf(oldEvent.getDurationMinutes()))
                    .newValue(String.valueOf(newEvent.getDurationMinutes()))
                    .build());
        }

        if (oldEvent.getLocation() != null && newEvent.getLocation() != null) {
            if (!Objects.equals(oldEvent.getLocation().getId(), newEvent.getLocation().getId())) {
                changes.add(ChangeItem.builder()
                        .field("locationId")
                        .oldValue(String.valueOf(oldEvent.getLocation().getId()))
                        .newValue(String.valueOf(newEvent.getLocation().getId()))
                        .build());
            }
        }

        if (!Objects.equals(oldEvent.getStatus(), newEvent.getStatus())) {
            changes.add(ChangeItem.builder()
                    .field("status")
                    .oldValue(oldEvent.getStatus() != null ? oldEvent.getStatus().name() : null)
                    .newValue(newEvent.getStatus() != null ? newEvent.getStatus().name() : null)
                    .build());
        }

        return changes;
    }

    private List<Long> getSubscribers(Long eventId) {
        return eventRegistrationRepository.findByEventId(eventId)
                .stream()
                .map(registration -> registration.getUser().getId())
                .collect(Collectors.toList());
    }

    private Long getCurrentUserId() {
        UserAccountEntity user = userRepository.findByLogin(securityUtil.getCurrentUserLogin()).orElseThrow(
                () -> new ApiException("User not found", HttpStatus.NOT_FOUND)
        );

        return user.getId();
    }
}
